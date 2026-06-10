package bm.b0b0b0.SoulPact.gladiator.command;

import bm.b0b0b0.SoulPact.gladiator.command.admin.ArenaAdminHandler;
import bm.b0b0b0.SoulPact.gladiator.command.admin.RewardAdminHandler;
import bm.b0b0b0.SoulPact.gladiator.command.admin.SchedulerAdminHandler;
import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorTextParser;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaPoint;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorActionResult;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClanGladAdminCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS =
            List.of("help", "reload", "wand", "arena", "reward", "start", "stop", "view", "scheduler");
    private static final List<String> ARENA_ACTIONS =
            List.of("create", "delete", "list", "point", "toggle", "seticon", "settag", "desc");
    private static final List<String> REWARD_ACTIONS = List.of("list", "clean", "add");
    private static final List<String> SCHEDULER_ACTIONS = List.of("list", "remove", "create");

    private final Supplier<GladiatorConfig> configSupplier;
    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;
    private final GladiatorEventService eventService;
    private final ArenaAdminHandler arenaHandler;
    private final RewardAdminHandler rewardHandler;
    private final SchedulerAdminHandler schedulerHandler;
    private final Runnable reloadAction;

    public ClanGladAdminCommand(
            Supplier<GladiatorConfig> configSupplier,
            GladiatorMessages messages,
            ArenaCatalog catalog,
            GladiatorEventService eventService,
            ArenaAdminHandler arenaHandler,
            RewardAdminHandler rewardHandler,
            SchedulerAdminHandler schedulerHandler,
            Runnable reloadAction
    ) {
        this.configSupplier = configSupplier;
        this.messages = messages;
        this.catalog = catalog;
        this.eventService = eventService;
        this.arenaHandler = arenaHandler;
        this.rewardHandler = rewardHandler;
        this.schedulerHandler = schedulerHandler;
        this.reloadAction = reloadAction;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            messages.send(sender, "gladiator.error.no-permission", Map.of());
            return true;
        }
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "help" -> sendHelp(sender);
            case "reload" -> reload(sender);
            case "wand" -> giveWand(sender);
            case "arena" -> arenaHandler.handle(sender, args);
            case "reward" -> rewardHandler.handle(sender, args);
            case "scheduler" -> schedulerHandler.handle(sender, args);
            case "start" -> start(sender, args);
            case "stop" -> stop(sender, args);
            case "view" -> view(sender, args);
            default -> sendHelp(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!hasPermission(sender)) {
            return List.of();
        }
        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }
        String root = args[0].toLowerCase(Locale.ROOT);
        if (args.length == 2) {
            return switch (root) {
                case "arena" -> filter(ARENA_ACTIONS, args[1]);
                case "reward" -> filter(REWARD_ACTIONS, args[1]);
                case "scheduler" -> filter(SCHEDULER_ACTIONS, args[1]);
                case "start", "stop", "view" -> filter(arenaNames(), args[1]);
                default -> List.of();
            };
        }
        if (args.length == 3 && (root.equals("arena") || root.equals("reward") || root.equals("scheduler"))) {
            return filter(arenaNames(), args[2]);
        }
        if (args.length == 4 && root.equals("arena") && args[1].equalsIgnoreCase("point")) {
            return filter(List.of("SPAWN", "WATCH", "EXIT", "LOBBY"), args[3]);
        }
        if (args.length == 4 && root.equals("scheduler") && args[1].equalsIgnoreCase("create")) {
            return filter(List.of("DAILY", "WEEKLY"), args[3]);
        }
        return List.of();
    }

    private boolean hasPermission(CommandSender sender) {
        return !(sender instanceof Player player) || player.hasPermission(configSupplier.get().adminPermission());
    }

    private void sendHelp(CommandSender sender) {
        for (String line : messages.resolveList(null, "gladiator.help.admin", Map.of())) {
            sender.sendMessage(GladiatorTextParser.parse(line));
        }
    }

    private void reload(CommandSender sender) {
        reloadAction.run();
        messages.send(sender, "gladiator.admin.reloaded", Map.of());
    }

    private void giveWand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "gladiator.error.players-only", Map.of());
            return;
        }
        player.getInventory().addItem(new ItemStack(configSupplier.get().wandMaterial()));
        messages.send(player, "gladiator.admin.wand-given");
    }

    private void start(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messages.send(sender, "gladiator.usage.start", Map.of());
            return;
        }
        notifyControl(sender, args[1], eventService.start(args[1]));
    }

    private void stop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messages.send(sender, "gladiator.usage.stop", Map.of());
            return;
        }
        notifyControl(sender, args[1], eventService.stop(args[1]));
    }

    private void view(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "gladiator.error.players-only", Map.of());
            return;
        }
        if (args.length < 2) {
            messages.send(sender, "gladiator.usage.view", Map.of());
            return;
        }
        catalog.find(args[1]).ifPresentOrElse(arena -> {
            arena.location(ArenaPoint.WATCH)
                    .or(() -> arena.location(ArenaPoint.SPAWN))
                    .ifPresentOrElse(location -> {
                        player.teleport(location);
                        messages.send(player, "gladiator.admin.viewing", Map.of("arena", arena.name()));
                    }, () -> messages.send(player, "gladiator.error.no-watch-point", Map.of("arena", arena.name())));
        }, () -> messages.send(player, "gladiator.error.unknown-arena", Map.of("arena", args[1])));
    }

    private void notifyControl(CommandSender sender, String arenaName, GladiatorActionResult result) {
        Map<String, String> placeholders = Map.of("arena", arenaName);
        switch (result) {
            case STARTED -> messages.send(sender, "gladiator.admin.started", placeholders);
            case STOPPED -> messages.send(sender, "gladiator.admin.stopped", placeholders);
            case UNKNOWN_ARENA -> messages.send(sender, "gladiator.error.unknown-arena", placeholders);
            case ARENA_DISABLED -> messages.send(sender, "gladiator.error.arena-disabled", placeholders);
            case MISSING_POINTS -> messages.send(sender, "gladiator.error.missing-points", placeholders);
            case ALREADY_RUNNING -> messages.send(sender, "gladiator.error.already-running", placeholders);
            case NO_EVENT -> messages.send(sender, "gladiator.error.no-event", placeholders);
            default -> messages.send(sender, "gladiator.error.no-event", placeholders);
        }
    }

    private List<String> arenaNames() {
        return catalog.all().stream().map(Arena::name).toList();
    }

    private List<String> filter(List<String> options, String prefix) {
        String lowered = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lowered)).toList();
    }
}
