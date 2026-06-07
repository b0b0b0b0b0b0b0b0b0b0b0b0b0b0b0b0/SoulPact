package bm.b0b0b0.SoulPact.bank.config;

import bm.b0b0b0.SoulPact.bank.config.settings.BankSettings;
import java.nio.file.Path;
import org.bukkit.plugin.java.JavaPlugin;

public final class BankConfigurationLoader {

    private final JavaPlugin plugin;
    private final BankSettings settings = new BankSettings();
    private BankConfig config;

    public BankConfigurationLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BankConfig load() {
        SerializedConfigReloader.reload(plugin, settings, Path.of("config.yml"));
        config = BankConfigFactory.from(settings);
        return config;
    }

    public BankConfig config() {
        return config;
    }

    public BankSettings settings() {
        return settings;
    }

    public void reload() {
        load();
    }
}
