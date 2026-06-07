package bm.b0b0b0.SoulPact.chest.config;

import bm.b0b0b0.SoulPact.chest.config.settings.ChestSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChestConfigurationLoader {

    private final JavaPlugin plugin;
    private final ChestSettings settings = new ChestSettings();
    private ChestConfig config;

    public ChestConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ChestConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = ChestConfigFactory.from(settings);
        return config;
    }

    public ChestConfig config() {
        return config;
    }

    public ChestSettings settings() {
        return settings;
    }

    public void reload() {
        load();
    }
}
