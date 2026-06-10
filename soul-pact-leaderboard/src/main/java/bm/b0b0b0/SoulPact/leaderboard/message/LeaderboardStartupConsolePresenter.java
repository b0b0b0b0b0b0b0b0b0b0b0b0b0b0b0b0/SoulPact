package bm.b0b0b0.SoulPact.leaderboard.message;

import java.util.Map;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class LeaderboardStartupConsolePresenter {

    private static final String PREFIX = "\u001B[37m[\u001B[90mSoulPact-Leaderboard\u001B[37m]\u001B[0m ";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private final JavaPlugin plugin;
    private final LeaderboardMessages messages;

    public LeaderboardStartupConsolePresenter(JavaPlugin plugin, LeaderboardMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(int boardCount) {
        ConsoleCommandSender console = console();
        console.sendMessage(" ");
        separator(console);
        line(console, GREEN, "leaderboard.startup.registered", Map.of("count", String.valueOf(boardCount)));
        separator(console);
        console.sendMessage(" ");
    }

    public void logCoreMissing() {
        ConsoleCommandSender console = console();
        separator(console);
        line(console, RED, "leaderboard.startup.core-missing", Map.of());
        separator(console);
    }

    private void separator(ConsoleCommandSender console) {
        console.sendMessage(PREFIX + messages.resolve("leaderboard.startup.separator", Map.of()));
    }

    private void line(ConsoleCommandSender console, String color, String key, Map<String, String> placeholders) {
        console.sendMessage(PREFIX + color + messages.resolve(key, placeholders) + RESET);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }
}
