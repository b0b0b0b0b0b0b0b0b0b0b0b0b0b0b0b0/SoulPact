package bm.b0b0b0.SoulPact.core;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.platform.SoulPactClanGui;
import bm.b0b0b0.SoulPact.clan.command.ClanCommandRegistrar;
import bm.b0b0b0.SoulPact.clan.gui.ClanCreateChatPrompt;
import bm.b0b0b0.SoulPact.clan.gui.ClanGuiClickDispatcher;
import bm.b0b0b0.SoulPact.clan.gui.ClanGuiOpenService;
import bm.b0b0b0.SoulPact.clan.gui.ClanHubClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanHubMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanExtensionsClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanExtensionsMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanListClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanListMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanProfileClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanProfileMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.GuiItemBuilder;
import bm.b0b0b0.SoulPact.clan.gui.ClanInfoClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanInfoMenuPopulator;
import bm.b0b0b0.SoulPact.clan.listener.ClanGuiListener;
import bm.b0b0b0.SoulPact.clan.listener.ClanPendingJoinListener;
import bm.b0b0b0.SoulPact.clan.message.ClanPendingChatPresenter;
import bm.b0b0b0.SoulPact.clan.gui.ClanMemberDetailClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanMemberDetailMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanMemberKickConfirmClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanMemberKickConfirmMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanMembersClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanMembersMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanRequestDetailClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanRequestDetailMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanRequestsClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanRequestsMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanRoleSettingsClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanBannerClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanBannerMenuPopulator;
import bm.b0b0b0.SoulPact.clan.repository.ClanBannerRepository;
import bm.b0b0b0.SoulPact.clan.repository.SqlClanBannerRepository;
import bm.b0b0b0.SoulPact.clan.listener.ClanStandardListener;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardItem;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardKeys;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardPresence;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardService;
import bm.b0b0b0.SoulPact.clan.service.ClanBannerDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanBannerService;
import bm.b0b0b0.SoulPact.clan.gui.ClanRoleSettingsMenuPopulator;
import bm.b0b0b0.SoulPact.clan.gui.ClanSettingsClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanSettingsMenuPopulator;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipHistoryRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRolePermissionRepository;
import bm.b0b0b0.SoulPact.clan.repository.SqlClanRolePermissionRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipNotificationRepository;
import bm.b0b0b0.SoulPact.clan.repository.SqlClanMembershipHistoryRepository;
import bm.b0b0b0.SoulPact.clan.repository.SqlClanMembershipNotificationRepository;
import bm.b0b0b0.SoulPact.clan.service.ClanMembershipHistoryService;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberManagementPlanner;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberManagementService;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberDetailDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberDetailLoreBuilder;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersSlotLayout;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestDetailDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestHistoryLoreBuilder;
import bm.b0b0b0.SoulPact.clan.service.ClanRolePermissionService;
import bm.b0b0b0.SoulPact.clan.service.ClanRoleSettingsDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanSettingsDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestsDataService;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipRepository;
import bm.b0b0b0.SoulPact.clan.repository.SqlClanMembershipRepository;
import bm.b0b0b0.SoulPact.clan.service.ClanInfoViewDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembershipService;
import bm.b0b0b0.SoulPact.clan.service.ClanTargetResolver;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.repository.SqlClanRepository;
import bm.b0b0b0.SoulPact.clan.runtime.ClanRuntimeHolder;
import bm.b0b0b0.SoulPact.clan.runtime.ClanRuntimeServices;
import bm.b0b0b0.SoulPact.clan.message.ClanHelpChatPresenter;
import bm.b0b0b0.SoulPact.clan.message.ClanInfoChatPresenter;
import bm.b0b0b0.SoulPact.clan.message.ClanListChatPresenter;
import bm.b0b0b0.SoulPact.clan.service.ClanCreateEconomy;
import bm.b0b0b0.SoulPact.clan.service.ClanCreateService;
import bm.b0b0b0.SoulPact.clan.service.ClanCreateValidator;
import bm.b0b0b0.SoulPact.clan.service.ClanDisbandService;
import bm.b0b0b0.SoulPact.clan.service.ClanEconomyMessages;
import bm.b0b0b0.SoulPact.clan.service.ClanHubDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanInfoService;
import bm.b0b0b0.SoulPact.clan.service.ClanKickService;
import bm.b0b0b0.SoulPact.clan.service.ClanLeaveService;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberHistoryLoreBuilder;
import bm.b0b0b0.SoulPact.clan.service.ClanExtensionsDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanHubModuleSlotService;
import bm.b0b0b0.SoulPact.clan.service.ExtensionDisplayService;
import bm.b0b0b0.SoulPact.clan.service.ClanListDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanProfileDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanProfileMembersLoreBuilder;
import bm.b0b0b0.SoulPact.clan.service.ClanQueryService;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.api.SoulPactApiImpl;
import bm.b0b0b0.SoulPact.core.api.SoulPactClanStandardImpl;
import bm.b0b0b0.SoulPact.core.api.SoulPactClanAccessImpl;
import bm.b0b0b0.SoulPact.core.api.SoulPactClanGuiImpl;
import bm.b0b0b0.SoulPact.core.api.SoulPactSchedulerImpl;
import bm.b0b0b0.SoulPact.core.config.ConfigurationLoader;
import bm.b0b0b0.SoulPact.core.config.PluginConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.database.DatabaseBootstrap;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import bm.b0b0b0.SoulPact.core.integration.IntegrationBootstrap;
import bm.b0b0b0.SoulPact.core.integration.PlayerHeadSkinApplier;
import bm.b0b0b0.SoulPact.core.integration.IntegrationRegistry;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.message.StartupConsolePresenter;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import bm.b0b0b0.SoulPact.core.module.ClanExtensionMembershipNotifier;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactApplication {

    private final JavaPlugin plugin;
    private final ConfigurationLoader configurationLoader;
    private final DataSourceProvider dataSourceProvider;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ExtensionRegistryImpl extensionRegistry;
    private final IntegrationRegistry integrationRegistry;
    private final IntegrationBootstrap integrationBootstrap;
    private final ClanRuntimeHolder clanRuntimeHolder;
    private MessageService messageService;
    private PluginConfig pluginConfig;
    private DatabaseBootstrap databaseBootstrap;
    private SoulPactApiImpl soulPactApi;

    public SoulPactApplication(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configurationLoader = new ConfigurationLoader(plugin);
        this.dataSourceProvider = new DataSourceProvider();
        this.asyncDatabaseExecutor = new AsyncDatabaseExecutor(plugin);
        this.extensionRegistry = new ExtensionRegistryImpl();
        this.integrationRegistry = new IntegrationRegistry();
        this.integrationBootstrap = new IntegrationBootstrap(integrationRegistry);
        this.clanRuntimeHolder = new ClanRuntimeHolder();
    }

    public void enable() {
        pluginConfig = configurationLoader.load();
        messageService = new MessageService(plugin, pluginConfig.locale());
        messageService.load();
        integrationBootstrap.registerDefaults();
        integrationBootstrap.hookAll();
        new StartupConsolePresenter(plugin, messageService).logStartupHeader(
                integrationRegistry,
                pluginConfig.economy(),
                integrationBootstrap.vaultIntegration()
        );
        registerCommands();
        databaseBootstrap = new DatabaseBootstrap(
                plugin,
                pluginConfig.database(),
                dataSourceProvider,
                asyncDatabaseExecutor
        );
        databaseBootstrap.start().thenAccept(success -> asyncDatabaseExecutor.runSync(() -> completeEnable(success)));
    }

    public void disable() {
        plugin.getServer().getServicesManager().unregisterAll(plugin);
        if (databaseBootstrap != null) {
            databaseBootstrap.shutdown();
        }
        extensionRegistry.disableAll();
    }

    public void reload() {
        pluginConfig = configurationLoader.load();
        messageService = new MessageService(plugin, pluginConfig.locale());
        messageService.load();
        integrationBootstrap.hookAll();
        PlayerHeadSkinApplier.clearCache();
        new StartupConsolePresenter(plugin, messageService).logReloadComplete(
                integrationRegistry,
                pluginConfig.economy(),
                integrationBootstrap.vaultIntegration()
        );
        extensionRegistry.reloadAll();
    }

    public SoulPactApi api() {
        return soulPactApi;
    }

    private void registerCommands() {
        ClanCommandRegistrar clanCommandRegistrar = new ClanCommandRegistrar(
                pluginConfig.permissions(),
                messageService,
                clanRuntimeHolder,
                dataSourceProvider
        );
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            Commands registrar = event.registrar();
            clanCommandRegistrar.register(registrar);
        });
    }

    private void completeEnable(boolean success) {
        if (!success) {
            new StartupConsolePresenter(plugin, messageService).logStartupFailed();
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }
        wireServices();
        new StartupConsolePresenter(plugin, messageService).logStartupComplete(extensionRegistry);
        plugin.getServer().getPluginManager().registerEvents(
                new ClanGuiListener(clanRuntimeHolder.services().guiClickDispatcher()),
                plugin
        );
        plugin.getServer().getPluginManager().registerEvents(
                new ClanPendingJoinListener(clanRuntimeHolder, dataSourceProvider),
                plugin
        );
        plugin.getServer().getPluginManager().registerEvents(
                new ClanStandardListener(clanStandardItem, standardPresence, clanStandardService),
                plugin
        );
    }

    private ClanStandardItem clanStandardItem;
    private ClanStandardPresence standardPresence;
    private ClanStandardService clanStandardService;

    private void wireServices() {
        ClanRepository clanRepository = new SqlClanRepository(dataSourceProvider, asyncDatabaseExecutor);
        ClanBannerRepository clanBannerRepository = new SqlClanBannerRepository(dataSourceProvider, asyncDatabaseExecutor);
        ClanBannerService clanBannerService = new ClanBannerService(clanBannerRepository);
        ClanQueryService clanQueryService = new ClanQueryService(clanRepository);
        RoleThemeService roleThemeService = new RoleThemeService(plugin, pluginConfig.locale(), pluginConfig.clan());
        PlayerHeadSkinApplier playerHeadSkinApplier = new PlayerHeadSkinApplier(
                integrationBootstrap.skinRestorerIntegration()
        );
        GuiItemBuilder guiItemBuilder = new GuiItemBuilder(messageService, playerHeadSkinApplier);
        ExtensionDisplayService extensionDisplayService = new ExtensionDisplayService(messageService);
        ClanHubModuleSlotService hubModuleSlotService = new ClanHubModuleSlotService(
                extensionRegistry,
                pluginConfig.guiHub()
        );
        ClanStandardKeys standardKeys = new ClanStandardKeys(plugin);
        this.clanStandardItem = new ClanStandardItem(standardKeys, messageService);
        this.standardPresence = new ClanStandardPresence();
        ClanHubMenuPopulator hubMenuPopulator = new ClanHubMenuPopulator(
                pluginConfig.guiHub(),
                guiItemBuilder,
                extensionDisplayService
        );
        ClanProfileMembersLoreBuilder profileMembersLoreBuilder = new ClanProfileMembersLoreBuilder(
                messageService,
                roleThemeService
        );
        ClanEconomyMessages clanEconomyMessages = new ClanEconomyMessages(
                pluginConfig.economy(),
                integrationBootstrap.vaultIntegration(),
                messageService
        );
        ClanProfileMenuPopulator profileMenuPopulator = new ClanProfileMenuPopulator(
                pluginConfig.guiProfile(),
                guiItemBuilder,
                profileMembersLoreBuilder,
                messageService,
                clanEconomyMessages
        );
        ClanMembershipRepository membershipRepository = new SqlClanMembershipRepository(
                dataSourceProvider,
                asyncDatabaseExecutor
        );
        ClanMembershipHistoryRepository historyRepository = new SqlClanMembershipHistoryRepository(
                dataSourceProvider,
                asyncDatabaseExecutor
        );
        ClanRolePermissionRepository rolePermissionRepository = new SqlClanRolePermissionRepository(
                dataSourceProvider,
                asyncDatabaseExecutor
        );
        ClanRolePermissionService rolePermissionService = new ClanRolePermissionService(
                rolePermissionRepository,
                clanRepository,
                pluginConfig.clan(),
                messageService,
                asyncDatabaseExecutor
        );
        ClanProfileDataService profileDataService = new ClanProfileDataService(
                clanRepository,
                membershipRepository,
                rolePermissionService,
                clanBannerService
        );
        ClanMembershipNotificationRepository notificationRepository = new SqlClanMembershipNotificationRepository(
                dataSourceProvider,
                asyncDatabaseExecutor
        );
        ClanMembershipHistoryService membershipHistoryService = new ClanMembershipHistoryService(historyRepository);
        ClanRequestHistoryLoreBuilder requestHistoryLoreBuilder = new ClanRequestHistoryLoreBuilder(messageService);
        ClanMemberHistoryLoreBuilder memberHistoryLoreBuilder = new ClanMemberHistoryLoreBuilder(messageService);
        ClanMemberDetailLoreBuilder memberDetailLoreBuilder = new ClanMemberDetailLoreBuilder(
                messageService,
                memberHistoryLoreBuilder
        );
        ClanMembersSlotLayout membersSlotLayout = new ClanMembersSlotLayout(roleThemeService);
        ClanMembersDataService membersDataService = new ClanMembersDataService(clanRepository, clanBannerService);
        ClanMemberManagementPlanner memberManagementPlanner = new ClanMemberManagementPlanner(roleThemeService);
        ClanMemberDetailDataService memberDetailDataService = new ClanMemberDetailDataService(
                clanRepository,
                historyRepository,
                roleThemeService,
                memberManagementPlanner,
                rolePermissionService
        );
        ClanRequestsDataService requestsDataService = new ClanRequestsDataService(
                clanRepository,
                membershipRepository,
                rolePermissionService
        );
        ClanRequestDetailDataService requestDetailDataService = new ClanRequestDetailDataService(
                clanRepository,
                membershipRepository,
                historyRepository,
                rolePermissionService
        );
        ClanListDataService listDataService = new ClanListDataService(clanRepository, pluginConfig.guiList());
        ClanListMenuPopulator listMenuPopulator = new ClanListMenuPopulator(
                pluginConfig.guiList(),
                guiItemBuilder,
                messageService
        );
        ClanInfoMenuPopulator infoMenuPopulator = new ClanInfoMenuPopulator(
                pluginConfig.guiInfo(),
                guiItemBuilder,
                messageService
        );
        ClanInfoViewDataService infoViewDataService = new ClanInfoViewDataService(clanRepository);
        ClanTargetResolver targetResolver = new ClanTargetResolver(clanRepository);
        ClanPendingChatPresenter pendingChatPresenter = new ClanPendingChatPresenter(messageService);
        ClanExtensionMembershipNotifier extensionMembershipNotifier = new ClanExtensionMembershipNotifier(extensionRegistry);
        ClanMembershipService membershipService = new ClanMembershipService(
                clanRepository,
                membershipRepository,
                notificationRepository,
                targetResolver,
                pendingChatPresenter,
                messageService,
                asyncDatabaseExecutor,
                rolePermissionService,
                extensionMembershipNotifier
        );
        ClanExtensionsDataService extensionsDataService = new ClanExtensionsDataService(
                extensionRegistry,
                pluginConfig.guiExtensions()
        );
        ClanExtensionsMenuPopulator extensionsMenuPopulator = new ClanExtensionsMenuPopulator(
                pluginConfig.guiExtensions(),
                guiItemBuilder,
                extensionDisplayService
        );
        ClanHubDataService hubDataService = new ClanHubDataService(
                clanRepository,
                clanBannerService,
                clanEconomyMessages,
                messageService,
                extensionRegistry
        );
        ClanRequestsMenuPopulator requestsMenuPopulator = new ClanRequestsMenuPopulator(
                pluginConfig.guiRequests(),
                guiItemBuilder
        );
        ClanRequestDetailMenuPopulator requestDetailMenuPopulator = new ClanRequestDetailMenuPopulator(
                pluginConfig.guiRequestDetail(),
                guiItemBuilder,
                requestHistoryLoreBuilder
        );
        ClanMembersMenuPopulator membersMenuPopulator = new ClanMembersMenuPopulator(
                pluginConfig.guiMembers(),
                guiItemBuilder,
                membersSlotLayout,
                roleThemeService
        );
        ClanMemberDetailMenuPopulator memberDetailMenuPopulator = new ClanMemberDetailMenuPopulator(
                pluginConfig.guiMemberDetail(),
                guiItemBuilder,
                memberDetailLoreBuilder,
                roleThemeService,
                clanEconomyMessages
        );
        ClanMemberKickConfirmMenuPopulator memberKickConfirmMenuPopulator = new ClanMemberKickConfirmMenuPopulator(
                pluginConfig.guiMemberKickConfirm(),
                guiItemBuilder
        );
        ClanSettingsMenuPopulator settingsMenuPopulator = new ClanSettingsMenuPopulator(
                pluginConfig.guiClanSettings(),
                guiItemBuilder,
                roleThemeService
        );
        ClanRoleSettingsMenuPopulator roleSettingsMenuPopulator = new ClanRoleSettingsMenuPopulator(
                pluginConfig.guiClanRoleSettings(),
                guiItemBuilder
        );
        ClanBannerMenuPopulator bannerMenuPopulator = new ClanBannerMenuPopulator(
                pluginConfig.guiClanBanner(),
                guiItemBuilder,
                messageService
        );
        ClanSettingsDataService settingsDataService = new ClanSettingsDataService(clanRepository, roleThemeService);
        ClanRoleSettingsDataService roleSettingsDataService = new ClanRoleSettingsDataService(
                clanRepository,
                roleThemeService,
                rolePermissionService
        );
        ClanDisbandService disbandService = new ClanDisbandService(
                clanRepository,
                membershipHistoryService,
                standardPresence,
                messageService,
                asyncDatabaseExecutor
        );
        ClanStandardService clanStandardService = new ClanStandardService(
                clanRepository,
                clanBannerService,
                clanBannerRepository,
                clanStandardItem,
                standardPresence,
                disbandService,
                messageService,
                asyncDatabaseExecutor
        );
        this.clanStandardService = clanStandardService;
        ClanBannerDataService bannerDataService = new ClanBannerDataService(
                clanRepository,
                clanBannerService,
                clanStandardService
        );
        ClanGuiOpenService guiOpenService = new ClanGuiOpenService(
                pluginConfig.guiHub(),
                pluginConfig.guiProfile(),
                pluginConfig.guiRequests(),
                pluginConfig.guiRequestDetail(),
                pluginConfig.guiMembers(),
                pluginConfig.guiMemberDetail(),
                pluginConfig.guiMemberKickConfirm(),
                pluginConfig.guiClanSettings(),
                pluginConfig.guiClanRoleSettings(),
                pluginConfig.guiClanBanner(),
                pluginConfig.guiList(),
                pluginConfig.guiExtensions(),
                pluginConfig.guiInfo(),
                hubMenuPopulator,
                profileMenuPopulator,
                requestsMenuPopulator,
                requestDetailMenuPopulator,
                membersMenuPopulator,
                memberDetailMenuPopulator,
                memberKickConfirmMenuPopulator,
                settingsMenuPopulator,
                roleSettingsMenuPopulator,
                bannerMenuPopulator,
                listMenuPopulator,
                infoMenuPopulator,
                extensionsMenuPopulator,
                messageService,
                hubDataService,
                hubModuleSlotService,
                profileDataService,
                requestsDataService,
                requestDetailDataService,
                membersDataService,
                memberDetailDataService,
                infoViewDataService,
                listDataService,
                extensionsDataService,
                settingsDataService,
                roleSettingsDataService,
                bannerDataService,
                asyncDatabaseExecutor
        );
        ClanCreateChatPrompt createChatPrompt = new ClanCreateChatPrompt(messageService, asyncDatabaseExecutor);
        ClanCreateValidator createValidator = new ClanCreateValidator();
        ClanHelpChatPresenter helpChatPresenter = new ClanHelpChatPresenter(messageService);
        ClanListChatPresenter listChatPresenter = new ClanListChatPresenter(
                clanRepository,
                pluginConfig.clan(),
                messageService,
                asyncDatabaseExecutor
        );
        ClanInfoChatPresenter infoChatPresenter = new ClanInfoChatPresenter(messageService);
        ClanInfoService infoService = new ClanInfoService(
                clanRepository,
                pluginConfig.clan(),
                createValidator,
                infoChatPresenter,
                messageService,
                asyncDatabaseExecutor
        );
        ClanLeaveService leaveService = new ClanLeaveService(
                clanRepository,
                membershipHistoryService,
                messageService,
                asyncDatabaseExecutor,
                extensionMembershipNotifier
        );
        ClanKickService kickService = new ClanKickService(
                clanRepository,
                membershipHistoryService,
                rolePermissionService,
                roleThemeService,
                messageService,
                asyncDatabaseExecutor,
                extensionMembershipNotifier
        );
        ClanCreateEconomy createEconomy = new ClanCreateEconomy(
                pluginConfig.economy(),
                integrationBootstrap.vaultIntegration()
        );
        ClanMemberManagementService memberManagementService = new ClanMemberManagementService(
                clanRepository,
                roleThemeService,
                createEconomy,
                messageService,
                asyncDatabaseExecutor,
                extensionMembershipNotifier
        );
        ClanHubClickHandler hubClickHandler = new ClanHubClickHandler(
                messageService,
                createChatPrompt,
                helpChatPresenter,
                guiOpenService
        );
        ClanProfileClickHandler profileClickHandler = new ClanProfileClickHandler(
                createChatPrompt,
                guiOpenService,
                leaveService,
                messageService
        );
        ClanListClickHandler listClickHandler = new ClanListClickHandler(guiOpenService);
        ClanInfoClickHandler infoClickHandler = new ClanInfoClickHandler(
                guiOpenService,
                membershipService,
                leaveService
        );
        ClanExtensionsClickHandler extensionsClickHandler = new ClanExtensionsClickHandler(guiOpenService);
        ClanRequestsClickHandler requestsClickHandler = new ClanRequestsClickHandler(
                guiOpenService,
                membershipService
        );
        ClanRequestDetailClickHandler requestDetailClickHandler = new ClanRequestDetailClickHandler(
                guiOpenService,
                membershipService
        );
        ClanMembersClickHandler membersClickHandler = new ClanMembersClickHandler(guiOpenService, messageService);
        ClanMemberDetailClickHandler memberDetailClickHandler = new ClanMemberDetailClickHandler(
                guiOpenService,
                memberManagementService
        );
        ClanMemberKickConfirmClickHandler memberKickConfirmClickHandler = new ClanMemberKickConfirmClickHandler(
                guiOpenService,
                kickService
        );
        ClanSettingsClickHandler settingsClickHandler = new ClanSettingsClickHandler(guiOpenService);
        ClanRoleSettingsClickHandler roleSettingsClickHandler = new ClanRoleSettingsClickHandler(
                guiOpenService,
                rolePermissionService
        );
        ClanBannerClickHandler bannerClickHandler = new ClanBannerClickHandler(
                messageService,
                clanBannerService,
                clanStandardService,
                bannerMenuPopulator,
                guiOpenService,
                asyncDatabaseExecutor
        );
        ClanGuiClickDispatcher guiClickDispatcher = new ClanGuiClickDispatcher(
                hubClickHandler,
                profileClickHandler,
                listClickHandler,
                infoClickHandler,
                extensionsClickHandler,
                requestsClickHandler,
                requestDetailClickHandler,
                membersClickHandler,
                memberDetailClickHandler,
                memberKickConfirmClickHandler,
                settingsClickHandler,
                roleSettingsClickHandler,
                bannerClickHandler
        );
        ClanCreateService createService = new ClanCreateService(
                clanRepository,
                pluginConfig.clan(),
                pluginConfig.economy(),
                createValidator,
                createEconomy,
                clanEconomyMessages,
                messageService,
                asyncDatabaseExecutor,
                guiOpenService,
                rolePermissionService
        );
        clanRuntimeHolder.install(new ClanRuntimeServices(
                guiOpenService,
                hubClickHandler,
                profileClickHandler,
                listClickHandler,
                infoClickHandler,
                extensionsClickHandler,
                guiClickDispatcher,
                createService,
                createChatPrompt,
                helpChatPresenter,
                listChatPresenter,
                infoService,
                leaveService,
                disbandService,
                membershipService,
                roleThemeService
        ));
        SoulPactClanGui clanGui = new SoulPactClanGuiImpl(guiOpenService);
        soulPactApi = new SoulPactApiImpl(
                plugin,
                messageService,
                extensionRegistry,
                new SoulPactSchedulerImpl(asyncDatabaseExecutor),
                new SoulPactClanAccessImpl(clanRepository, rolePermissionService),
                clanGui,
                new SoulPactClanStandardImpl(clanStandardService),
                dataSourceProvider,
                clanQueryService
        );
        plugin.getServer().getServicesManager().register(
                SoulPactApi.class,
                soulPactApi,
                plugin,
                ServicePriority.Normal
        );
        plugin.getServer().getServicesManager().register(
                SoulPactClanGui.class,
                clanGui,
                plugin,
                ServicePriority.Normal
        );
    }
}
