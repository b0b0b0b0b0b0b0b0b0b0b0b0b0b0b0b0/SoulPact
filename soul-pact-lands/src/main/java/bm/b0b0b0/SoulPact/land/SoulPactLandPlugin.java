package bm.b0b0b0.SoulPact.land;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.config.LandConfigLoader;
import bm.b0b0b0.SoulPact.land.extension.LandExtension;
import bm.b0b0b0.SoulPact.land.gui.LandGuiListener;
import bm.b0b0b0.SoulPact.land.gui.LandGuiService;
import bm.b0b0b0.SoulPact.land.economy.LandVaultGateway;
import bm.b0b0b0.SoulPact.land.listener.BaseWorldListener;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.message.LandStartupConsolePresenter;
import bm.b0b0b0.SoulPact.land.migration.LandSchemaMigrator;
import bm.b0b0b0.SoulPact.land.repository.SqlClanBaseRepository;
import bm.b0b0b0.SoulPact.land.repository.SqlClanMemberUuidRepository;
import bm.b0b0b0.SoulPact.land.integration.WorldGuardGateway;
import bm.b0b0b0.SoulPact.land.service.BaseBorderService;
import bm.b0b0b0.SoulPact.land.service.BaseExpansionPaymentService;
import bm.b0b0b0.SoulPact.land.service.BaseFlagIndex;
import bm.b0b0b0.SoulPact.land.service.BorderBlockIndex;
import bm.b0b0b0.SoulPact.land.service.BorderIndexBootstrap;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactLandPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private LandExtension extension;
    private LandGuiService guiService;
    private LandMessages landMessages;
    private LandStartupConsolePresenter startupPresenter;
    private LandConfig config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = LandConfigLoader.load(this);
        landMessages = new LandMessages(this, config.locale(), config.fallbackLocale());
        landMessages.load();
        startupPresenter = new LandStartupConsolePresenter(this, landMessages);
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
        new LandSchemaMigrator(this, api.dataSource()).migrate();
        WorldGuardGateway worldGuardGateway = new WorldGuardGateway();
        BorderBlockIndex borderBlockIndex = new BorderBlockIndex();
        SqlClanBaseRepository baseRepository = new SqlClanBaseRepository(api);
        BaseFlagIndex flagIndex = new BaseFlagIndex();
        new BorderIndexBootstrap(api, baseRepository, borderBlockIndex, flagIndex).loadAll();
        BaseBorderService borderService = new BaseBorderService(config, borderBlockIndex);
        SqlClanMemberUuidRepository memberRepository = new SqlClanMemberUuidRepository(api);
        LandVaultGateway vaultGateway = new LandVaultGateway();
        vaultGateway.hook();
        BaseExpansionPaymentService paymentService = new BaseExpansionPaymentService(api, vaultGateway);
        ClanBaseService baseService = new ClanBaseService(
                api,
                config,
                landMessages,
                baseRepository,
                memberRepository,
                worldGuardGateway,
                borderService,
                paymentService,
                flagIndex
        );
        guiService = new LandGuiService(api, config, landMessages, baseService);
        extension = new LandExtension(baseService, guiService);
        api.extensions().register(extension);
        extension.enable(api);
        Bukkit.getPluginManager().registerEvents(
                new LandGuiListener(guiService.clickHandler()),
                this
        );
        Bukkit.getPluginManager().registerEvents(
                new BaseWorldListener(api, baseService, borderBlockIndex, flagIndex, landMessages),
                this
        );
        PluginCommand command = getCommand("clanland");
        if (command != null) {
            command.setExecutor((sender, unused, label, args) -> {
                if (!(sender instanceof Player player)) {
                    return true;
                }
                guiService.open(player);
                return true;
            });
        }
        startupPresenter.logRegistered(worldGuardGateway);
        Bukkit.getScheduler().runTaskLater(this, () -> baseService.reconcileDeployedFlags(), 40L);
    }
}
