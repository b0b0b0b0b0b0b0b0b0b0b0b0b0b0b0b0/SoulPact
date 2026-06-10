package bm.b0b0b0.SoulPact.leaderboard;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.leaderboard.command.ClanBoardCommand;
import bm.b0b0b0.SoulPact.leaderboard.config.LeaderboardConfig;
import bm.b0b0b0.SoulPact.leaderboard.config.LeaderboardConfigurationLoader;
import bm.b0b0b0.SoulPact.leaderboard.extension.LeaderboardExtension;
import bm.b0b0b0.SoulPact.leaderboard.listener.LeaderboardEventListener;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardStartupConsolePresenter;
import bm.b0b0b0.SoulPact.leaderboard.migration.LeaderboardSchemaMigrator;
import bm.b0b0b0.SoulPact.leaderboard.placeholder.LeaderboardPlaceholderResolver;
import bm.b0b0b0.SoulPact.leaderboard.render.BoardEntityTag;
import bm.b0b0b0.SoulPact.leaderboard.render.BoardPlaceholders;
import bm.b0b0b0.SoulPact.leaderboard.render.BoardRenderService;
import bm.b0b0b0.SoulPact.leaderboard.render.HologramBoardRenderer;
import bm.b0b0b0.SoulPact.leaderboard.render.SignBoardRenderer;
import bm.b0b0b0.SoulPact.leaderboard.render.StandBoardRenderer;
import bm.b0b0b0.SoulPact.leaderboard.repository.BoardRepository;
import bm.b0b0b0.SoulPact.leaderboard.repository.ClanStandingQuery;
import bm.b0b0b0.SoulPact.leaderboard.repository.SqlBoardRepository;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardCatalog;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardCreationService;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardUpdateService;
import bm.b0b0b0.SoulPact.leaderboard.service.StandingsCache;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class SoulPactLeaderboardPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;
    private static final long TICKS_PER_SECOND = 20L;

    private SoulPactApi api;
    private LeaderboardExtension extension;
    private LeaderboardConfigurationLoader configurationLoader;
    private LeaderboardConfig config;
    private LeaderboardMessages messages;
    private LeaderboardStartupConsolePresenter startupPresenter;
    private BoardCatalog catalog;
    private BoardUpdateService updateService;
    private BukkitTask updateTask;

    @Override
    public void onEnable() {
        configurationLoader = new LeaderboardConfigurationLoader(this);
        config = configurationLoader.load();
        messages = new LeaderboardMessages(this, config.locale(), config.fallbackLocale());
        messages.load();
        startupPresenter = new LeaderboardStartupConsolePresenter(this, messages);
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
        if (updateTask != null) {
            updateTask.cancel();
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
        BoardRepository repository = new SqlBoardRepository(api.dataSource());
        ClanStandingQuery standingQuery = new ClanStandingQuery(api.dataSource(), getLogger());
        StandingsCache standingsCache = new StandingsCache(standingQuery);
        catalog = new BoardCatalog();
        BoardEntityTag entityTag = new BoardEntityTag(this);
        BoardPlaceholders boardPlaceholders = new BoardPlaceholders(messages);
        BoardRenderService renderService = new BoardRenderService(
                standingsCache,
                boardPlaceholders,
                new SignBoardRenderer(messages),
                new StandBoardRenderer(messages, entityTag, () -> config),
                new HologramBoardRenderer(messages, entityTag, () -> config)
        );
        updateService = new BoardUpdateService(api, catalog, standingsCache, renderService, () -> config);
        BoardCreationService creationService = new BoardCreationService(api, repository, catalog, updateService, renderService);
        LeaderboardPlaceholderResolver placeholderResolver = new LeaderboardPlaceholderResolver(standingsCache, messages);
        extension = new LeaderboardExtension(placeholderResolver, this::reloadModule);

        api.scheduler().runAsync(() -> {
            new LeaderboardSchemaMigrator(this, api.dataSource()).migrate();
            var boards = repository.findAll();
            api.scheduler().runSync(() -> {
                catalog.replaceAll(boards);
                api.extensions().register(extension);
                extension.enable(api);
                registerCommand(creationService);
                Bukkit.getPluginManager().registerEvents(new LeaderboardEventListener(updateService), this);
                startUpdateTask();
                updateService.updateAll();
                startupPresenter.logRegistered(boards.size());
            });
        });
    }

    private void registerCommand(BoardCreationService creationService) {
        PluginCommand command = getCommand("clanboard");
        if (command == null) {
            return;
        }
        ClanBoardCommand executor = new ClanBoardCommand(
                messages,
                () -> config,
                catalog,
                creationService,
                updateService,
                this::reloadModule
        );
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    private void startUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel();
        }
        long intervalTicks = config.updateIntervalSeconds() * TICKS_PER_SECOND;
        updateTask = Bukkit.getScheduler().runTaskTimer(this, () -> updateService.updateAll(), intervalTicks, intervalTicks);
    }

    private void reloadModule() {
        config = configurationLoader.load();
        messages.load();
        startUpdateTask();
        updateService.updateAll();
    }
}
