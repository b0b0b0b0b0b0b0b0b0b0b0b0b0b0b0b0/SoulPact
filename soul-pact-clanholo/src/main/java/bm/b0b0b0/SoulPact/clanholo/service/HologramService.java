package bm.b0b0b0.SoulPact.clanholo.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import bm.b0b0b0.SoulPact.clanholo.gate.ClanBaseLocationGate;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloMessages;
import bm.b0b0b0.SoulPact.clanholo.model.ClanHologram;
import bm.b0b0b0.SoulPact.clanholo.render.ClanHologramRenderer;
import bm.b0b0b0.SoulPact.clanholo.repository.HologramRepository;
import bm.b0b0b0.SoulPact.clanholo.validation.ContentValidator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class HologramService {

    private static final Pattern NAME_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private final SoulPactApi api;
    private final HologramRepository repository;
    private final ClanHologramRenderer renderer;
    private final ClanBaseLocationGate baseGate;
    private final ContentValidator contentValidator;
    private final HologramSessionStore sessionStore;
    private final HologramLimitResolver limitResolver;
    private final HologramTemplateService templateService;
    private final HologramPlaceholderBuilder placeholderBuilder;
    private final ClanHoloMessages messages;
    private final Supplier<ClanHoloConfig> configSupplier;

    public HologramService(
            SoulPactApi api,
            HologramRepository repository,
            ClanHologramRenderer renderer,
            ClanBaseLocationGate baseGate,
            ContentValidator contentValidator,
            HologramSessionStore sessionStore,
            HologramLimitResolver limitResolver,
            HologramTemplateService templateService,
            HologramPlaceholderBuilder placeholderBuilder,
            ClanHoloMessages messages,
            Supplier<ClanHoloConfig> configSupplier
    ) {
        this.api = api;
        this.repository = repository;
        this.renderer = renderer;
        this.baseGate = baseGate;
        this.contentValidator = contentValidator;
        this.sessionStore = sessionStore;
        this.limitResolver = limitResolver;
        this.templateService = templateService;
        this.placeholderBuilder = placeholderBuilder;
        this.messages = messages;
        this.configSupplier = configSupplier;
    }

    public CompletableFuture<Void> create(Player player, String name, String template, boolean admin) {
        if (!validName(name)) {
            notify(player, "clanholo.error.invalid-name");
            return CompletableFuture.completedFuture(null);
        }
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(player, "clanholo.error.not-in-clan");
                return CompletableFuture.completedFuture(null);
            }
            ClanSnapshot clan = clanOptional.get();
            return authorize(player, clan.id(), admin).thenCompose(allowed -> {
                if (!allowed) {
                    notify(player, "clanholo.error.no-permission");
                    return CompletableFuture.completedFuture(null);
                }
                return validateLocation(player, clan.id(), admin).thenCompose(locationOk -> {
                    if (!locationOk) {
                        return CompletableFuture.completedFuture(null);
                    }
                    return repository.countByClanId(clan.id()).thenCompose(count -> {
                        int limit = limitResolver.resolve(player);
                        if (count >= limit) {
                            notify(player, "clanholo.error.limit", Map.of("max", String.valueOf(limit)));
                            return CompletableFuture.completedFuture(null);
                        }
                        List<String> lines = templateService.resolveTemplateLines(template, clan);
                        Location location = player.getLocation();
                        ClanHologram hologram = new ClanHologram(
                                0L,
                                clan.id(),
                                name,
                                location.getWorld().getName(),
                                location.getX(),
                                location.getY() + 1.0D,
                                location.getZ(),
                                player.getUniqueId(),
                                player.getName(),
                                template == null ? "" : template,
                                System.currentTimeMillis(),
                                lines
                        );
                        return repository.create(hologram).thenApply(created -> {
                            api.scheduler().runSync(() -> {
                                sessionStore.select(player.getUniqueId(), created.id());
                                renderHologram(created);
                                notify(player, "clanholo.create.success", Map.of("name", name));
                            });
                            return null;
                        });
                    });
                });
            });
        });
    }

    public CompletableFuture<Void> delete(Player player, String name, boolean admin) {
        return resolveOwnedHologram(player, name, admin).thenCompose(hologramOptional -> {
            if (hologramOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanHologram hologram = hologramOptional.get();
            return repository.delete(hologram.id()).thenAccept(deleted -> api.scheduler().runSync(() -> {
                renderer.removeEntities(hologram);
                sessionStore.clear(player.getUniqueId(), hologram.id());
                notify(player, "clanholo.delete.success", Map.of("name", hologram.name()));
            }));
        });
    }

    public CompletableFuture<Void> addLine(Player player, String content, boolean admin) {
        return resolveSelected(player, admin).thenCompose(hologramOptional -> {
            if (hologramOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanHologram hologram = hologramOptional.get();
            return authorize(player, hologram.clanId(), admin).thenCompose(allowed -> {
                if (!allowed) {
                    notify(player, "clanholo.error.no-permission");
                    return CompletableFuture.completedFuture(null);
                }
                if (hologram.lines().size() >= configSupplier.get().maxLines()) {
                    notify(player, "clanholo.error.max-lines", Map.of(
                            "max", String.valueOf(configSupplier.get().maxLines())
                    ));
                    return CompletableFuture.completedFuture(null);
                }
                Optional<String> validationError = contentValidator.validateLine(content);
                if (validationError.isPresent()) {
                    notify(player, validationError.get());
                    return CompletableFuture.completedFuture(null);
                }
                List<String> lines = new ArrayList<>(hologram.lines());
                lines.add(content.trim());
                return persistAndRender(player, hologram, lines, "clanholo.add.success");
            });
        });
    }

    public CompletableFuture<Void> removeLine(Player player, int lineNumber, boolean admin) {
        return resolveSelected(player, admin).thenCompose(hologramOptional -> {
            if (hologramOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanHologram hologram = hologramOptional.get();
            return authorize(player, hologram.clanId(), admin).thenCompose(allowed -> {
                if (!allowed) {
                    notify(player, "clanholo.error.no-permission");
                    return CompletableFuture.completedFuture(null);
                }
                int index = lineNumber - 1;
                if (index < 0 || index >= hologram.lines().size()) {
                    notify(player, "clanholo.error.line-not-found");
                    return CompletableFuture.completedFuture(null);
                }
                List<String> lines = new ArrayList<>(hologram.lines());
                lines.remove(index);
                return persistAndRender(player, hologram, lines, "clanholo.remove.success");
            });
        });
    }

    public CompletableFuture<Void> editLine(Player player, int lineNumber, String content, boolean admin) {
        return resolveSelected(player, admin).thenCompose(hologramOptional -> {
            if (hologramOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanHologram hologram = hologramOptional.get();
            return authorize(player, hologram.clanId(), admin).thenCompose(allowed -> {
                if (!allowed) {
                    notify(player, "clanholo.error.no-permission");
                    return CompletableFuture.completedFuture(null);
                }
                int index = lineNumber - 1;
                if (index < 0 || index >= hologram.lines().size()) {
                    notify(player, "clanholo.error.line-not-found");
                    return CompletableFuture.completedFuture(null);
                }
                Optional<String> validationError = contentValidator.validateLine(content);
                if (validationError.isPresent()) {
                    notify(player, validationError.get());
                    return CompletableFuture.completedFuture(null);
                }
                List<String> lines = new ArrayList<>(hologram.lines());
                lines.set(index, content.trim());
                return persistAndRender(player, hologram, lines, "clanholo.edit.success");
            });
        });
    }

    public CompletableFuture<Void> list(Player player) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(player, "clanholo.error.not-in-clan");
                return CompletableFuture.completedFuture(null);
            }
            return repository.findByClanId(clanOptional.get().id()).thenAccept(holograms -> api.scheduler().runSync(() -> {
                if (holograms.isEmpty()) {
                    notify(player, "clanholo.list.empty");
                    return;
                }
                notify(player, "clanholo.list.header", Map.of("count", String.valueOf(holograms.size())));
                for (ClanHologram hologram : holograms) {
                    notify(player, "clanholo.list.entry", Map.of(
                            "id", String.valueOf(hologram.id()),
                            "name", hologram.name(),
                            "world", hologram.world(),
                            "x", String.valueOf((int) hologram.x()),
                            "y", String.valueOf((int) hologram.y()),
                            "z", String.valueOf((int) hologram.z()),
                            "lines", String.valueOf(hologram.lines().size())
                    ));
                }
            }));
        });
    }

    public CompletableFuture<Void> select(Player player, String name) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(player, "clanholo.error.not-in-clan");
                return CompletableFuture.completedFuture(null);
            }
            return repository.findByName(clanOptional.get().id(), name).thenAccept(hologramOptional ->
                    api.scheduler().runSync(() -> {
                        if (hologramOptional.isEmpty()) {
                            notify(player, "clanholo.error.not-found", Map.of("name", name));
                            return;
                        }
                        sessionStore.select(player.getUniqueId(), hologramOptional.get().id());
                        notify(player, "clanholo.select.success", Map.of("name", name));
                    })
            );
        });
    }

    public CompletableFuture<Void> refresh(Player player, String name, boolean admin) {
        return resolveOwnedHologram(player, name, admin).thenCompose(hologramOptional -> {
            if (hologramOptional.isEmpty()) {
                return CompletableFuture.completedFuture(null);
            }
            ClanHologram hologram = hologramOptional.get();
            if (hologram.template() == null || hologram.template().isBlank()) {
                notify(player, "clanholo.error.no-template");
                return CompletableFuture.completedFuture(null);
            }
            return api.scheduler().supplyAsync(() -> placeholderBuilder.loadClan(hologram.clanId())).thenCompose(clanOptional -> {
                if (clanOptional.isEmpty()) {
                    return CompletableFuture.completedFuture(null);
                }
                List<String> lines = templateService.resolveTemplateLines(hologram.template(), clanOptional.get());
                return persistAndRender(player, hologram, lines, "clanholo.refresh.success");
            });
        });
    }

    public CompletableFuture<Void> destroyClanHolograms(long clanId) {
        return repository.findByClanId(clanId).thenCompose(holograms -> {
            api.scheduler().runSync(() -> holograms.forEach(renderer::removeEntities));
            return repository.deleteByClanId(clanId).thenApply(ignored -> null);
        });
    }

    public void renderAll(List<ClanHologram> holograms) {
        for (ClanHologram hologram : holograms) {
            renderHologram(hologram);
        }
    }

    public void renderHologram(ClanHologram hologram) {
        api.scheduler().supplyAsync(() -> placeholderBuilder.loadClan(hologram.clanId())).thenAccept(clanOptional -> {
            Map<String, String> placeholders = clanOptional.map(placeholderBuilder::build).orElse(Map.of());
            List<String> lines = resolveLines(hologram, clanOptional.orElse(null), placeholders);
            String ownerLine = applyOwnerLine(hologram, placeholders);
            api.scheduler().runSync(() -> renderer.render(hologram, lines, ownerLine));
        });
    }

    private CompletableFuture<Void> persistAndRender(
            Player player,
            ClanHologram hologram,
            List<String> lines,
            String messageKey
    ) {
        return repository.replaceLines(hologram.id(), lines).thenAccept(ignored -> api.scheduler().runSync(() -> {
            ClanHologram updated = new ClanHologram(
                    hologram.id(),
                    hologram.clanId(),
                    hologram.name(),
                    hologram.world(),
                    hologram.x(),
                    hologram.y(),
                    hologram.z(),
                    hologram.creatorId(),
                    hologram.creatorName(),
                    "",
                    hologram.createdAt(),
                    List.copyOf(lines)
            );
            renderHologram(updated);
            notify(player, messageKey, Map.of("name", hologram.name()));
        }));
    }

    private List<String> resolveLines(ClanHologram hologram, ClanSnapshot clan, Map<String, String> placeholders) {
        if (hologram.template() != null && !hologram.template().isBlank()) {
            return applyPlaceholders(templateService.resolveTemplateLines(hologram.template(), clan), placeholders);
        }
        return applyPlaceholders(hologram.lines(), placeholders);
    }

    private String applyOwnerLine(ClanHologram hologram, Map<String, String> placeholders) {
        Map<String, String> ownerPlaceholders = new HashMap<>(placeholders);
        ownerPlaceholders.put("player", hologram.creatorName());
        String template = configSupplier.get().ownerLine();
        String resolved = template;
        for (Map.Entry<String, String> entry : ownerPlaceholders.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
            resolved = resolved.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return resolved;
    }

    private List<String> applyPlaceholders(List<String> lines, Map<String, String> placeholders) {
        List<String> resolved = new ArrayList<>();
        for (String line : lines) {
            String value = line;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                value = value.replace("{" + entry.getKey() + "}", entry.getValue());
                value = value.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            resolved.add(value);
        }
        return resolved;
    }

    private CompletableFuture<Boolean> validateLocation(Player player, long clanId, boolean admin) {
        if (admin) {
            return CompletableFuture.completedFuture(true);
        }
        ClanHoloConfig config = configSupplier.get();
        if (!config.requireClanBase()) {
            return CompletableFuture.completedFuture(true);
        }
        if (!baseGate.available()) {
            notify(player, "clanholo.error.worldguard-missing");
            return CompletableFuture.completedFuture(false);
        }
        Optional<Long> regionClanId = baseGate.findClanIdAt(player.getLocation());
        if (regionClanId.isEmpty() || regionClanId.get() != clanId) {
            notify(player, "clanholo.error.not-on-base");
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(true);
    }

    private CompletableFuture<Boolean> authorize(Player player, long clanId, boolean admin) {
        if (admin) {
            return CompletableFuture.completedFuture(true);
        }
        return api.clanAccess().hasPermission(clanId, player.getUniqueId(), configSupplier.get().clanPermissionKey());
    }

    private CompletableFuture<Optional<ClanHologram>> resolveOwnedHologram(Player player, String name, boolean admin) {
        if (admin) {
            return repository.findAll().thenApply(all -> all.stream()
                    .filter(h -> h.name().equalsIgnoreCase(name))
                    .findFirst()
                    .or(() -> all.stream().filter(h -> String.valueOf(h.id()).equals(name)).findFirst()));
        }
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(player, "clanholo.error.not-in-clan");
                return CompletableFuture.completedFuture(Optional.empty());
            }
            return repository.findByName(clanOptional.get().id(), name);
        }).thenApply(optional -> {
            if (optional.isEmpty()) {
                notify(player, "clanholo.error.not-found", Map.of("name", name));
            }
            return optional;
        });
    }

    private CompletableFuture<Optional<ClanHologram>> resolveSelected(Player player, boolean admin) {
        if (admin) {
            Long selectedId = sessionStore.selected(player.getUniqueId());
            if (selectedId != null) {
                return repository.findById(selectedId);
            }
            notify(player, "clanholo.error.no-selection");
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                notify(player, "clanholo.error.not-in-clan");
                return CompletableFuture.completedFuture(Optional.empty());
            }
            Long selectedId = sessionStore.selected(player.getUniqueId());
            if (selectedId != null) {
                return repository.findById(selectedId);
            }
            return repository.findByClanId(clanOptional.get().id()).thenApply(holograms -> {
                Location location = player.getLocation();
                double radius = configSupplier.get().selectRadius();
                Optional<ClanHologram> nearest = holograms.stream()
                        .filter(h -> location.getWorld() != null && h.world().equals(location.getWorld().getName()))
                        .filter(h -> location.distance(new Location(location.getWorld(), h.x(), h.y(), h.z())) <= radius)
                        .min(Comparator.comparingDouble(h ->
                                location.distance(new Location(location.getWorld(), h.x(), h.y(), h.z()))));
                if (nearest.isEmpty()) {
                    notify(player, "clanholo.error.no-selection");
                }
                return nearest;
            });
        });
    }

    private boolean validName(String name) {
        return name != null && !name.isBlank() && name.length() <= 32 && NAME_PATTERN.matcher(name).matches();
    }

    private void notify(Player player, String key) {
        notify(player, key, Map.of());
    }

    private void notify(Player player, String key, Map<String, String> placeholders) {
        api.scheduler().runSync(() -> messages.send(player, key, placeholders));
    }
}
