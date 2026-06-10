package bm.b0b0b0.SoulPact.discord.message;

import java.util.Map;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordStartupConsolePresenter {

    private static final String PREFIX = "\u001B[37m[\u001B[90mSoulPact-Discord\u001B[37m]\u001B[0m ";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private final JavaPlugin plugin;
    private final DiscordMessages messages;

    public DiscordStartupConsolePresenter(JavaPlugin plugin, DiscordMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(boolean webhookConfigured) {
        ConsoleCommandSender console = console();
        console.sendMessage(" ");
        separator(console);
        if (webhookConfigured) {
            line(console, GREEN, "discord.startup.registered");
        } else {
            line(console, YELLOW, "discord.startup.no-webhook");
        }
        separator(console);
        console.sendMessage(" ");
    }

    public void logCoreMissing() {
        ConsoleCommandSender console = console();
        separator(console);
        line(console, RED, "discord.startup.core-missing");
        separator(console);
    }

    private void separator(ConsoleCommandSender console) {
        console.sendMessage(PREFIX + messages.resolve("discord.startup.separator", Map.of()));
    }

    private void line(ConsoleCommandSender console, String color, String key) {
        console.sendMessage(PREFIX + color + messages.resolve(key, Map.of()) + RESET);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }
}
