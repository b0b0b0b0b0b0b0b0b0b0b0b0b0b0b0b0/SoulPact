package bm.b0b0b0.SoulPact.gladiator.message;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class GladiatorStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final GladiatorMessages messages;

    public GladiatorStartupConsolePresenter(JavaPlugin plugin, GladiatorMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(int arenaCount) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "gladiator.startup.separator");
        coloredLine(console, "gladiator.startup.registered", GladiatorConsolePalette::green, Map.of("count", String.valueOf(arenaCount)));
        line(console, "gladiator.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("gladiator.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("gladiator.startup.database-missing");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "gladiator.startup.separator");
        coloredLine(console, key, GladiatorConsolePalette::red, Map.of());
        line(console, "gladiator.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(GladiatorConsolePalette.prefixLine(messages.resolveDefault(key)));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color, Map<String, String> placeholders) {
        console.sendMessage(GladiatorConsolePalette.prefixLine(color.apply(messages.resolveDefault(key, placeholders))));
    }
}
