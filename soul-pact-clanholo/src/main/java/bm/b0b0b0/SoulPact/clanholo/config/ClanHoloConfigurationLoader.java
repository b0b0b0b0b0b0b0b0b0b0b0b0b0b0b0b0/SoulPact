package bm.b0b0b0.SoulPact.clanholo.config;

import bm.b0b0b0.SoulPact.clanholo.config.settings.ClanHoloSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class ClanHoloConfigurationLoader {

    private final JavaPlugin plugin;
    private final ClanHoloSettings settings = new ClanHoloSettings();
    private ClanHoloConfig config;

    public ClanHoloConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ClanHoloConfig load() {
        Path path = plugin.getDataFolder().toPath().resolve("config.yml");
        settings.reload(path);
        config = ClanHoloConfigFactory.from(settings);
        return config;
    }

    public ClanHoloConfig config() {
        return config;
    }
}
