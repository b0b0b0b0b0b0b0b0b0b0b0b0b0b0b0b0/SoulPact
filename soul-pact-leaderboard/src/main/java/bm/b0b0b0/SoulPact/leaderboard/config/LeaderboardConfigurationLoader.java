package bm.b0b0b0.SoulPact.leaderboard.config;

import bm.b0b0b0.SoulPact.leaderboard.config.settings.LeaderboardSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class LeaderboardConfigurationLoader {

    private final JavaPlugin plugin;
    private final LeaderboardSettings settings = new LeaderboardSettings();
    private LeaderboardConfig config;

    public LeaderboardConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public LeaderboardConfig load() {
        Path path = plugin.getDataFolder().toPath().resolve("config.yml");
        settings.reload(path);
        config = LeaderboardConfigFactory.from(settings);
        return config;
    }

    public LeaderboardConfig config() {
        return config;
    }
}
