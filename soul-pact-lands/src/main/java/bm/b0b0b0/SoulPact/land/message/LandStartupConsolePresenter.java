package bm.b0b0b0.SoulPact.land.message;

import bm.b0b0b0.SoulPact.land.integration.WorldGuardGateway;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class LandStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final LandMessages messages;

    public LandStartupConsolePresenter(JavaPlugin plugin, LandMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(WorldGuardGateway worldGuardGateway) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "land.startup.separator");
        coloredLine(console, "land.startup.registered", LandConsolePalette::green);
        if (worldGuardGateway.available()) {
            coloredLine(console, "land.startup.worldguard-connected", LandConsolePalette::green);
        } else {
            coloredLine(console, "land.startup.worldguard-missing", LandConsolePalette::yellow);
        }
        line(console, "land.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("land.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("land.startup.database-missing");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "land.startup.separator");
        coloredLine(console, key, LandConsolePalette::red);
        line(console, "land.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(LandConsolePalette.prefixLine(messages.resolveDefault(key)));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color) {
        console.sendMessage(LandConsolePalette.prefixLine(color.apply(messages.resolveDefault(key))));
    }
}
