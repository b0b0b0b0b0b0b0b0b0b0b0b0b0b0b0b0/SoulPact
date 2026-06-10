package bm.b0b0b0.SoulPact.discord.config;

import bm.b0b0b0.SoulPact.discord.config.settings.DiscordSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordConfigurationLoader {

    private final JavaPlugin plugin;
    private final DiscordSettings settings = new DiscordSettings();
    private DiscordConfig config;

    public DiscordConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public DiscordConfig load() {
        Path path = plugin.getDataFolder().toPath().resolve("config.yml");
        settings.reload(path);
        config = DiscordConfigFactory.from(settings);
        return config;
    }

    public DiscordConfig config() {
        return config;
    }
}
