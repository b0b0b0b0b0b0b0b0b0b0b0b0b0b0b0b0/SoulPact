package bm.b0b0b0.SoulPact.war.config;

import bm.b0b0b0.SoulPact.war.config.settings.WarSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarConfigurationLoader {

    private final JavaPlugin plugin;
    private final WarSettings settings = new WarSettings();
    private WarConfig config;

    public WarConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public WarConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = WarConfigFactory.from(settings);
        return config;
    }

    public WarConfig config() {
        return config;
    }

    public WarSettings settings() {
        return settings;
    }

    public void reload() {
        load();
    }
}
