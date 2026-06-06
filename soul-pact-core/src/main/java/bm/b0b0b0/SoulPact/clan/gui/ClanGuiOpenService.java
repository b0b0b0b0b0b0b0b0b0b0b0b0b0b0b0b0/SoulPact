package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanInfoViewDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanExtensionsDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanHubDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanListDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanProfileDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberDetailDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestDetailDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanRequestsDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanRoleSettingsDataService;
import bm.b0b0b0.SoulPact.clan.service.ClanSettingsDataService;
import bm.b0b0b0.SoulPact.core.config.GuiClanRoleSettingsConfig;
import bm.b0b0b0.SoulPact.core.config.GuiClanSettingsConfig;
import bm.b0b0b0.SoulPact.core.config.GuiExtensionsConfig;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
import bm.b0b0b0.SoulPact.core.config.GuiInfoConfig;
import bm.b0b0b0.SoulPact.core.config.GuiListConfig;
import bm.b0b0b0.SoulPact.core.config.GuiMemberDetailConfig;
import bm.b0b0b0.SoulPact.core.config.GuiMemberKickConfirmConfig;
import bm.b0b0b0.SoulPact.core.config.GuiMembersConfig;
import bm.b0b0b0.SoulPact.core.config.GuiProfileConfig;
import bm.b0b0b0.SoulPact.core.config.GuiRequestDetailConfig;
import bm.b0b0b0.SoulPact.core.config.GuiRequestsConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.UUID;
import org.bukkit.entity.Player;

public final class ClanGuiOpenService {

    private final GuiHubConfig guiHubConfig;
    private final GuiProfileConfig guiProfileConfig;
    private final GuiRequestsConfig guiRequestsConfig;
    private final GuiRequestDetailConfig guiRequestDetailConfig;
    private final GuiMembersConfig guiMembersConfig;
    private final GuiMemberDetailConfig guiMemberDetailConfig;
    private final GuiMemberKickConfirmConfig guiMemberKickConfirmConfig;
    private final GuiClanSettingsConfig guiClanSettingsConfig;
    private final GuiClanRoleSettingsConfig guiClanRoleSettingsConfig;
    private final GuiListConfig guiListConfig;
    private final GuiExtensionsConfig guiExtensionsConfig;
    private final GuiInfoConfig guiInfoConfig;
    private final ClanHubMenuPopulator hubMenuPopulator;
    private final ClanProfileMenuPopulator profileMenuPopulator;
    private final ClanRequestsMenuPopulator requestsMenuPopulator;
    private final ClanRequestDetailMenuPopulator requestDetailMenuPopulator;
    private final ClanMembersMenuPopulator membersMenuPopulator;
    private final ClanMemberDetailMenuPopulator memberDetailMenuPopulator;
    private final ClanMemberKickConfirmMenuPopulator memberKickConfirmMenuPopulator;
    private final ClanSettingsMenuPopulator settingsMenuPopulator;
    private final ClanRoleSettingsMenuPopulator roleSettingsMenuPopulator;
    private final ClanListMenuPopulator listMenuPopulator;
    private final ClanInfoMenuPopulator infoMenuPopulator;
    private final ClanExtensionsMenuPopulator extensionsMenuPopulator;
    private final MessageService messageService;
    private final ClanHubDataService hubDataService;
    private final ClanProfileDataService profileDataService;
    private final ClanRequestsDataService requestsDataService;
    private final ClanRequestDetailDataService requestDetailDataService;
    private final ClanMembersDataService membersDataService;
    private final ClanMemberDetailDataService memberDetailDataService;
    private final ClanInfoViewDataService infoViewDataService;
    private final ClanListDataService listDataService;
    private final ClanExtensionsDataService extensionsDataService;
    private final ClanSettingsDataService settingsDataService;
    private final ClanRoleSettingsDataService roleSettingsDataService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanGuiOpenService(
            GuiHubConfig guiHubConfig,
            GuiProfileConfig guiProfileConfig,
            GuiRequestsConfig guiRequestsConfig,
            GuiRequestDetailConfig guiRequestDetailConfig,
            GuiMembersConfig guiMembersConfig,
            GuiMemberDetailConfig guiMemberDetailConfig,
            GuiMemberKickConfirmConfig guiMemberKickConfirmConfig,
            GuiClanSettingsConfig guiClanSettingsConfig,
            GuiClanRoleSettingsConfig guiClanRoleSettingsConfig,
            GuiListConfig guiListConfig,
            GuiExtensionsConfig guiExtensionsConfig,
            GuiInfoConfig guiInfoConfig,
            ClanHubMenuPopulator hubMenuPopulator,
            ClanProfileMenuPopulator profileMenuPopulator,
            ClanRequestsMenuPopulator requestsMenuPopulator,
            ClanRequestDetailMenuPopulator requestDetailMenuPopulator,
            ClanMembersMenuPopulator membersMenuPopulator,
            ClanMemberDetailMenuPopulator memberDetailMenuPopulator,
            ClanMemberKickConfirmMenuPopulator memberKickConfirmMenuPopulator,
            ClanSettingsMenuPopulator settingsMenuPopulator,
            ClanRoleSettingsMenuPopulator roleSettingsMenuPopulator,
            ClanListMenuPopulator listMenuPopulator,
            ClanInfoMenuPopulator infoMenuPopulator,
            ClanExtensionsMenuPopulator extensionsMenuPopulator,
            MessageService messageService,
            ClanHubDataService hubDataService,
            ClanProfileDataService profileDataService,
            ClanRequestsDataService requestsDataService,
            ClanRequestDetailDataService requestDetailDataService,
            ClanMembersDataService membersDataService,
            ClanMemberDetailDataService memberDetailDataService,
            ClanInfoViewDataService infoViewDataService,
            ClanListDataService listDataService,
            ClanExtensionsDataService extensionsDataService,
            ClanSettingsDataService settingsDataService,
            ClanRoleSettingsDataService roleSettingsDataService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.guiHubConfig = guiHubConfig;
        this.guiProfileConfig = guiProfileConfig;
        this.guiRequestsConfig = guiRequestsConfig;
        this.guiRequestDetailConfig = guiRequestDetailConfig;
        this.guiMembersConfig = guiMembersConfig;
        this.guiMemberDetailConfig = guiMemberDetailConfig;
        this.guiMemberKickConfirmConfig = guiMemberKickConfirmConfig;
        this.guiClanSettingsConfig = guiClanSettingsConfig;
        this.guiClanRoleSettingsConfig = guiClanRoleSettingsConfig;
        this.guiListConfig = guiListConfig;
        this.guiExtensionsConfig = guiExtensionsConfig;
        this.guiInfoConfig = guiInfoConfig;
        this.hubMenuPopulator = hubMenuPopulator;
        this.profileMenuPopulator = profileMenuPopulator;
        this.requestsMenuPopulator = requestsMenuPopulator;
        this.requestDetailMenuPopulator = requestDetailMenuPopulator;
        this.membersMenuPopulator = membersMenuPopulator;
        this.memberDetailMenuPopulator = memberDetailMenuPopulator;
        this.memberKickConfirmMenuPopulator = memberKickConfirmMenuPopulator;
        this.settingsMenuPopulator = settingsMenuPopulator;
        this.roleSettingsMenuPopulator = roleSettingsMenuPopulator;
        this.listMenuPopulator = listMenuPopulator;
        this.infoMenuPopulator = infoMenuPopulator;
        this.extensionsMenuPopulator = extensionsMenuPopulator;
        this.messageService = messageService;
        this.hubDataService = hubDataService;
        this.profileDataService = profileDataService;
        this.requestsDataService = requestsDataService;
        this.requestDetailDataService = requestDetailDataService;
        this.membersDataService = membersDataService;
        this.memberDetailDataService = memberDetailDataService;
        this.infoViewDataService = infoViewDataService;
        this.listDataService = listDataService;
        this.extensionsDataService = extensionsDataService;
        this.settingsDataService = settingsDataService;
        this.roleSettingsDataService = roleSettingsDataService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void openHub(Player player) {
        hubDataService.loadSnapshot(player).thenAccept(snapshot -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            ClanHubMenu menu = new ClanHubMenu(guiHubConfig, hubMenuPopulator, messageService, player, snapshot);
            player.openInventory(menu.getInventory());
        }));
    }

    public void openProfile(Player player) {
        profileDataService.load(player).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                ClanProfileMenu menu = new ClanProfileMenu(
                        guiProfileConfig,
                        profileMenuPopulator,
                        messageService,
                        player
                );
                player.openInventory(menu.getInventory());
                return;
            }
            ClanProfileMenu menu = new ClanProfileMenu(
                    guiProfileConfig,
                    profileMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get()
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openList(Player player, int page) {
        listDataService.loadPage(page).thenAccept(listPage -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            ClanListMenu menu = new ClanListMenu(
                    guiListConfig,
                    listMenuPopulator,
                    messageService,
                    player,
                    listPage
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openInfo(Player player, long clanId, int listPage) {
        infoViewDataService.load(player, clanId).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                messageService.send(player, "clan.info.not-found", java.util.Map.of("tag", String.valueOf(clanId)));
                return;
            }
            ClanInfoMenu menu = new ClanInfoMenu(
                    guiInfoConfig,
                    infoMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get(),
                    listPage
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openRequests(Player player) {
        requestsDataService.load(player).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                messageService.send(player, "clan.request.not-staff");
                return;
            }
            ClanRequestsMenu menu = new ClanRequestsMenu(
                    guiRequestsConfig,
                    requestsMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get()
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openRequestDetail(Player player, long requestId) {
        requestDetailDataService.load(player, requestId).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                messageService.send(player, "clan.request.not-found");
                openRequests(player);
                return;
            }
            ClanRequestDetailMenu menu = new ClanRequestDetailMenu(
                    guiRequestDetailConfig,
                    requestDetailMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get()
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openMembers(Player player, ClanMembersNav navigation) {
        membersDataService.load(navigation.clanId()).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                messageService.send(player, "clan.info.not-found", java.util.Map.of("tag", String.valueOf(navigation.clanId())));
                return;
            }
            ClanMembersMenu menu = new ClanMembersMenu(
                    guiMembersConfig,
                    membersMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get(),
                    navigation
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openMemberDetail(Player player, long clanId, UUID memberId, ClanMembersNav navigation) {
        memberDetailDataService.load(clanId, memberId, player.getUniqueId()).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                openMembers(player, navigation);
                return;
            }
            ClanMemberDetailMenu menu = new ClanMemberDetailMenu(
                    guiMemberDetailConfig,
                    memberDetailMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get(),
                    navigation
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openMemberKickConfirm(
            Player player,
            long clanId,
            UUID targetId,
            String targetName,
            ClanMembersNav navigation
    ) {
        if (!player.isOnline()) {
            return;
        }
        ClanMemberKickConfirmMenu menu = new ClanMemberKickConfirmMenu(
                guiMemberKickConfirmConfig,
                memberKickConfirmMenuPopulator,
                messageService,
                player,
                navigation,
                clanId,
                targetId,
                targetName
        );
        player.openInventory(menu.getInventory());
    }

    public void openSettings(Player player) {
        settingsDataService.load(player).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                messageService.send(player, "clan.settings.not-leader");
                return;
            }
            ClanSettingsMenu menu = new ClanSettingsMenu(
                    guiClanSettingsConfig,
                    settingsMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get()
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openRoleSettings(Player player, long clanId, String roleKey) {
        roleSettingsDataService.load(player, clanId, roleKey).thenAccept(snapshotOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (snapshotOptional.isEmpty()) {
                openSettings(player);
                return;
            }
            ClanRoleSettingsMenu menu = new ClanRoleSettingsMenu(
                    guiClanRoleSettingsConfig,
                    roleSettingsMenuPopulator,
                    messageService,
                    player,
                    snapshotOptional.get()
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openExtensions(Player player, int page) {
        if (!player.isOnline()) {
            return;
        }
        var extensionsPage = extensionsDataService.loadPage(page);
        ClanExtensionsMenu menu = new ClanExtensionsMenu(
                guiExtensionsConfig,
                extensionsMenuPopulator,
                messageService,
                player,
                extensionsPage
        );
        player.openInventory(menu.getInventory());
    }
}
