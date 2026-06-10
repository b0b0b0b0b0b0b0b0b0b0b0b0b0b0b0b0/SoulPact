package bm.b0b0b0.SoulPact.gladiator;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.gladiator.command.ClanGladAdminCommand;
import bm.b0b0b0.SoulPact.gladiator.command.GladiatorPlayerCommand;
import bm.b0b0b0.SoulPact.gladiator.command.admin.ArenaAdminHandler;
import bm.b0b0b0.SoulPact.gladiator.command.admin.RewardAdminHandler;
import bm.b0b0b0.SoulPact.gladiator.command.admin.SchedulerAdminHandler;
import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfigurationLoader;
import bm.b0b0b0.SoulPact.gladiator.extension.GladiatorExtension;
import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorClanNavigation;
import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorClickHandler;
import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorGuiListener;
import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorGuiService;
import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorMenuPopulator;
import bm.b0b0b0.SoulPact.gladiator.listener.GladiatorDeathListener;
import bm.b0b0b0.SoulPact.gladiator.listener.GladiatorQuitListener;
import bm.b0b0b0.SoulPact.gladiator.listener.GladiatorRespawnListener;
import bm.b0b0b0.SoulPact.gladiator.listener.GladiatorWandListener;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorStartupConsolePresenter;
import bm.b0b0b0.SoulPact.gladiator.migration.GladiatorSchemaMigrator;
import bm.b0b0b0.SoulPact.gladiator.placeholder.GladiatorPlaceholderResolver;
import bm.b0b0b0.SoulPact.gladiator.repository.SqlArenaRepository;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventPresenter;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorEventService;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorRewardDispatcher;
import bm.b0b0b0.SoulPact.gladiator.service.GladiatorScheduleService;
import bm.b0b0b0.SoulPact.gladiator.service.PlayerClanCache;
import bm.b0b0b0.SoulPact.gladiator.service.WandSelectionService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class SoulPactGladiatorPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;
    private static final long TICKS_PER_SECOND = 20L;

    private SoulPactApi api;
    private GladiatorExtension extension;
    private GladiatorConfigurationLoader configurationLoader;
    private GladiatorConfig config;
    private GladiatorMessages gladiatorMessages;
    private GladiatorStartupConsolePresenter startupPresenter;
    private ArenaCatalog catalog;
    private GladiatorEventService eventService;
    private GladiatorScheduleService scheduleService;
    private BukkitTask tickTask;
    private BukkitTask scheduleTask;

    @Override
    public void onEnable() {
        configurationLoader = new GladiatorConfigurationLoader(this);
        config = configurationLoader.load();
        gladiatorMessages = new GladiatorMessages(this, config.locale(), config.fallbackLocale());
        gladiatorMessages.load();
        startupPresenter = new GladiatorStartupConsolePresenter(this, gladiatorMessages);
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
        cancelTasks();
        if (eventService != null) {
            eventService.shutdown();
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
        new GladiatorSchemaMigrator(this, api.dataSource()).migrate();
        catalog = new ArenaCatalog(api, new SqlArenaRepository(api));
        api.scheduler().runAsync(() -> catalog.loadAll());
        PlayerClanCache playerClanCache = new PlayerClanCache(api, config.playerClanCacheMillis());
        GladiatorEventPresenter presenter = new GladiatorEventPresenter(config, gladiatorMessages);
        eventService = new GladiatorEventService(
                api,
                config,
                catalog,
                presenter,
                new GladiatorRewardDispatcher(catalog)
        );
        scheduleService = new GladiatorScheduleService(api, catalog, eventService);
        WandSelectionService selectionService = new WandSelectionService();
        GladiatorGuiService guiService = buildGuiService();
        GladiatorPlaceholderResolver placeholderResolver = new GladiatorPlaceholderResolver(
                gladiatorMessages,
                catalog,
                eventService,
                scheduleService,
                playerClanCache
        );
        extension = new GladiatorExtension(guiService, placeholderResolver, this::reloadModule);
        api.extensions().register(extension);
        extension.enable(api);
        registerListeners(selectionService);
        registerCommands(selectionService, guiService);
        startTasks();
        startupPresenter.logRegistered(catalog.size());
    }

    private GladiatorGuiService buildGuiService() {
        GladiatorMenuPopulator populator = new GladiatorMenuPopulator(config, gladiatorMessages, catalog, eventService);
        return new GladiatorGuiService(() -> config, populator, gladiatorMessages);
    }

    private void registerListeners(WandSelectionService selectionService) {
        GladiatorClanNavigation navigation = new GladiatorClanNavigation(api);
        GladiatorClickHandler clickHandler = new GladiatorClickHandler(
                () -> config,
                gladiatorMessages,
                eventService,
                navigation
        );
        Bukkit.getPluginManager().registerEvents(new GladiatorGuiListener(clickHandler), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorDeathListener(eventService), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorRespawnListener(eventService), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorQuitListener(eventService), this);
        Bukkit.getPluginManager().registerEvents(new GladiatorWandListener(config, gladiatorMessages, selectionService), this);
    }

    private void registerCommands(WandSelectionService selectionService, GladiatorGuiService guiService) {
        PluginCommand playerCommand = getCommand("gladiator");
        if (playerCommand != null) {
            GladiatorPlayerCommand executor =
                    new GladiatorPlayerCommand(gladiatorMessages, catalog, eventService, guiService);
            playerCommand.setExecutor(executor);
            playerCommand.setTabCompleter(executor);
        }
        PluginCommand adminCommand = getCommand("clanglad");
        if (adminCommand != null) {
            ClanGladAdminCommand executor = new ClanGladAdminCommand(
                    () -> config,
                    gladiatorMessages,
                    catalog,
                    eventService,
                    new ArenaAdminHandler(gladiatorMessages, catalog, selectionService),
                    new RewardAdminHandler(gladiatorMessages, catalog),
                    new SchedulerAdminHandler(gladiatorMessages, catalog),
                    this::reloadModule
            );
            adminCommand.setExecutor(executor);
            adminCommand.setTabCompleter(executor);
        }
    }

    private void startTasks() {
        tickTask = Bukkit.getScheduler().runTaskTimer(this, () -> eventService.tick(), TICKS_PER_SECOND, TICKS_PER_SECOND);
        long scheduleTicks = config.scheduleCheckSeconds() * TICKS_PER_SECOND;
        scheduleTask = Bukkit.getScheduler().runTaskTimerAsynchronously(
                this,
                () -> scheduleService.checkDue(),
                scheduleTicks,
                scheduleTicks
        );
    }

    private void cancelTasks() {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        if (scheduleTask != null) {
            scheduleTask.cancel();
            scheduleTask = null;
        }
    }

    private void reloadModule() {
        config = configurationLoader.load();
        gladiatorMessages.load();
        api.scheduler().runAsync(() -> catalog.loadAll());
    }
}
