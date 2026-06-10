package bm.b0b0b0.SoulPact.discord;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.discord.config.DiscordConfig;
import bm.b0b0b0.SoulPact.discord.config.DiscordConfigurationLoader;
import bm.b0b0b0.SoulPact.discord.extension.DiscordExtension;
import bm.b0b0b0.SoulPact.discord.listener.DiscordBridgeListener;
import bm.b0b0b0.SoulPact.discord.message.DiscordMessages;
import bm.b0b0b0.SoulPact.discord.message.DiscordStartupConsolePresenter;
import bm.b0b0b0.SoulPact.discord.model.DiscordEventType;
import bm.b0b0b0.SoulPact.discord.webhook.DiscordEmbedFactory;
import bm.b0b0b0.SoulPact.discord.webhook.DiscordEventPublisher;
import bm.b0b0b0.SoulPact.discord.webhook.DiscordWebhookClient;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactDiscordPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private DiscordExtension extension;
    private DiscordConfigurationLoader configurationLoader;
    private DiscordConfig config;
    private DiscordMessages discordMessages;
    private DiscordStartupConsolePresenter startupPresenter;
    private DiscordWebhookClient webhookClient;
    private DiscordEventPublisher publisher;

    @Override
    public void onEnable() {
        configurationLoader = new DiscordConfigurationLoader(this);
        config = configurationLoader.load();
        discordMessages = new DiscordMessages(this, config.locale(), config.fallbackLocale());
        discordMessages.load();
        startupPresenter = new DiscordStartupConsolePresenter(this, discordMessages);
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
        if (publisher != null) {
            publisher.publish(DiscordEventType.SERVER_STOP, Map.of());
        }
        if (webhookClient != null) {
            webhookClient.shutdown();
        }
        if (api != null && extension != null) {
            api.extensions().unregister(extension.id());
        }
    }

    private void scheduleBootstrap(int attempt) {
        long delay = attempt == 0 ? 1L : BOOTSTRAP_DELAY_TICKS;
        Bukkit.getScheduler().runTaskLater(this, () -> tryBootstrap(attempt), delay);
    }

    private void tryBootstrap(int attempt) {
        SoulPactApi resolved = resolveApi();
        if (resolved == null) {
            if (attempt >= BOOTSTRAP_MAX_ATTEMPTS) {
                startupPresenter.logCoreMissing();
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            scheduleBootstrap(attempt + 1);
            return;
        }
        finishEnable(resolved);
    }

    private SoulPactApi resolveApi() {
        RegisteredServiceProvider<SoulPactApi> provider = Bukkit.getServicesManager().getRegistration(SoulPactApi.class);
        return provider == null ? null : provider.getProvider();
    }

    private void finishEnable(SoulPactApi resolvedApi) {
        api = resolvedApi;
        webhookClient = new DiscordWebhookClient(() -> config, getLogger());
        DiscordEmbedFactory embedFactory = new DiscordEmbedFactory(() -> config, discordMessages);
        publisher = new DiscordEventPublisher(() -> config, embedFactory, webhookClient);
        webhookClient.start();
        extension = new DiscordExtension(this::reloadModule);
        api.extensions().register(extension);
        extension.enable(api);
        Bukkit.getPluginManager().registerEvents(new DiscordBridgeListener(publisher), this);
        publisher.publish(DiscordEventType.SERVER_START, Map.of());
        startupPresenter.logRegistered(config.webhookConfigured());
    }

    private void reloadModule() {
        config = configurationLoader.load();
        discordMessages.load();
    }
}
