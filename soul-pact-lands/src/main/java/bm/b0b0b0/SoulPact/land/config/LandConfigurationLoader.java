package bm.b0b0b0.SoulPact.land.config;

import bm.b0b0b0.SoulPact.land.config.settings.LandSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class LandConfigurationLoader {

    private final JavaPlugin plugin;
    private final LandSettings settings = new LandSettings();
    private LandConfig config;

    public LandConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public LandConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = LandConfigFactory.from(settings);
        return config;
    }

    public LandConfig config() {
        return config;
    }

    public LandSettings settings() {
        return settings;
    }

    public void reload() {
        load();
    }
}
