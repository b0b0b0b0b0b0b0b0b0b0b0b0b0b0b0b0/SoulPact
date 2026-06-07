package bm.b0b0b0.SoulPact.coalition.message;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class CoalitionStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final CoalitionMessages messages;

    public CoalitionStartupConsolePresenter(JavaPlugin plugin, CoalitionMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered() {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "coalition.startup.separator");
        coloredLine(console, "coalition.startup.registered", CoalitionConsolePalette::green);
        line(console, "coalition.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("coalition.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("coalition.startup.database-missing");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "coalition.startup.separator");
        coloredLine(console, key, CoalitionConsolePalette::red);
        line(console, "coalition.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(CoalitionConsolePalette.prefixLine(messages.resolveDefault(key, Map.of())));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color) {
        console.sendMessage(CoalitionConsolePalette.prefixLine(color.apply(messages.resolveDefault(key, Map.of()))));
    }
}
