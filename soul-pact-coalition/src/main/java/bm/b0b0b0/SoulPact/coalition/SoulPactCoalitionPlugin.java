package bm.b0b0b0.SoulPact.coalition;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.coalition.bridge.CoalitionDisplayBridgeImpl;
import bm.b0b0b0.SoulPact.coalition.bridge.CoalitionWarBridgeImpl;
import bm.b0b0b0.SoulPact.coalition.command.ClanCoalitionCommand;
import bm.b0b0b0.SoulPact.coalition.config.CoalitionConfig;
import bm.b0b0b0.SoulPact.coalition.config.CoalitionConfigurationLoader;
import bm.b0b0b0.SoulPact.coalition.extension.CoalitionExtension;
import bm.b0b0b0.SoulPact.coalition.gui.CoalitionGuiService;
import bm.b0b0b0.SoulPact.coalition.listener.CoalitionPlayerCacheListener;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionInviteChatPresenter;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionMessages;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionStartupConsolePresenter;
import bm.b0b0b0.SoulPact.coalition.migration.CoalitionSchemaMigrator;
import bm.b0b0b0.SoulPact.coalition.repository.SqlCoalitionRepository;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionBossBarService;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionClanLookup;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionMembershipCache;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionPlayerClanCache;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionService;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionTreasuryBridge;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionTreasuryDistributor;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionWarStateTracker;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactCoalitionPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private CoalitionExtension extension;
    private CoalitionBossBarService bossBarService;
    private CoalitionStartupConsolePresenter startupPresenter;
    private CoalitionConfigurationLoader configurationLoader;

    @Override
    public void onEnable() {
        configurationLoader = new CoalitionConfigurationLoader(this);
        CoalitionConfig config = configurationLoader.load();
        CoalitionMessages coalitionMessages = new CoalitionMessages(this, config.locale(), config.fallbackLocale());
        coalitionMessages.load();
        startupPresenter = new CoalitionStartupConsolePresenter(this, coalitionMessages);
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
        if (bossBarService != null) {
            bossBarService.clearAll();
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
        if (!resolved.isDatabaseReady()) {
            if (attempt >= BOOTSTRAP_MAX_ATTEMPTS) {
                startupPresenter.logDatabaseMissing();
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
        CoalitionConfig config = configurationLoader.load();
        CoalitionMessages coalitionMessages = new CoalitionMessages(this, config.locale(), config.fallbackLocale());
        coalitionMessages.load();
        new CoalitionSchemaMigrator(this, api.dataSource()).migrate();
        SqlCoalitionRepository repository = new SqlCoalitionRepository(api);
        CoalitionMembershipCache membershipCache = new CoalitionMembershipCache(repository);
        CoalitionWarStateTracker warStateTracker = new CoalitionWarStateTracker();
        CoalitionPlayerClanCache playerClanCache = new CoalitionPlayerClanCache();
        CoalitionClanLookup clanLookup = new CoalitionClanLookup(api);
        CoalitionTreasuryBridge treasuryBridge = new CoalitionTreasuryBridge(api);
        CoalitionTreasuryDistributor treasuryDistributor = new CoalitionTreasuryDistributor(
                config,
                membershipCache,
                treasuryBridge
        );
        bossBarService = new CoalitionBossBarService(
                config,
                coalitionMessages,
                membershipCache,
                warStateTracker,
                playerClanCache
        );
        CoalitionInviteChatPresenter inviteChatPresenter = new CoalitionInviteChatPresenter(api, coalitionMessages, clanLookup);
        CoalitionService coalitionService = new CoalitionService(
                api,
                config,
                coalitionMessages,
                repository,
                membershipCache,
                clanLookup,
                inviteChatPresenter,
                bossBarService,
                playerClanCache
        );
        CoalitionGuiService guiService = new CoalitionGuiService(coalitionMessages, coalitionService);
        CoalitionDisplayBridgeImpl displayBridge = new CoalitionDisplayBridgeImpl(coalitionService);
        CoalitionWarBridgeImpl warBridge = new CoalitionWarBridgeImpl(
                api,
                coalitionMessages,
                coalitionService,
                membershipCache,
                warStateTracker,
                bossBarService,
                clanLookup,
                treasuryDistributor
        );
        extension = new CoalitionExtension(guiService, displayBridge, warBridge);
        api.extensions().register(extension);
        extension.enable(api);
        coalitionService.bootstrapCache();
        Bukkit.getPluginManager().registerEvents(
                new CoalitionPlayerCacheListener(api, playerClanCache, bossBarService),
                this
        );
        CoalitionPlayerCacheListener.startBossBarTask(this, bossBarService);
        PluginCommand command = getCommand("clancoalition");
        if (command != null) {
            command.setExecutor(new ClanCoalitionCommand(guiService, coalitionService, coalitionMessages));
        }
        startupPresenter.logRegistered();
    }
}
