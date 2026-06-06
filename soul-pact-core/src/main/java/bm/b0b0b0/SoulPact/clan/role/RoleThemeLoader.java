package bm.b0b0b0.SoulPact.clan.role;

import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.config.LocaleConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class RoleThemeLoader {

    private final JavaPlugin plugin;
    private final LocaleConfig localeConfig;
    private final ClanConfig clanConfig;

    public RoleThemeLoader(JavaPlugin plugin, LocaleConfig localeConfig, ClanConfig clanConfig) {
        this.plugin = plugin;
        this.localeConfig = localeConfig;
        this.clanConfig = clanConfig;
    }

    public RoleTheme load() {
        ensureRoleFiles();
        RoleTheme theme = loadTheme(clanConfig.roleTheme(), localeConfig.defaultLocale());
        if (theme != null) {
            return theme;
        }
        RoleTheme fallback = loadTheme(clanConfig.roleTheme(), localeConfig.fallbackLocale());
        if (fallback != null) {
            return fallback;
        }
        return defaultTheme();
    }

    private RoleTheme loadTheme(String themeId, String locale) {
        File file = roleFile(themeId, locale);
        if (!file.exists()) {
            return null;
        }
        return parse(YamlConfiguration.loadConfiguration(file));
    }

    private static RoleTheme parse(YamlConfiguration configuration) {
        List<String> order = configuration.getStringList("order");
        ConfigurationSection rolesSection = configuration.getConfigurationSection("roles");
        if (order.isEmpty() || rolesSection == null) {
            return null;
        }
        List<RoleDefinition> definitions = new ArrayList<>();
        for (String roleKey : order) {
            ConfigurationSection roleSection = rolesSection.getConfigurationSection(roleKey);
            if (roleSection == null) {
                continue;
            }
            String title = roleSection.getString("title", roleKey);
            boolean listNames = roleSection.getBoolean("list-names", true);
            definitions.add(new RoleDefinition(roleKey, title, listNames));
        }
        if (definitions.isEmpty()) {
            return null;
        }
        return new RoleTheme(order, definitions);
    }

    private static RoleTheme defaultTheme() {
        List<String> order = List.of("leader", "deputy", "officer", "member");
        List<RoleDefinition> definitions = List.of(
                new RoleDefinition("leader", "Leader", true),
                new RoleDefinition("deputy", "Deputy", true),
                new RoleDefinition("officer", "Officer", true),
                new RoleDefinition("member", "Members", false)
        );
        return new RoleTheme(order, definitions);
    }

    private void ensureRoleFiles() {
        File rolesFolder = rolesFolder();
        if (!rolesFolder.exists() && !rolesFolder.mkdirs()) {
            plugin.getLogger().severe("Failed to create lang/roles folder.");
        }
        copyDefaultRoleFile("military.ru.yml");
        copyDefaultRoleFile("military.en.yml");
        copyDefaultRoleFile("anime.ru.yml");
        copyDefaultRoleFile("anime.en.yml");
    }

    private void copyDefaultRoleFile(String fileName) {
        File target = new File(rolesFolder(), fileName);
        if (target.exists()) {
            return;
        }
        try (InputStream stream = plugin.getResource("lang/roles/" + fileName)) {
            if (stream == null) {
                return;
            }
            Files.copy(stream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to copy role file " + fileName + ": " + exception.getMessage());
        }
    }

    private File roleFile(String themeId, String locale) {
        String normalizedTheme = themeId == null || themeId.isBlank() ? "military" : themeId.trim();
        String normalizedLocale = locale == null || locale.isBlank() ? "en" : locale.trim().toLowerCase();
        return new File(rolesFolder(), normalizedTheme + "." + normalizedLocale + ".yml");
    }

    private File rolesFolder() {
        return new File(plugin.getDataFolder(), "lang/roles");
    }
}
