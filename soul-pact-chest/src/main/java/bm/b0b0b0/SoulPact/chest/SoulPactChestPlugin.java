package bm.b0b0b0.SoulPact.chest;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import bm.b0b0b0.SoulPact.chest.config.ChestConfigurationLoader;
import bm.b0b0b0.SoulPact.chest.economy.ChestVaultGateway;
import bm.b0b0b0.SoulPact.chest.extension.ChestExtension;
import bm.b0b0b0.SoulPact.chest.gui.ChestGuiListener;
import bm.b0b0b0.SoulPact.chest.gui.ChestGuiService;
import bm.b0b0b0.SoulPact.chest.message.ChestMessages;
import bm.b0b0b0.SoulPact.chest.message.ChestStartupConsolePresenter;
import bm.b0b0b0.SoulPact.chest.migration.ChestSchemaMigrator;
import bm.b0b0b0.SoulPact.chest.repository.SqlClanChestRepository;
import bm.b0b0b0.SoulPact.chest.repository.SqlClanChestSpoilsRepository;
import bm.b0b0b0.SoulPact.chest.service.ClanChestSpoilsService;
import bm.b0b0b0.SoulPact.chest.service.ChestAccessService;
import bm.b0b0b0.SoulPact.chest.service.ChestGuiLayout;
import bm.b0b0b0.SoulPact.chest.service.ChestPaymentService;
import bm.b0b0b0.SoulPact.chest.service.ClanChestService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactChestPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private ChestExtension extension;
    private ChestGuiService guiService;
    private ChestMessages chestMessages;
    private ChestStartupConsolePresenter startupPresenter;
    private ChestConfigurationLoader configurationLoader;
    private ChestConfig config;

    @Override
    public void onEnable() {
        configurationLoader = new ChestConfigurationLoader(this);
        config = configurationLoader.load();
        chestMessages = new ChestMessages(this, config.locale(), config.fallbackLocale());
        chestMessages.load();
        startupPresenter = new ChestStartupConsolePresenter(this, chestMessages);
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
        new ChestSchemaMigrator(this, api.dataSource()).migrate();
        ChestVaultGateway vaultGateway = new ChestVaultGateway();
        vaultGateway.hook();
        SqlClanChestRepository repository = new SqlClanChestRepository(api);
        SqlClanChestSpoilsRepository spoilsRepository = new SqlClanChestSpoilsRepository(api, repository);
        ClanChestSpoilsService spoilsService = new ClanChestSpoilsService(api, spoilsRepository);
        ChestAccessService accessService = new ChestAccessService(api);
        ChestPaymentService paymentService = new ChestPaymentService(api, vaultGateway);
        ClanChestService chestService = new ClanChestService(
                api,
                config,
                chestMessages,
                repository,
                accessService,
                paymentService
        );
        ChestGuiLayout layout = new ChestGuiLayout(config);
        guiService = new ChestGuiService(api, config, chestMessages, chestService, layout);
        extension = new ChestExtension(guiService, spoilsService);
        api.extensions().register(extension);
        extension.enable(api);
        Bukkit.getPluginManager().registerEvents(
                new ChestGuiListener(guiService, guiService.clickHandler(), layout),
                this
        );
        PluginCommand command = getCommand("clanchest");
        if (command != null) {
            command.setExecutor((sender, unused, label, args) -> {
                if (!(sender instanceof Player player)) {
                    return true;
                }
                guiService.open(player);
                return true;
            });
        }
        startupPresenter.logRegistered(vaultGateway, paymentService.usesTreasury());
    }
}
