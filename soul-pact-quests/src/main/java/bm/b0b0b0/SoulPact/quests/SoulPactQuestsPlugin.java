package bm.b0b0b0.SoulPact.quests;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.quests.command.ClanQuestCommand;
import bm.b0b0b0.SoulPact.quests.config.QuestsConfig;
import bm.b0b0b0.SoulPact.quests.config.QuestsConfigurationLoader;
import bm.b0b0b0.SoulPact.quests.extension.QuestsExtension;
import bm.b0b0b0.SoulPact.quests.gui.QuestsGuiListener;
import bm.b0b0b0.SoulPact.quests.gui.QuestsGuiService;
import bm.b0b0b0.SoulPact.quests.listener.QuestBlockListener;
import bm.b0b0b0.SoulPact.quests.listener.QuestFishListener;
import bm.b0b0b0.SoulPact.quests.listener.QuestKillListener;
import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.message.QuestsStartupConsolePresenter;
import bm.b0b0b0.SoulPact.quests.migration.QuestsSchemaMigrator;
import bm.b0b0b0.SoulPact.quests.placeholder.QuestPlaceholderResolver;
import bm.b0b0b0.SoulPact.quests.repository.SqlClanPointsRepository;
import bm.b0b0b0.SoulPact.quests.repository.SqlClanQuestRepository;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import bm.b0b0b0.SoulPact.quests.service.PlayerClanCache;
import bm.b0b0b0.SoulPact.quests.service.QuestCatalog;
import bm.b0b0b0.SoulPact.quests.service.QuestProgressTracker;
import bm.b0b0b0.SoulPact.quests.service.QuestRewardService;
import bm.b0b0b0.SoulPact.quests.service.QuestTreasuryBridge;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class SoulPactQuestsPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;
    private static final long TICKS_PER_SECOND = 20L;

    private SoulPactApi api;
    private QuestsExtension extension;
    private QuestsConfigurationLoader configurationLoader;
    private QuestsConfig config;
    private QuestsMessages questsMessages;
    private QuestsStartupConsolePresenter startupPresenter;
    private QuestCatalog catalog;
    private ClanQuestService questService;
    private BukkitTask flushTask;

    @Override
    public void onEnable() {
        configurationLoader = new QuestsConfigurationLoader(this);
        config = configurationLoader.load();
        questsMessages = new QuestsMessages(this, config.locale(), config.fallbackLocale());
        questsMessages.load();
        startupPresenter = new QuestsStartupConsolePresenter(this, questsMessages);
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
        if (flushTask != null) {
            flushTask.cancel();
            flushTask = null;
        }
        if (questService != null && api != null && api.isDatabaseReady()) {
            questService.flushProgress();
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
        new QuestsSchemaMigrator(this, api.dataSource()).migrate();
        catalog = new QuestCatalog(config.quests());
        QuestProgressTracker tracker = new QuestProgressTracker();
        PlayerClanCache playerClanCache = new PlayerClanCache(api, config.playerClanCacheMillis());
        QuestTreasuryBridge treasuryBridge = new QuestTreasuryBridge(api);
        QuestRewardService rewardService = new QuestRewardService(api, new SqlClanPointsRepository(api), treasuryBridge);
        questService = new ClanQuestService(
                api,
                config,
                catalog,
                new SqlClanQuestRepository(api),
                tracker,
                playerClanCache,
                rewardService,
                questsMessages
        );
        QuestsGuiService guiService = new QuestsGuiService(api, config, questsMessages, questService);
        QuestPlaceholderResolver placeholderResolver = new QuestPlaceholderResolver(questService, questsMessages);
        extension = new QuestsExtension(guiService, placeholderResolver, this::reloadModule);
        api.extensions().register(extension);
        extension.enable(api);
        registerListeners(guiService);
        registerCommand(guiService);
        questService.loadActiveStates();
        startFlushTask();
        startupPresenter.logRegistered(catalog.size(), treasuryBridge.available());
    }

    private void registerListeners(QuestsGuiService guiService) {
        Bukkit.getPluginManager().registerEvents(new QuestsGuiListener(guiService.clickHandler()), this);
        Bukkit.getPluginManager().registerEvents(new QuestKillListener(questService), this);
        Bukkit.getPluginManager().registerEvents(new QuestBlockListener(questService), this);
        Bukkit.getPluginManager().registerEvents(new QuestFishListener(questService), this);
    }

    private void registerCommand(QuestsGuiService guiService) {
        PluginCommand command = getCommand("clanquest");
        if (command != null) {
            ClanQuestCommand executor = new ClanQuestCommand(api, guiService, questService, catalog, questsMessages);
            command.setExecutor(executor);
            command.setTabCompleter(executor);
        }
    }

    private void startFlushTask() {
        long intervalTicks = config.progressFlushSeconds() * TICKS_PER_SECOND;
        flushTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> questService.flushProgress(),
                intervalTicks,
                intervalTicks
        );
    }

    private void reloadModule() {
        config = configurationLoader.load();
        questsMessages.load();
        if (catalog != null) {
            catalog.update(config.quests());
        }
    }
}
