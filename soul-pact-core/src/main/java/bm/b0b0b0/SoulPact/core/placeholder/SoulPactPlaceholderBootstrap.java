package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.config.LocaleConfig;
import bm.b0b0b0.SoulPact.core.config.PlaceholderConfig;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import bm.b0b0b0.SoulPact.core.integration.PlaceholderApiIntegration;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactPlaceholderBootstrap {

    private final JavaPlugin plugin;
    private final PlaceholderApiIntegration placeholderApiIntegration;
    private SoulPactPlaceholderService placeholderService;
    private SoulPactPlaceholderExpansion expansion;

    public SoulPactPlaceholderBootstrap(
            JavaPlugin plugin,
            PlaceholderApiIntegration placeholderApiIntegration
    ) {
        this.plugin = plugin;
        this.placeholderApiIntegration = placeholderApiIntegration;
    }

    public void register(
            PlaceholderConfig placeholderConfig,
            LocaleConfig localeConfig,
            VaultIntegration vaultIntegration,
            DataSourceProvider dataSourceProvider,
            RoleThemeService roleThemeService,
            ExtensionRegistry extensionRegistry
    ) {
        if (!placeholderApiIntegration.available()) {
            return;
        }
        unregister();
        ClanPlaceholderDataLoader dataLoader = new ClanPlaceholderDataLoader(dataSourceProvider);
        ClanPlaceholderSnapshotFactory snapshotFactory = new ClanPlaceholderSnapshotFactory(
                placeholderConfig,
                roleThemeService
        );
        placeholderService = new SoulPactPlaceholderService(
                null,
                dataLoader,
                snapshotFactory,
                placeholderConfig.cacheMillis()
        );
        SoulPactPlaceholderResolver resolver = new SoulPactPlaceholderResolver(
                placeholderConfig,
                localeConfig,
                vaultIntegration,
                placeholderService,
                List.of(new ExtensionPlaceholderBridge(extensionRegistry))
        );
        placeholderService.bindResolver(resolver);
        expansion = new SoulPactPlaceholderExpansion(plugin, placeholderService);
        expansion.register();
        ClanPlaceholderInvalidatorRegistry.install(placeholderService);
    }

    public void unregister() {
        ClanPlaceholderInvalidatorRegistry.uninstall();
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
        if (placeholderService != null) {
            placeholderService.invalidateAll();
            placeholderService = null;
        }
    }

    public SoulPactPlaceholderService service() {
        return placeholderService;
    }
}
