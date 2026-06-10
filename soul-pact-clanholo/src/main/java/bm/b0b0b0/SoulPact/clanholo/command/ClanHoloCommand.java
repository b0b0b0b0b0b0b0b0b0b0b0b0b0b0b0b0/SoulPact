package bm.b0b0b0.SoulPact.clanholo.command;

import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloMessages;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloTextParser;
import bm.b0b0b0.SoulPact.clanholo.service.HologramService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class ClanHoloCommand implements CommandExecutor, TabCompleter {

    private final ClanHoloMessages messages;
    private final Supplier<ClanHoloConfig> configSupplier;
    private final HologramService hologramService;
    private final Runnable reloadAction;

    public ClanHoloCommand(
            ClanHoloMessages messages,
            Supplier<ClanHoloConfig> configSupplier,
            HologramService hologramService,
            Runnable reloadAction
    ) {
        this.messages = messages;
        this.configSupplier = configSupplier;
        this.hologramService = hologramService;
        this.reloadAction = reloadAction;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            messages.send(sender, "clanholo.error.players-only");
            return true;
        }
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        if (sub.equals("admin")) {
            handleAdmin(player, args);
            return true;
        }
        if (!player.hasPermission("soulpact.clanholo.use")) {
            messages.send(player, "clanholo.error.no-permission");
            return true;
        }
        switch (sub) {
            case "help" -> showHelp(player);
            case "create" -> handleCreate(player, args, false);
            case "delete" -> handleDelete(player, args, false);
            case "add" -> handleAdd(player, args, false);
            case "remove" -> handleRemove(player, args, false);
            case "edit" -> handleEdit(player, args, false);
            case "list" -> hologramService.list(player);
            case "select" -> handleSelect(player, args);
            case "refresh" -> handleRefresh(player, args, false);
            case "reload" -> handleReload(player);
            default -> showHelp(player);
        }
        return true;
    }

    private void handleAdmin(Player player, String[] args) {
        if (!player.hasPermission(configSupplier.get().adminPermission())) {
            messages.send(player, "clanholo.error.no-permission");
            return;
        }
        if (args.length < 2) {
            showAdminHelp(player);
            return;
        }
        String sub = args[1].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "create" -> handleCreate(player, copyFrom(args, 2), true);
            case "delete" -> handleDelete(player, copyFrom(args, 2), true);
            case "add" -> handleAdd(player, copyFrom(args, 2), true);
            case "remove" -> handleRemove(player, copyFrom(args, 2), true);
            case "edit" -> handleEdit(player, copyFrom(args, 2), true);
            case "refresh" -> handleRefresh(player, copyFrom(args, 2), true);
            case "reload" -> handleReload(player);
            default -> showAdminHelp(player);
        }
    }

    private void handleCreate(Player player, String[] args, boolean admin) {
        if (args.length < 1) {
            messages.send(player, "clanholo.usage.create");
            return;
        }
        String name = args[0];
        String template = "";
        if (args.length >= 2 && ("info".equalsIgnoreCase(args[1]) || "rules".equalsIgnoreCase(args[1]))) {
            template = args[1].toLowerCase(Locale.ROOT);
        }
        hologramService.create(player, name, template, admin);
    }

    private void handleDelete(Player player, String[] args, boolean admin) {
        if (args.length < 1) {
            messages.send(player, "clanholo.usage.delete");
            return;
        }
        hologramService.delete(player, args[0], admin);
    }

    private void handleAdd(Player player, String[] args, boolean admin) {
        String content = args.length == 0 ? "" : String.join(" ", args);
        hologramService.addLine(player, content, admin);
    }

    private void handleRemove(Player player, String[] args, boolean admin) {
        if (args.length < 1) {
            messages.send(player, "clanholo.usage.remove");
            return;
        }
        try {
            hologramService.removeLine(player, Integer.parseInt(args[0]), admin);
        } catch (NumberFormatException exception) {
            messages.send(player, "clanholo.error.line-not-found");
        }
    }

    private void handleEdit(Player player, String[] args, boolean admin) {
        if (args.length < 2) {
            messages.send(player, "clanholo.usage.edit");
            return;
        }
        try {
            int line = Integer.parseInt(args[0]);
            String content = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            hologramService.editLine(player, line, content, admin);
        } catch (NumberFormatException exception) {
            messages.send(player, "clanholo.error.line-not-found");
        }
    }

    private void handleSelect(Player player, String[] args) {
        if (args.length < 1) {
            messages.send(player, "clanholo.usage.select");
            return;
        }
        hologramService.select(player, args[0]);
    }

    private void handleRefresh(Player player, String[] args, boolean admin) {
        if (args.length < 1) {
            messages.send(player, "clanholo.usage.refresh");
            return;
        }
        hologramService.refresh(player, args[0], admin);
    }

    private void handleReload(Player player) {
        if (!player.hasPermission(configSupplier.get().adminPermission())) {
            messages.send(player, "clanholo.error.no-permission");
            return;
        }
        reloadAction.run();
        messages.send(player, "clanholo.reload.success");
    }

    private void showHelp(Player player) {
        for (String line : messages.resolveList("clanholo.command.help", Map.of())) {
            player.sendMessage(ClanHoloTextParser.parse(line));
        }
    }

    private void showAdminHelp(Player player) {
        for (String line : messages.resolveList("clanholo.command.admin-help", Map.of())) {
            player.sendMessage(ClanHoloTextParser.parse(line));
        }
    }

    private static String[] copyFrom(String[] args, int from) {
        if (from >= args.length) {
            return new String[0];
        }
        return Arrays.copyOfRange(args, from, args.length);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            addIfMatches(completions, args[0], "help", "create", "delete", "add", "remove", "edit", "list", "select", "refresh", "admin", "reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
            addIfMatches(completions, args[1], "info", "rules");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            addIfMatches(completions, args[1], "create", "delete", "add", "remove", "edit", "refresh", "reload");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("create")) {
            addIfMatches(completions, args[2], "info", "rules");
        }
        return completions;
    }

    private static void addIfMatches(List<String> target, String prefix, String... values) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        for (String value : values) {
            if (value.startsWith(lower)) {
                target.add(value);
            }
        }
    }
}
