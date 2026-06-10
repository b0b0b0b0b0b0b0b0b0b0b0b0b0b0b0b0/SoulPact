package bm.b0b0b0.SoulPact.gladiator.command.admin;

import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorTextParser;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.CommandSender;

public final class RewardAdminHandler {

    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;

    public RewardAdminHandler(GladiatorMessages messages, ArenaCatalog catalog) {
        this.messages = messages;
        this.catalog = catalog;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "gladiator.usage.reward", Map.of());
            return;
        }
        if (catalog.find(args[2]).isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "list" -> list(sender, args[2]);
            case "clean" -> clean(sender, args[2]);
            case "add" -> add(sender, args);
            default -> messages.send(sender, "gladiator.usage.reward", Map.of());
        }
    }

    private void list(CommandSender sender, String arenaName) {
        List<String> rewards = catalog.rewardsOf(arenaName);
        messages.send(sender, "gladiator.admin.reward-list-header", Map.of(
                "arena", arenaName,
                "count", String.valueOf(rewards.size())
        ));
        int index = 1;
        for (String reward : rewards) {
            sender.sendMessage(GladiatorTextParser.parse(messages.resolveDefault("gladiator.admin.reward-list-line", Map.of(
                    "index", String.valueOf(index++),
                    "command", reward
            ))));
        }
    }

    private void clean(CommandSender sender, String arenaName) {
        catalog.clearRewards(arenaName);
        messages.send(sender, "gladiator.admin.reward-cleared", Map.of("arena", arenaName));
    }

    private void add(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "gladiator.usage.reward-add", Map.of());
            return;
        }
        String command = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
        catalog.addReward(args[2], command);
        messages.send(sender, "gladiator.admin.reward-added", Map.of("arena", args[2], "command", command));
    }
}
