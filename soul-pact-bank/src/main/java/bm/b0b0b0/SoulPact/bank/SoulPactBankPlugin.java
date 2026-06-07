package bm.b0b0b0.SoulPact.bank;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.bank.config.BankConfig;
import bm.b0b0b0.SoulPact.bank.config.BankConfigLoader;
import bm.b0b0b0.SoulPact.bank.economy.VaultGateway;
import bm.b0b0b0.SoulPact.bank.extension.BankExtension;
import bm.b0b0b0.SoulPact.bank.gui.BankGuiListener;
import bm.b0b0b0.SoulPact.bank.gui.BankGuiService;
import bm.b0b0b0.SoulPact.bank.message.BankMessages;
import bm.b0b0b0.SoulPact.bank.message.BankStartupConsolePresenter;
import bm.b0b0b0.SoulPact.bank.migration.BankSchemaMigrator;
import bm.b0b0b0.SoulPact.bank.repository.SqlClanTreasuryRepository;
import bm.b0b0b0.SoulPact.bank.service.ClanTreasuryService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactBankPlugin extends JavaPlugin {

    private static final int BOOTSTRAP_MAX_ATTEMPTS = 60;
    private static final long BOOTSTRAP_DELAY_TICKS = 5L;

    private SoulPactApi api;
    private BankExtension extension;
    private BankGuiService guiService;
    private BankMessages bankMessages;
    private BankStartupConsolePresenter startupPresenter;
    private BankConfig config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = BankConfigLoader.load(this);
        bankMessages = new BankMessages(this, config.locale(), config.fallbackLocale());
        bankMessages.load();
        startupPresenter = new BankStartupConsolePresenter(this, bankMessages);
        scheduleBootstrap(0);
    }

    @Override
    public void onDisable() {
        if (api != null && extension != null) {
            api.extensions().unregister(extension.id());
        }
    }

    public BankGuiService guiService() {
        return guiService;
    }

    public BankMessages messages() {
        return bankMessages;
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
        new BankSchemaMigrator(this, api.dataSource()).migrate();
        VaultGateway vaultGateway = new VaultGateway();
        vaultGateway.hook();
        SqlClanTreasuryRepository treasuryRepository = new SqlClanTreasuryRepository(api);
        ClanTreasuryService treasuryService = new ClanTreasuryService(
                api,
                treasuryRepository,
                vaultGateway,
                config,
                bankMessages
        );
        guiService = new BankGuiService(api, config, bankMessages, treasuryService, vaultGateway);
        extension = new BankExtension(treasuryService, guiService);
        api.extensions().register(extension);
        extension.enable(api);
        Bukkit.getPluginManager().registerEvents(
                new BankGuiListener(guiService.clickHandler()),
                this
        );
        PluginCommand command = getCommand("clanbank");
        if (command != null) {
            command.setExecutor((sender, unused, label, args) -> {
                if (!(sender instanceof Player player)) {
                    return true;
                }
                guiService.open(player);
                return true;
            });
        }
        startupPresenter.logRegistered(vaultGateway);
    }
}
