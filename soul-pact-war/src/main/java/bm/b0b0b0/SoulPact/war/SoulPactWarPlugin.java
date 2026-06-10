package bm.b0b0b0.SoulPact.war;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.bridge.WarFlagBreakGateImpl;
import bm.b0b0b0.SoulPact.war.bridge.WarUiBridgeImpl;
import bm.b0b0b0.SoulPact.war.command.ClanWarCommand;
import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.config.WarConfigurationLoader;
import bm.b0b0b0.SoulPact.war.extension.WarExtension;
import bm.b0b0b0.SoulPact.war.gui.WarGuiListener;
import bm.b0b0b0.SoulPact.war.gui.WarGuiService;
import bm.b0b0b0.SoulPact.war.integration.WorldGuardWarEventBridge;
import bm.b0b0b0.SoulPact.war.listener.WarBossBarTask;
import bm.b0b0b0.SoulPact.war.listener.WarCombatKillListener;
import bm.b0b0b0.SoulPact.war.listener.WarPlayerCacheListener;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.message.WarFlagRevealPresenter;
import bm.b0b0b0.SoulPact.war.message.WarPendingChatPresenter;
import bm.b0b0b0.SoulPact.war.message.WarStartupConsolePresenter;
import bm.b0b0b0.SoulPact.war.migration.WarSchemaMigrator;
import bm.b0b0b0.SoulPact.war.repository.SqlWarRepository;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import bm.b0b0b0.SoulPact.war.service.CoalitionWarBridgeLookup;
import bm.b0b0b0.SoulPact.war.service.WarLandCombatService;
import bm.b0b0b0.SoulPact.war.service.WarLandBridgeLookup;
import bm.b0b0b0.SoulPact.war.service.WarBossBarService;
import bm.b0b0b0.SoulPact.war.service.WarClanLookup;
import bm.b0b0b0.SoulPact.war.service.WarPlayerClanCache;
import bm.b0b0b0.SoulPact.war.service.WarSpoilsBridge;
import bm.b0b0b0.SoulPact.war.service.WarStateCache;
import bm.b0b0b0.SoulPact.war.service.WarTreasuryBridge;
import bm.b0b0b0.SoulPact.war.service.WarCoalitionWithdrawService;
import bm.b0b0b0.SoulPact.war.service.WarKillTracker;
import bm.b0b0b0.SoulPact.war.service.WarVictoryAnnouncer;
import bm.b0b0b0.SoulPact.war.service.WarVictoryService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactWarPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private WarExtension extension;
    private WarBossBarService bossBarService;
    private WarStartupConsolePresenter startupPresenter;
    private WarConfigurationLoader configurationLoader;

    @Override
    public void onEnable() {
        configurationLoader = new WarConfigurationLoader(this);
        WarConfig config = configurationLoader.load();
        WarMessages warMessages = new WarMessages(this, config.locale(), config.fallbackLocale());
        warMessages.load();
        startupPresenter = new WarStartupConsolePresenter(this, warMessages);
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
        WarConfig config = configurationLoader.load();
        WarMessages warMessages = new WarMessages(this, config.locale(), config.fallbackLocale());
        warMessages.load();
        new WarSchemaMigrator(this, api.dataSource()).migrate();
        SqlWarRepository repository = new SqlWarRepository(api);
        WarStateCache stateCache = new WarStateCache();
        WarTreasuryBridge treasuryBridge = new WarTreasuryBridge(api);
        WarSpoilsBridge spoilsBridge = new WarSpoilsBridge(api);
        CoalitionWarBridgeLookup coalitionWarBridgeLookup = new CoalitionWarBridgeLookup(api);
        WarLandBridgeLookup landBridgeLookup = new WarLandBridgeLookup(api);
        WarLandCombatService landCombatService = new WarLandCombatService(
                landBridgeLookup,
                coalitionWarBridgeLookup,
                stateCache
        );
        WarPlayerClanCache playerClanCache = new WarPlayerClanCache();
        WarClanLookup clanLookup = new WarClanLookup(api);
        WarKillTracker killTracker = new WarKillTracker();
        WarVictoryAnnouncer victoryAnnouncer = new WarVictoryAnnouncer(
                warMessages,
                clanLookup,
                coalitionWarBridgeLookup
        );
        bossBarService = new WarBossBarService(
                config,
                warMessages,
                stateCache,
                playerClanCache,
                clanLookup,
                coalitionWarBridgeLookup
        );
        WarCoalitionWithdrawService coalitionWithdrawService = new WarCoalitionWithdrawService(
                api,
                warMessages,
                repository,
                stateCache,
                bossBarService,
                coalitionWarBridgeLookup,
                landCombatService
        );
        WarVictoryService victoryService = new WarVictoryService(
                api,
                repository,
                treasuryBridge,
                spoilsBridge,
                stateCache,
                bossBarService,
                coalitionWarBridgeLookup,
                landCombatService,
                coalitionWithdrawService,
                killTracker,
                victoryAnnouncer,
                clanLookup,
                landBridgeLookup
        );
        WarPendingChatPresenter pendingChatPresenter = new WarPendingChatPresenter(
                api,
                config,
                warMessages,
                clanLookup
        );
        WarFlagRevealPresenter flagRevealPresenter = new WarFlagRevealPresenter(
                api,
                warMessages,
                coalitionWarBridgeLookup
        );
        ClanWarService warService = new ClanWarService(
                api,
                config,
                warMessages,
                repository,
                treasuryBridge,
                stateCache,
                victoryService,
                coalitionWithdrawService,
                playerClanCache,
                pendingChatPresenter,
                bossBarService,
                coalitionWarBridgeLookup,
                landBridgeLookup,
                flagRevealPresenter,
                clanLookup,
                landCombatService,
                killTracker
        );
        WarGuiService guiService = new WarGuiService(api, config, warMessages, warService, clanLookup);
        WarUiBridgeImpl uiBridge = new WarUiBridgeImpl(warService, guiService);
        WarFlagBreakGateImpl flagBreakGate = new WarFlagBreakGateImpl(warService);
        extension = new WarExtension(guiService, uiBridge, flagBreakGate);
        api.extensions().register(extension);
        extension.enable(api);
        warService.bootstrapCache();
        Bukkit.getPluginManager().registerEvents(new WarGuiListener(guiService.clickHandler()), this);
        Bukkit.getPluginManager().registerEvents(new WarPlayerCacheListener(warService), this);
        Bukkit.getPluginManager().registerEvents(new WarCombatKillListener(warService), this);
        WorldGuardWarEventBridge.register(this, api, flagBreakGate);
        WarBossBarTask.start(this, bossBarService, victoryService);
        PluginCommand command = getCommand("clanwar");
        if (command != null) {
            command.setExecutor(new ClanWarCommand(guiService, warService));
        }
        startupPresenter.logRegistered();
    }
}
