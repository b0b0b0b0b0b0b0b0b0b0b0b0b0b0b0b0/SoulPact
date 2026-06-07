package bm.b0b0b0.SoulPact.bank.message;

import bm.b0b0b0.SoulPact.bank.economy.VaultGateway;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class BankStartupConsolePresenter {

    private final JavaPlugin plugin;
    private final BankMessages messages;

    public BankStartupConsolePresenter(JavaPlugin plugin, BankMessages messages) {
        this.plugin = plugin;
        this.messages = messages;
    }

    public void logRegistered(VaultGateway vaultGateway) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "bank.startup.separator");
        coloredLine(console, "bank.startup.registered", BankConsolePalette::green);
        if (vaultGateway.available()) {
            coloredLine(console, "bank.startup.economy-connected", BankConsolePalette::green);
        } else {
            coloredLine(console, "bank.startup.economy-missing", BankConsolePalette::yellow);
        }
        line(console, "bank.startup.separator");
        blank(console);
    }

    public void logCoreMissing() {
        logFailure("bank.startup.core-missing");
    }

    public void logDatabaseMissing() {
        logFailure("bank.startup.database-missing");
    }

    public void logFailed() {
        logFailure("bank.startup.failed");
    }

    private void logFailure(String key) {
        ConsoleCommandSender console = console();
        line(console, "bank.startup.separator");
        coloredLine(console, key, BankConsolePalette::red);
        line(console, "bank.startup.separator");
        blank(console);
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        console.sendMessage(BankConsolePalette.prefixLine(messages.resolveDefault(key)));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color) {
        console.sendMessage(BankConsolePalette.prefixLine(color.apply(messages.resolveDefault(key))));
    }
}
