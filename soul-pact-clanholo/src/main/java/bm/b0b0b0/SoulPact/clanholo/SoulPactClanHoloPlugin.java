package bm.b0b0b0.SoulPact.clanholo;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.clanholo.command.ClanHoloCommand;
import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfigurationLoader;
import bm.b0b0b0.SoulPact.clanholo.extension.ClanHoloExtension;
import bm.b0b0b0.SoulPact.clanholo.gate.ClanBaseLocationGate;
import bm.b0b0b0.SoulPact.clanholo.listener.ClanHoloEventListener;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloMessages;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloStartupConsolePresenter;
import bm.b0b0b0.SoulPact.clanholo.migration.ClanHoloSchemaMigrator;
import bm.b0b0b0.SoulPact.clanholo.render.ClanHologramRenderer;
import bm.b0b0b0.SoulPact.clanholo.render.HologramEntityTag;
import bm.b0b0b0.SoulPact.clanholo.repository.HologramRepository;
import bm.b0b0b0.SoulPact.clanholo.repository.SqlHologramRepository;
import bm.b0b0b0.SoulPact.clanholo.service.HologramLimitResolver;
import bm.b0b0b0.SoulPact.clanholo.service.HologramPlaceholderBuilder;
import bm.b0b0b0.SoulPact.clanholo.service.HologramService;
import bm.b0b0b0.SoulPact.clanholo.service.HologramSessionStore;
import bm.b0b0b0.SoulPact.clanholo.service.HologramTemplateService;
import bm.b0b0b0.SoulPact.clanholo.validation.ContentValidator;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactClanHoloPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private ClanHoloExtension extension;
    private ClanHoloConfigurationLoader configurationLoader;
    private ClanHoloConfig config;
    private ClanHoloMessages messages;
    private HologramService hologramService;

    @Override
    public void onEnable() {
        configurationLoader = new ClanHoloConfigurationLoader(this);
        config = configurationLoader.load();
        messages = new ClanHoloMessages(this, config.locale(), config.fallbackLocale());
        messages.load();
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
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
                new ClanHoloStartupConsolePresenter(this, messages).logCoreMissing();
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
            scheduleBootstrap(attempt + 1);
            return;
        }
        if (!resolved.isDatabaseReady()) {
            if (attempt >= BOOTSTRAP_MAX_ATTEMPTS) {
                new ClanHoloStartupConsolePresenter(this, messages).logCoreMissing();
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
        HologramRepository repository = new SqlHologramRepository(api.dataSource());
        HologramEntityTag entityTag = new HologramEntityTag(this);
        ClanHologramRenderer renderer = new ClanHologramRenderer(entityTag, this::config);
        HologramSessionStore sessionStore = new HologramSessionStore();
        HologramLimitResolver limitResolver = new HologramLimitResolver(this::config);
        HologramPlaceholderBuilder placeholderBuilder = new HologramPlaceholderBuilder(api.dataSource());
        HologramTemplateService templateService = new HologramTemplateService(messages, this::config);
        ContentValidator contentValidator = new ContentValidator(config);
        hologramService = new HologramService(
                api,
                repository,
                renderer,
                new ClanBaseLocationGate(),
                contentValidator,
                sessionStore,
                limitResolver,
                templateService,
                placeholderBuilder,
                messages,
                this::config
        );
        extension = new ClanHoloExtension(messages, this::reloadModule);

        api.scheduler().runAsync(() -> {
            try {
                new ClanHoloSchemaMigrator(this, api.dataSource()).migrate();
            } catch (Exception exception) {
                getLogger().severe("Clan hologram migration failed: " + exception.getMessage());
                api.scheduler().runSync(() -> Bukkit.getPluginManager().disablePlugin(this));
                return;
            }
            var holograms = repository.findAll().join();
            api.scheduler().runSync(() -> {
                api.extensions().register(extension);
                extension.enable(api);
                registerCommand();
                Bukkit.getPluginManager().registerEvents(new ClanHoloEventListener(repository, hologramService), this);
                hologramService.renderAll(holograms);
                new ClanHoloStartupConsolePresenter(this, messages).logRegistered(holograms.size());
            });
        });
    }

    private void registerCommand() {
        PluginCommand command = getCommand("clanholo");
        if (command == null) {
            return;
        }
        ClanHoloCommand executor = new ClanHoloCommand(messages, this::config, hologramService, this::reloadModule);
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    private ClanHoloConfig config() {
        return config;
    }

    private void reloadModule() {
        config = configurationLoader.load();
        messages.load();
        repositoryReloadRender();
    }

    private void repositoryReloadRender() {
        SqlHologramRepository repository = new SqlHologramRepository(api.dataSource());
        repository.findAll().thenAccept(holograms -> api.scheduler().runSync(() -> hologramService.renderAll(holograms)));
    }
}
