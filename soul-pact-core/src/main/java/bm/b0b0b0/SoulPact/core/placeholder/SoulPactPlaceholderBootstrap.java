package bm.b0b0b0.SoulPact.core.placeholder;

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
            RoleThemeService roleThemeService
    ) {
        if (!placeholderApiIntegration.available()) {
            return;
        }
        unregister();
        ClanPlaceholderSnapshotLoader snapshotLoader = new ClanPlaceholderSnapshotLoader(
                dataSourceProvider,
                roleThemeService,
                placeholderConfig.cacheMillis()
        );
        SoulPactPlaceholderResolver resolver = new SoulPactPlaceholderResolver(
                placeholderConfig,
                localeConfig,
                vaultIntegration,
                snapshotLoader,
                List.of()
        );
        placeholderService = new SoulPactPlaceholderService(
                resolver,
                snapshotLoader,
                placeholderConfig.cacheMillis()
        );
        expansion = new SoulPactPlaceholderExpansion(plugin, placeholderService);
        expansion.register();
    }

    public void unregister() {
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
