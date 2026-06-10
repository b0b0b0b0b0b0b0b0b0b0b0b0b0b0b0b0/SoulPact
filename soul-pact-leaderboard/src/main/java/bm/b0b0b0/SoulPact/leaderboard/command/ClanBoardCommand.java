package bm.b0b0b0.SoulPact.leaderboard.command;

import bm.b0b0b0.SoulPact.leaderboard.config.LeaderboardConfig;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardTextParser;
import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardKind;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardCatalog;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardCreationService;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardUpdateService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class ClanBoardCommand implements CommandExecutor, TabCompleter {

    private final LeaderboardMessages messages;
    private final Supplier<LeaderboardConfig> configSupplier;
    private final BoardCatalog catalog;
    private final BoardCreationService creationService;
    private final BoardUpdateService updateService;
    private final Runnable reloadAction;

    public ClanBoardCommand(
            LeaderboardMessages messages,
            Supplier<LeaderboardConfig> configSupplier,
            BoardCatalog catalog,
            BoardCreationService creationService,
            BoardUpdateService updateService,
            Runnable reloadAction
    ) {
        this.messages = messages;
        this.configSupplier = configSupplier;
        this.catalog = catalog;
        this.creationService = creationService;
        this.updateService = updateService;
        this.reloadAction = reloadAction;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(configSupplier.get().adminPermission())) {
            messages.send(sender, "leaderboard.command.no-permission");
            return true;
        }
        String sub = args.length == 0 ? "help" : args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "reload" -> handleReload(sender);
            case "create" -> handleCreate(sender, args);
            case "delete" -> handleDelete(sender, args);
            case "list" -> handleList(sender);
            case "update" -> handleUpdate(sender);
            case "tp" -> handleTeleport(sender, args);
            default -> handleHelp(sender);
        }
        return true;
    }

    private void handleHelp(CommandSender sender) {
        for (String line : messages.resolveList("leaderboard.command.help", Map.of())) {
            sender.sendMessage(LeaderboardTextParser.parse(line));
        }
    }

    private void handleReload(CommandSender sender) {
        reloadAction.run();
        messages.send(sender, "leaderboard.command.reloaded");
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "leaderboard.command.players-only");
            return;
        }
        if (args.length < 3) {
            messages.send(sender, "leaderboard.command.create-usage", Map.of(
                    "types", availableTypes()
            ));
            return;
        }
        Optional<BoardStatistic> statistic = BoardStatistic.parse(args[1]);
        if (statistic.isEmpty()) {
            messages.send(sender, "leaderboard.command.unknown-type", Map.of("types", availableTypes()));
            return;
        }
        int rank;
        try {
            rank = Integer.parseInt(args[2]);
        } catch (NumberFormatException exception) {
            messages.send(sender, "leaderboard.command.invalid-rank");
            return;
        }
        if (rank < 1 || rank > configSupplier.get().topSize()) {
            messages.send(sender, "leaderboard.command.invalid-rank");
            return;
        }
        BoardKind kind = args.length >= 4
                ? BoardKind.parse(args[3]).orElse(null)
                : BoardKind.SIGN;
        if (kind == null) {
            messages.send(sender, "leaderboard.command.unknown-kind");
            return;
        }
        creationService.create(player, statistic.get(), rank, kind, result -> {
            switch (result) {
                case CREATED -> messages.send(player, "leaderboard.command.created", Map.of(
                        "type", statistic.get().name(),
                        "rank", String.valueOf(rank),
                        "kind", kind.key()
                ));
                case NOT_A_SIGN -> messages.send(player, "leaderboard.command.not-a-sign");
                case NO_TARGET -> messages.send(player, "leaderboard.command.no-target");
            }
        });
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messages.send(sender, "leaderboard.command.delete-usage");
            return;
        }
        long boardId;
        try {
            boardId = Long.parseLong(args[1]);
        } catch (NumberFormatException exception) {
            messages.send(sender, "leaderboard.command.not-found");
            return;
        }
        creationService.delete(boardId, deleted -> {
            if (deleted) {
                messages.send(sender, "leaderboard.command.deleted", Map.of("id", String.valueOf(boardId)));
            } else {
                messages.send(sender, "leaderboard.command.not-found");
            }
        });
    }

    private void handleList(CommandSender sender) {
        List<Board> boards = catalog.all();
        if (boards.isEmpty()) {
            messages.send(sender, "leaderboard.command.list-empty");
            return;
        }
        messages.send(sender, "leaderboard.command.list-header", Map.of("count", String.valueOf(boards.size())));
        for (Board board : boards) {
            messages.send(sender, "leaderboard.command.list-entry", Map.of(
                    "id", String.valueOf(board.id()),
                    "type", board.statistic().name(),
                    "rank", String.valueOf(board.rankPosition()),
                    "kind", board.kind().key(),
                    "world", board.world(),
                    "x", String.valueOf((int) board.x()),
                    "y", String.valueOf((int) board.y()),
                    "z", String.valueOf((int) board.z())
            ));
        }
    }

    private void handleUpdate(CommandSender sender) {
        updateService.updateAll();
        messages.send(sender, "leaderboard.command.updated");
    }

    private void handleTeleport(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "leaderboard.command.players-only");
            return;
        }
        if (args.length < 2) {
            messages.send(sender, "leaderboard.command.tp-usage");
            return;
        }
        long boardId;
        try {
            boardId = Long.parseLong(args[1]);
        } catch (NumberFormatException exception) {
            messages.send(sender, "leaderboard.command.not-found");
            return;
        }
        Optional<Board> board = catalog.find(boardId);
        if (board.isEmpty()) {
            messages.send(sender, "leaderboard.command.not-found");
            return;
        }
        board.get().location().ifPresentOrElse(
                location -> {
                    player.teleport(location.clone().add(0, 1, 0));
                    messages.send(player, "leaderboard.command.teleported", Map.of("id", String.valueOf(boardId)));
                },
                () -> messages.send(player, "leaderboard.command.world-missing")
        );
    }

    private String availableTypes() {
        return String.join(", ", Arrays.stream(BoardStatistic.values()).map(Enum::name).toList());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(args[0], List.of("help", "reload", "create", "delete", "list", "update", "tp"));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            return filter(args[1], Arrays.stream(BoardStatistic.values()).map(Enum::name).toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return filter(args[2], List.of("1", "2", "3", "4", "5"));
        }
        if (args.length == 4 && args[0].equalsIgnoreCase("create")) {
            return filter(args[3], List.of("sign", "stand", "hologram"));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("tp"))) {
            return filter(args[1], catalog.all().stream().map(board -> String.valueOf(board.id())).toList());
        }
        return List.of();
    }

    private List<String> filter(String input, List<String> options) {
        String lower = input.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lower)) {
                matches.add(option);
            }
        }
        return matches;
    }
}
