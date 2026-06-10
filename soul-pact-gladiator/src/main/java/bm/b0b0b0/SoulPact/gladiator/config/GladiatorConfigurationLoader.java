package bm.b0b0b0.SoulPact.gladiator.config;

import bm.b0b0b0.SoulPact.gladiator.config.settings.GladiatorSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class GladiatorConfigurationLoader {

    private final JavaPlugin plugin;
    private final GladiatorSettings settings = new GladiatorSettings();
    private GladiatorConfig config;

    public GladiatorConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public GladiatorConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = GladiatorConfigFactory.from(settings);
        return config;
    }

    public GladiatorConfig config() {
        return config;
    }

    public void reload() {
        load();
    }
}
