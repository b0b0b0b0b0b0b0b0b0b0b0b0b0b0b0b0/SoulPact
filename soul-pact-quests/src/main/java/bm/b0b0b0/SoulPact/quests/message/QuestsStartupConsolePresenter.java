package bm.b0b0b0.SoulPact.quests.message;

import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestsStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final QuestsMessages messages;

    public QuestsStartupConsolePresenter(JavaPlugin plugin, QuestsMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(int questCount, boolean bankAvailable) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "quests.startup.separator");
        coloredLine(console, "quests.startup.registered", QuestsConsolePalette::green, Map.of("count", String.valueOf(questCount)));
        if (bankAvailable) {
            coloredLine(console, "quests.startup.bank-connected", QuestsConsolePalette::green, Map.of());
        } else {
            coloredLine(console, "quests.startup.bank-missing", QuestsConsolePalette::yellow, Map.of());
        }
        line(console, "quests.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("quests.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("quests.startup.database-missing");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "quests.startup.separator");
        coloredLine(console, key, QuestsConsolePalette::red, Map.of());
        line(console, "quests.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(QuestsConsolePalette.prefixLine(messages.resolveDefault(key)));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color, Map<String, String> placeholders) {
        console.sendMessage(QuestsConsolePalette.prefixLine(color.apply(messages.resolveDefault(key, placeholders))));
    }
}
