package bm.b0b0b0.SoulPact.core.message;

import bm.b0b0b0.SoulPact.core.config.EconomyConfig;
import bm.b0b0b0.SoulPact.core.integration.IntegrationRegistry;
import bm.b0b0b0.SoulPact.core.integration.PluginIntegration;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class StartupConsolePresenter {

    private final JavaPlugin plugin;
    private final MessageService messageService;

    public StartupConsolePresenter(JavaPlugin plugin, MessageService messageService) {
        this.plugin = plugin;
        this.messageService = messageService;
    }

    public void logStartupHeader(
            IntegrationRegistry integrationRegistry,
            EconomyConfig economyConfig,
            VaultIntegration vaultIntegration
    ) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "startup.banner.separator");
        line(console, "startup.banner.intro");
        line(console, "startup.banner.version", Map.of("version", plugin.getPluginMeta().getVersion()));
        blank(console);
        line(console, "startup.banner.init");
        logIntegrations(console, integrationRegistry);
        logEconomy(console, economyConfig, vaultIntegration);
    }

    public void logStartupComplete(ExtensionRegistryImpl extensionRegistry) {
        ConsoleCommandSender console = console();
        line(console, "startup.banner.separator");
        coloredLine(console, "startup.database.connected", ConsolePalette::green);
        logExtensions(console, extensionRegistry);
        coloredLine(console, "startup.loaded", ConsolePalette::green);
        line(console, "startup.banner.separator");
        blank(console);
    }

    public void logStartupFailed() {
        ConsoleCommandSender console = console();
        line(console, "startup.banner.separator");
        coloredLine(console, "startup.database.failed", ConsolePalette::red);
        line(console, "startup.banner.separator");
        blank(console);
    }

    public void logReloadComplete(
            IntegrationRegistry integrationRegistry,
            EconomyConfig economyConfig,
            VaultIntegration vaultIntegration
    ) {
        ConsoleCommandSender console = console();
        blank(console);
        line(console, "startup.banner.separator");
        line(console, "startup.banner.intro");
        line(console, "startup.banner.version", Map.of("version", plugin.getPluginMeta().getVersion()));
        blank(console);
        line(console, "startup.banner.init");
        line(console, "startup.reload-complete");
        logIntegrations(console, integrationRegistry);
        logEconomy(console, economyConfig, vaultIntegration);
        line(console, "startup.banner.separator");
        blank(console);
    }

    private void logIntegrations(ConsoleCommandSender console, IntegrationRegistry integrationRegistry) {
        for (PluginIntegration integration : integrationRegistry.all()) {
            String statusText = messageService.resolveDefault(
                    integration.available()
                            ? "startup.integration.status-connected"
                            : "startup.integration.status-missing"
            );
            String status = integration.available()
                    ? ConsolePalette.green(statusText)
                    : ConsolePalette.gray(statusText);
            line(console, "startup.integration.line", Map.of(
                    "name", integration.displayName(),
                    "status", status
            ));
        }
    }

    private void logEconomy(
            ConsoleCommandSender console,
            EconomyConfig economyConfig,
            VaultIntegration vaultIntegration
    ) {
        if (economyConfig.economyDisabled()) {
            line(console, "startup.economy.disabled");
            return;
        }
        if (!vaultIntegration.available()) {
            coloredLine(console, "startup.economy.vault-missing", ConsolePalette::yellow);
            return;
        }
        coloredLine(console, "startup.economy.vault-connected", ConsolePalette::green);
    }

    private void logExtensions(ConsoleCommandSender console, ExtensionRegistryImpl extensionRegistry) {
        int count = extensionRegistry.all().size();
        if (count == 0) {
            return;
        }
        line(console, "startup.extensions.line", Map.of("count", String.valueOf(count)));
    }

    private ConsoleCommandSender console() {
        return plugin.getServer().getConsoleSender();
    }

    private void blank(ConsoleCommandSender console) {
        console.sendMessage(" ");
    }

    private void line(ConsoleCommandSender console, String key) {
        line(console, key, Map.of());
    }

    private void line(ConsoleCommandSender console, String key, Map<String, String> placeholders) {
        console.sendMessage(ConsolePalette.prefixLine(messageService.resolveDefault(key, placeholders)));
    }

    private void coloredLine(ConsoleCommandSender console, String key, UnaryOperator<String> color) {
        console.sendMessage(ConsolePalette.prefixLine(color.apply(messageService.resolveDefault(key))));
    }
}
