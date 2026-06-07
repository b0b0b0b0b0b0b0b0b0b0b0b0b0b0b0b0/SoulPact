package bm.b0b0b0.SoulPact.war;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.bridge.WarFlagBreakGateImpl;
import bm.b0b0b0.SoulPact.war.bridge.WarUiBridgeImpl;
import bm.b0b0b0.SoulPact.war.command.ClanWarCommand;
import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.config.WarConfigLoader;
import bm.b0b0b0.SoulPact.war.extension.WarExtension;
import bm.b0b0b0.SoulPact.war.gui.WarGuiListener;
import bm.b0b0b0.SoulPact.war.gui.WarGuiService;
import bm.b0b0b0.SoulPact.war.integration.WorldGuardWarEventBridge;
import bm.b0b0b0.SoulPact.war.listener.WarBossBarTask;
import bm.b0b0b0.SoulPact.war.listener.WarPlayerCacheListener;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.message.WarPendingChatPresenter;
import bm.b0b0b0.SoulPact.war.message.WarStartupConsolePresenter;
import bm.b0b0b0.SoulPact.war.migration.WarSchemaMigrator;
import bm.b0b0b0.SoulPact.war.repository.SqlWarRepository;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import bm.b0b0b0.SoulPact.war.service.CoalitionWarBridgeLookup;
import bm.b0b0b0.SoulPact.war.service.WarBossBarService;
import bm.b0b0b0.SoulPact.war.service.WarClanLookup;
import bm.b0b0b0.SoulPact.war.service.WarPlayerClanCache;
import bm.b0b0b0.SoulPact.war.service.WarSpoilsBridge;
import bm.b0b0b0.SoulPact.war.service.WarStateCache;
import bm.b0b0b0.SoulPact.war.service.WarTreasuryBridge;
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

    @Override
    public void onEnable() {
        saveDefaultConfig();
        WarConfig config = WarConfigLoader.load(this);
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
        WarConfig config = WarConfigLoader.load(this);
        WarMessages warMessages = new WarMessages(this, config.locale(), config.fallbackLocale());
        warMessages.load();
        new WarSchemaMigrator(this, api.dataSource()).migrate();
        SqlWarRepository repository = new SqlWarRepository(api);
        WarStateCache stateCache = new WarStateCache();
        WarTreasuryBridge treasuryBridge = new WarTreasuryBridge(api);
        WarSpoilsBridge spoilsBridge = new WarSpoilsBridge(api);
        CoalitionWarBridgeLookup coalitionWarBridgeLookup = new CoalitionWarBridgeLookup(api);
        WarPlayerClanCache playerClanCache = new WarPlayerClanCache();
        WarClanLookup clanLookup = new WarClanLookup(api);
        bossBarService = new WarBossBarService(config, warMessages, stateCache, playerClanCache);
        WarVictoryService victoryService = new WarVictoryService(
                api,
                warMessages,
                repository,
                treasuryBridge,
                spoilsBridge,
                stateCache,
                bossBarService,
                coalitionWarBridgeLookup
        );
        WarPendingChatPresenter pendingChatPresenter = new WarPendingChatPresenter(warMessages, clanLookup);
        ClanWarService warService = new ClanWarService(
                api,
                config,
                warMessages,
                repository,
                treasuryBridge,
                stateCache,
                victoryService,
                playerClanCache,
                pendingChatPresenter,
                bossBarService,
                coalitionWarBridgeLookup
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
        WorldGuardWarEventBridge.register(this, api, flagBreakGate);
        WarBossBarTask.start(this, bossBarService);
        PluginCommand command = getCommand("clanwar");
        if (command != null) {
            command.setExecutor(new ClanWarCommand(guiService, warService));
        }
        startupPresenter.logRegistered();
    }
}
