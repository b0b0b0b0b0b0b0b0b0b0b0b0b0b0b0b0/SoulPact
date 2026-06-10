package bm.b0b0b0.SoulPact.gladiator.command.admin;

import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorTextParser;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaPoint;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import bm.b0b0b0.SoulPact.gladiator.service.WandSelectionService;
import bm.b0b0b0.SoulPact.gladiator.util.LocationCodec;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ArenaAdminHandler {

    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;
    private final WandSelectionService selectionService;

    public ArenaAdminHandler(GladiatorMessages messages, ArenaCatalog catalog, WandSelectionService selectionService) {
        this.messages = messages;
        this.catalog = catalog;
        this.selectionService = selectionService;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messages.send(sender, "gladiator.usage.arena", Map.of());
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "create" -> create(sender, args);
            case "delete" -> delete(sender, args);
            case "list" -> list(sender);
            case "point" -> point(sender, args);
            case "toggle" -> toggle(sender, args);
            case "seticon" -> setIcon(sender, args);
            case "settag" -> setTag(sender, args);
            case "desc" -> setDescription(sender, args);
            default -> messages.send(sender, "gladiator.usage.arena", Map.of());
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "gladiator.usage.arena", Map.of());
            return;
        }
        String name = args[2];
        if (!catalog.create(name)) {
            messages.send(sender, "gladiator.error.arena-exists", Map.of("arena", name));
            return;
        }
        if (sender instanceof Player player) {
            selectionService.region(player.getUniqueId()).ifPresent(region -> {
                catalog.mutate(name, arena -> arena.withRegion(region.encode()));
                selectionService.clear(player.getUniqueId());
                messages.send(player, "gladiator.admin.region-applied", Map.of("arena", name));
            });
        }
        messages.send(sender, "gladiator.admin.arena-created", Map.of("arena", name));
    }

    private void delete(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "gladiator.usage.arena", Map.of());
            return;
        }
        if (catalog.delete(args[2])) {
            messages.send(sender, "gladiator.admin.arena-deleted", Map.of("arena", args[2]));
        } else {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
        }
    }

    private void list(CommandSender sender) {
        messages.send(sender, "gladiator.admin.arena-list-header", Map.of("count", String.valueOf(catalog.size())));
        for (Arena arena : catalog.all()) {
            String stateKey = arena.enabled() ? "gladiator.place.enabled" : "gladiator.place.disabled";
            sender.sendMessage(GladiatorTextParser.parse(messages.resolveDefault("gladiator.admin.arena-list-line", Map.of(
                    "arena", arena.name(),
                    "state", messages.resolveDefault(stateKey),
                    "tag", arena.tag().isBlank() ? messages.resolveDefault("gladiator.place.none") : arena.tag()
            ))));
        }
    }

    private void point(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "gladiator.error.players-only", Map.of());
            return;
        }
        if (args.length < 4) {
            messages.send(sender, "gladiator.usage.arena-point", Map.of());
            return;
        }
        ArenaPoint point;
        try {
            point = ArenaPoint.valueOf(args[3].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            messages.send(sender, "gladiator.usage.arena-point", Map.of());
            return;
        }
        String encoded = LocationCodec.encode(player.getLocation());
        Optional<Arena> updated = catalog.mutate(args[2], arena -> arena.withPoint(point, encoded));
        if (updated.isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        messages.send(player, "gladiator.admin.point-set", Map.of("arena", args[2], "point", point.name()));
    }

    private void toggle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "gladiator.usage.arena", Map.of());
            return;
        }
        Optional<Arena> updated = catalog.mutate(args[2], arena -> arena.withEnabled(!arena.enabled()));
        if (updated.isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        String stateKey = updated.get().enabled() ? "gladiator.place.enabled" : "gladiator.place.disabled";
        messages.send(sender, "gladiator.admin.arena-toggled", Map.of(
                "arena", args[2],
                "state", messages.resolveDefault(stateKey)
        ));
    }

    private void setIcon(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "gladiator.usage.arena-seticon", Map.of());
            return;
        }
        Material material = Material.matchMaterial(args[3]);
        if (material == null) {
            messages.send(sender, "gladiator.error.unknown-material", Map.of("material", args[3]));
            return;
        }
        Optional<Arena> updated = catalog.mutate(args[2], arena -> arena.withIcon(material.name()));
        if (updated.isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        messages.send(sender, "gladiator.admin.icon-set", Map.of("arena", args[2], "material", material.name()));
    }

    private void setTag(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "gladiator.usage.arena-settag", Map.of());
            return;
        }
        String tag = joinTail(args, 3);
        Optional<Arena> updated = catalog.mutate(args[2], arena -> arena.withTag(tag));
        if (updated.isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        messages.send(sender, "gladiator.admin.tag-set", Map.of("arena", args[2], "tag", tag));
    }

    private void setDescription(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "gladiator.usage.arena-desc", Map.of());
            return;
        }
        String description = joinTail(args, 3);
        Optional<Arena> updated = catalog.mutate(args[2], arena -> arena.withDescription(description));
        if (updated.isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        messages.send(sender, "gladiator.admin.desc-set", Map.of("arena", args[2], "description", description));
    }

    private String joinTail(String[] args, int fromIndex) {
        return String.join(" ", Arrays.copyOfRange(args, fromIndex, args.length));
    }
}
