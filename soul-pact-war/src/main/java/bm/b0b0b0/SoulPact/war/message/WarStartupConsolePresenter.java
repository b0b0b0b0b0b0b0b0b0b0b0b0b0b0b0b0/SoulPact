package bm.b0b0b0.SoulPact.war.message;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final WarMessages messages;

    public WarStartupConsolePresenter(JavaPlugin plugin, WarMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered() {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "war.startup.separator");
        coloredLine(console, "war.startup.registered", WarConsolePalette::green);
        line(console, "war.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("war.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("war.startup.database-missing");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "war.startup.separator");
        coloredLine(console, key, WarConsolePalette::red);
        line(console, "war.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(WarConsolePalette.prefixLine(messages.resolveDefault(key, Map.of())));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color) {
        console.sendMessage(WarConsolePalette.prefixLine(color.apply(messages.resolveDefault(key, Map.of()))));
    }
}
