package bm.b0b0b0.SoulPact.clan.role;

import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.config.LocaleConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class RoleThemeService {

    private final RoleThemeLoader loader;
    private RoleTheme activeTheme;

    public RoleThemeService(JavaPlugin plugin, LocaleConfig localeConfig, ClanConfig clanConfig) {
        this.loader = new RoleThemeLoader(plugin, localeConfig, clanConfig);
        this.activeTheme = loader.load();
    }

    public RoleTheme theme() {
        return activeTheme;
    }

    public void reload() {
        activeTheme = loader.load();
    }
}
