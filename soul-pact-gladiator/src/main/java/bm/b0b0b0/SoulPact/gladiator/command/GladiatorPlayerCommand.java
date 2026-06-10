package bm.b0b0b0.SoulPact.gladiator.command;

import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorGuiService;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorTextParser;
import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorActionResult;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public final class GladiatorPlayerCommand implements TabExecutor {

    private static final List<String> SUBCOMMANDS = List.of("help", "join", "leave", "watch");

    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;
    private final GladiatorEventService eventService;
    private final GladiatorGuiService guiService;

    public GladiatorPlayerCommand(
            GladiatorMessages messages,
            ArenaCatalog catalog,
            GladiatorEventService eventService,
            GladiatorGuiService guiService
    ) {
        this.messages = messages;
        this.catalog = catalog;
        this.eventService = eventService;
        this.guiService = guiService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "gladiator.error.players-only", Map.of());
            return true;
        }
        if (args.length == 0) {
            guiService.open(player);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "help" -> sendHelp(player);
            case "join" -> join(player, args);
            case "leave" -> leave(player);
            case "watch" -> watch(player, args);
            default -> sendHelp(player);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(SUBCOMMANDS, args[0]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("join") || args[0].equalsIgnoreCase("watch"))) {
            return filter(catalog.all().stream().map(Arena::name).toList(), args[1]);
        }
        return List.of();
    }

    private void sendHelp(Player player) {
        for (String line : messages.resolveList(player, "gladiator.help.player", Map.of())) {
            player.sendMessage(GladiatorTextParser.parse(line));
        }
    }

    private void join(Player player, String[] args) {
        if (args.length < 2) {
            messages.send(player, "gladiator.usage.join");
            return;
        }
        String arenaName = args[1];
        eventService.requestJoin(player, arenaName, result -> notify(player, arenaName, result));
    }

    private void leave(Player player) {
        notify(player, "", eventService.leave(player));
    }

    private void watch(Player player, String[] args) {
        if (args.length < 2) {
            messages.send(player, "gladiator.usage.watch");
            return;
        }
        notify(player, args[1], eventService.watch(player, args[1]));
    }

    private void notify(Player player, String arenaName, GladiatorActionResult result) {
        Map<String, String> placeholders = Map.of("arena", arenaName);
        switch (result) {
            case JOINED -> messages.send(player, "gladiator.event.you-joined", placeholders);
            case LEFT -> messages.send(player, "gladiator.event.you-left", placeholders);
            case WATCHING -> messages.send(player, "gladiator.event.you-watching", placeholders);
            case NO_EVENT -> messages.send(player, "gladiator.error.no-event", placeholders);
            case FIGHT_ALREADY_STARTED -> messages.send(player, "gladiator.error.fight-started", placeholders);
            case ALREADY_JOINED -> messages.send(player, "gladiator.error.already-joined", placeholders);
            case NOT_IN_CLAN -> messages.send(player, "gladiator.error.not-in-clan", placeholders);
            case NOT_IN_EVENT -> messages.send(player, "gladiator.error.not-in-event", placeholders);
            case NO_WATCH_POINT -> messages.send(player, "gladiator.error.no-watch-point", placeholders);
            default -> messages.send(player, "gladiator.error.no-event", placeholders);
        }
    }

    private List<String> filter(List<String> options, String prefix) {
        String lowered = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lowered)).toList();
    }
}
