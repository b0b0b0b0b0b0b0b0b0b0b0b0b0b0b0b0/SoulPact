package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.GuiGeneralSettings;
import bm.b0b0b0.SoulPact.core.config.settings.SoulPactSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConfigurationLoader {

    private final JavaPlugin plugin;
    private final SoulPactSettings mainSettings = new SoulPactSettings();
    private final GuiGeneralSettings guiSettings = new GuiGeneralSettings();
    private PluginConfig pluginConfig;

    public ConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public PluginConfig load() {
        SerializedConfigReloader.reload(plugin, mainSettings, Path.of("config.yml"));
        SerializedConfigReloader.reload(plugin, guiSettings, Path.of("gui", "general.yml"));
        this.pluginConfig = new PluginConfig(mainSettings, guiSettings);
        return pluginConfig;
    }

    public PluginConfig config() {
        return pluginConfig;
    }

    public SoulPactSettings mainSettings() {
        return mainSettings;
    }

    public GuiGeneralSettings guiSettings() {
        return guiSettings;
    }

    public void reload() {
        load();
    }
}
