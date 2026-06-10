package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanHome;
import bm.b0b0b0.SoulPact.clan.repository.ClanHomeRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.placeholder.ClanPlaceholderInvalidatorRegistry;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class ClanHomeService {

    private static final Pattern HOME_NAME_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private final ClanRepository clanRepository;
    private final ClanHomeRepository homeRepository;
    private final ClanConfig clanConfig;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanHomeService(
            ClanRepository clanRepository,
            ClanHomeRepository homeRepository,
            ClanConfig clanConfig,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.homeRepository = homeRepository;
        this.clanConfig = clanConfig;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void sendUsageHint(Player player) {
        messageService.send(player, "clan.home.usage");
    }

    public void create(Player player, String name, String password) {
        if (!validateName(player, name)) {
            return;
        }
        Location location = player.getLocation();
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            Clan clan = leaderClanOrNotify(player, clanOptional.orElse(null));
            if (clan == null) {
                return CompletableFuture.completedFuture(null);
            }
            return homeRepository.countByClanId(clan.id()).thenCompose(count -> {
                if (count >= clanConfig.homesMax()) {
                    asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.home.limit", Map.of(
                            "max", String.valueOf(clanConfig.homesMax())
                    )));
                    return CompletableFuture.completedFuture(null);
                }
                ClanHome home = new ClanHome(
                        0L,
                        clan.id(),
                        name,
                        location.getWorld().getName(),
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch(),
                        ClanHomePasswordHasher.hash(password),
                        System.currentTimeMillis()
                );
                return homeRepository.create(home).thenApply(created -> {
                    asyncDatabaseExecutor.runSync(() -> {
                        if (created.isEmpty()) {
                            messageService.send(player, "clan.home.exists", Map.of("name", name));
                            return;
                        }
                        ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
                        messageService.send(player, "clan.home.created", Map.of("name", name));
                    });
                    return null;
                });
            });
        });
    }

    public void delete(Player player, String name) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            Clan clan = leaderClanOrNotify(player, clanOptional.orElse(null));
            if (clan == null) {
                return CompletableFuture.completedFuture(null);
            }
            return homeRepository.delete(clan.id(), name).thenApply(deleted -> {
                asyncDatabaseExecutor.runSync(() -> {
                    if (!deleted) {
                        messageService.send(player, "clan.home.not-found", Map.of("name", name));
                        return;
                    }
                    ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
                    messageService.send(player, "clan.home.deleted", Map.of("name", name));
                });
                return null;
            });
        });
    }

    public void list(Player player) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.home.not-in-clan"));
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            return homeRepository.findByClanId(clan.id()).thenApply(homes -> {
                asyncDatabaseExecutor.runSync(() -> presentList(player, homes));
                return null;
            });
        });
    }

    public void teleport(Player player, String name, String password) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.home.not-in-clan"));
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            return homeRepository.findByName(clan.id(), name).thenApply(homeOptional -> {
                asyncDatabaseExecutor.runSync(() -> {
                    if (homeOptional.isEmpty()) {
                        messageService.send(player, "clan.home.not-found", Map.of("name", name));
                        return;
                    }
                    ClanHome home = homeOptional.get();
                    if (home.passwordProtected() && !ClanHomePasswordHasher.matches(password, home.passwordHash())) {
                        messageService.send(player, "clan.home.wrong-password", Map.of("name", home.name()));
                        return;
                    }
                    performTeleport(player, home);
                });
                return null;
            });
        });
    }

    private boolean validateName(Player player, String name) {
        if (name == null || name.isBlank()) {
            sendUsageHint(player);
            return false;
        }
        if (name.length() > clanConfig.homeNameMaxLength() || !HOME_NAME_PATTERN.matcher(name).matches()) {
            messageService.send(player, "clan.home.invalid-name", Map.of(
                    "max", String.valueOf(clanConfig.homeNameMaxLength())
            ));
            return false;
        }
        return true;
    }

    private Clan leaderClanOrNotify(Player player, Clan clan) {
        if (clan == null) {
            asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.home.not-in-clan"));
            return null;
        }
        if (!clan.leaderId().equals(player.getUniqueId())) {
            asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.home.only-leader"));
            return null;
        }
        return clan;
    }

    private void presentList(Player player, List<ClanHome> homes) {
        if (homes.isEmpty()) {
            messageService.send(player, "clan.home.list-empty");
            return;
        }
        messageService.send(player, "clan.home.list-header", Map.of(
                "count", String.valueOf(homes.size()),
                "max", String.valueOf(clanConfig.homesMax())
        ));
        for (ClanHome home : homes) {
            String lockedKey = home.passwordProtected() ? "clan.home.list-locked" : "clan.home.list-open";
            messageService.send(player, "clan.home.list-entry", Map.of(
                    "name", home.name(),
                    "world", home.world(),
                    "x", String.valueOf((int) home.x()),
                    "y", String.valueOf((int) home.y()),
                    "z", String.valueOf((int) home.z()),
                    "locked", messageService.resolve(player, lockedKey)
            ));
        }
    }

    private void performTeleport(Player player, ClanHome home) {
        World world = Bukkit.getWorld(home.world());
        if (world == null) {
            messageService.send(player, "clan.home.world-missing", Map.of("world", home.world()));
            return;
        }
        Location target = new Location(world, home.x(), home.y(), home.z(), home.yaw(), home.pitch());
        player.teleportAsync(target).thenAccept(success -> {
            if (success) {
                messageService.send(player, "clan.home.teleported", Map.of("name", home.name()));
            }
        });
    }
}
