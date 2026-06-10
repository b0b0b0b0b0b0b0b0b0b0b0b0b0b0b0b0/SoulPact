package bm.b0b0b0.SoulPact.quests.config;

import bm.b0b0b0.SoulPact.quests.config.settings.QuestsSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class QuestsConfigurationLoader {

    private final JavaPlugin plugin;
    private final QuestsSettings settings = new QuestsSettings();
    private QuestsConfig config;

    public QuestsConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public QuestsConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = QuestsConfigFactory.from(settings);
        return config;
    }

    public QuestsConfig config() {
        return config;
    }

    public void reload() {
        load();
    }
}
