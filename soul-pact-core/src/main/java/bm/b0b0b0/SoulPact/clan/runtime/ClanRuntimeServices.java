package bm.b0b0b0.SoulPact.clan.runtime;

import bm.b0b0b0.SoulPact.clan.gui.ClanCreateChatPrompt;
import bm.b0b0b0.SoulPact.clan.gui.ClanGuiClickDispatcher;
import bm.b0b0b0.SoulPact.clan.gui.ClanGuiOpenService;
import bm.b0b0b0.SoulPact.clan.gui.ClanHubClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanExtensionsClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanInfoClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanListClickHandler;
import bm.b0b0b0.SoulPact.clan.gui.ClanProfileClickHandler;
import bm.b0b0b0.SoulPact.clan.message.ClanHelpChatPresenter;
import bm.b0b0b0.SoulPact.clan.message.ClanListChatPresenter;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.clan.service.ClanCreateService;
import bm.b0b0b0.SoulPact.clan.service.ClanDisbandService;
import bm.b0b0b0.SoulPact.clan.service.ClanInfoService;
import bm.b0b0b0.SoulPact.clan.service.ClanLeaveService;
import bm.b0b0b0.SoulPact.clan.service.ClanMembershipService;

public final class ClanRuntimeServices {

    private final ClanGuiOpenService guiOpenService;
    private final ClanHubClickHandler hubClickHandler;
    private final ClanProfileClickHandler profileClickHandler;
    private final ClanListClickHandler listClickHandler;
    private final ClanInfoClickHandler infoClickHandler;
    private final ClanExtensionsClickHandler extensionsClickHandler;
    private final ClanGuiClickDispatcher guiClickDispatcher;
    private final ClanCreateService createService;
    private final ClanCreateChatPrompt createChatPrompt;
    private final ClanHelpChatPresenter helpChatPresenter;
    private final ClanListChatPresenter listChatPresenter;
    private final ClanInfoService infoService;
    private final ClanLeaveService leaveService;
    private final ClanDisbandService disbandService;
    private final ClanMembershipService membershipService;
    private final RoleThemeService roleThemeService;

    public ClanRuntimeServices(
            ClanGuiOpenService guiOpenService,
            ClanHubClickHandler hubClickHandler,
            ClanProfileClickHandler profileClickHandler,
            ClanListClickHandler listClickHandler,
            ClanInfoClickHandler infoClickHandler,
            ClanExtensionsClickHandler extensionsClickHandler,
            ClanGuiClickDispatcher guiClickDispatcher,
            ClanCreateService createService,
            ClanCreateChatPrompt createChatPrompt,
            ClanHelpChatPresenter helpChatPresenter,
            ClanListChatPresenter listChatPresenter,
            ClanInfoService infoService,
            ClanLeaveService leaveService,
            ClanDisbandService disbandService,
            ClanMembershipService membershipService,
            RoleThemeService roleThemeService
    ) {
        this.guiOpenService = guiOpenService;
        this.hubClickHandler = hubClickHandler;
        this.profileClickHandler = profileClickHandler;
        this.listClickHandler = listClickHandler;
        this.infoClickHandler = infoClickHandler;
        this.extensionsClickHandler = extensionsClickHandler;
        this.guiClickDispatcher = guiClickDispatcher;
        this.createService = createService;
        this.createChatPrompt = createChatPrompt;
        this.helpChatPresenter = helpChatPresenter;
        this.listChatPresenter = listChatPresenter;
        this.infoService = infoService;
        this.leaveService = leaveService;
        this.disbandService = disbandService;
        this.membershipService = membershipService;
        this.roleThemeService = roleThemeService;
    }

    public ClanGuiOpenService guiOpenService() {
        return guiOpenService;
    }

    public ClanHubClickHandler hubClickHandler() {
        return hubClickHandler;
    }

    public ClanProfileClickHandler profileClickHandler() {
        return profileClickHandler;
    }

    public ClanListClickHandler listClickHandler() {
        return listClickHandler;
    }

    public ClanInfoClickHandler infoClickHandler() {
        return infoClickHandler;
    }

    public ClanExtensionsClickHandler extensionsClickHandler() {
        return extensionsClickHandler;
    }

    public ClanGuiClickDispatcher guiClickDispatcher() {
        return guiClickDispatcher;
    }

    public ClanCreateService createService() {
        return createService;
    }

    public ClanCreateChatPrompt createChatPrompt() {
        return createChatPrompt;
    }

    public ClanHelpChatPresenter helpChatPresenter() {
        return helpChatPresenter;
    }

    public ClanListChatPresenter listChatPresenter() {
        return listChatPresenter;
    }

    public ClanInfoService infoService() {
        return infoService;
    }

    public ClanLeaveService leaveService() {
        return leaveService;
    }

    public ClanDisbandService disbandService() {
        return disbandService;
    }

    public ClanMembershipService membershipService() {
        return membershipService;
    }

    public RoleThemeService roleThemeService() {
        return roleThemeService;
    }
}
