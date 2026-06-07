package bm.b0b0b0.SoulPact.coalition.config;

import bm.b0b0b0.SoulPact.coalition.config.settings.CoalitionSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class CoalitionConfigurationLoader {

    private final JavaPlugin plugin;
    private final CoalitionSettings settings = new CoalitionSettings();
    private CoalitionConfig config;

    public CoalitionConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public CoalitionConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = CoalitionConfigFactory.from(settings);
        return config;
    }

    public CoalitionConfig config() {
        return config;
    }

    public CoalitionSettings settings() {
        return settings;
    }

    public void reload() {
        load();
    }
}
