package bm.b0b0b0.SoulPact.chest.message;

import bm.b0b0b0.SoulPact.chest.economy.ChestVaultGateway;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final ChestMessages messages;

    public ChestStartupConsolePresenter(JavaPlugin plugin, ChestMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(ChestVaultGateway vaultGateway, boolean treasuryAvailable) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "chest.startup.separator");
        coloredLine(console, "chest.startup.registered", ChestConsolePalette::green);
        if (treasuryAvailable) {
            coloredLine(console, "chest.startup.treasury-connected", ChestConsolePalette::green);
        } else if (vaultGateway.available()) {
            coloredLine(console, "chest.startup.leader-wallet", ChestConsolePalette::yellow);
        } else {
            coloredLine(console, "chest.startup.economy-missing", ChestConsolePalette::yellow);
        }
        line(console, "chest.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("chest.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("chest.startup.database-missing");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "chest.startup.separator");
        coloredLine(console, key, ChestConsolePalette::red);
        line(console, "chest.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(ChestConsolePalette.prefixLine(messages.resolveDefault(key, Map.of())));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color) {
        console.sendMessage(ChestConsolePalette.prefixLine(color.apply(messages.resolveDefault(key, Map.of()))));
    }
}
