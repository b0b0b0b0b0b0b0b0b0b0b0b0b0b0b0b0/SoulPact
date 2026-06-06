package bm.b0b0b0.SoulPact.clan.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class ClanGuiClickDispatcher {

    private final ClanHubClickHandler hubClickHandler;
    private final ClanProfileClickHandler profileClickHandler;
    private final ClanListClickHandler listClickHandler;
    private final ClanInfoClickHandler infoClickHandler;
    private final ClanExtensionsClickHandler extensionsClickHandler;
    private final ClanRequestsClickHandler requestsClickHandler;
    private final ClanRequestDetailClickHandler requestDetailClickHandler;
    private final ClanMembersClickHandler membersClickHandler;
    private final ClanMemberDetailClickHandler memberDetailClickHandler;
    private final ClanMemberKickConfirmClickHandler memberKickConfirmClickHandler;
    private final ClanSettingsClickHandler settingsClickHandler;
    private final ClanRoleSettingsClickHandler roleSettingsClickHandler;
    private final ClanBannerClickHandler bannerClickHandler;

    public ClanGuiClickDispatcher(
            ClanHubClickHandler hubClickHandler,
            ClanProfileClickHandler profileClickHandler,
            ClanListClickHandler listClickHandler,
            ClanInfoClickHandler infoClickHandler,
            ClanExtensionsClickHandler extensionsClickHandler,
            ClanRequestsClickHandler requestsClickHandler,
            ClanRequestDetailClickHandler requestDetailClickHandler,
            ClanMembersClickHandler membersClickHandler,
            ClanMemberDetailClickHandler memberDetailClickHandler,
            ClanMemberKickConfirmClickHandler memberKickConfirmClickHandler,
            ClanSettingsClickHandler settingsClickHandler,
            ClanRoleSettingsClickHandler roleSettingsClickHandler,
            ClanBannerClickHandler bannerClickHandler
    ) {
        this.hubClickHandler = hubClickHandler;
        this.profileClickHandler = profileClickHandler;
        this.listClickHandler = listClickHandler;
        this.infoClickHandler = infoClickHandler;
        this.extensionsClickHandler = extensionsClickHandler;
        this.requestsClickHandler = requestsClickHandler;
        this.requestDetailClickHandler = requestDetailClickHandler;
        this.membersClickHandler = membersClickHandler;
        this.memberDetailClickHandler = memberDetailClickHandler;
        this.memberKickConfirmClickHandler = memberKickConfirmClickHandler;
        this.settingsClickHandler = settingsClickHandler;
        this.roleSettingsClickHandler = roleSettingsClickHandler;
        this.bannerClickHandler = bannerClickHandler;
    }

    public void handle(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanHubMenu hubMenu) {
            event.setCancelled(true);
            hubClickHandler.handle(hubMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanProfileMenu profileMenu) {
            event.setCancelled(true);
            profileClickHandler.handle(profileMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanRequestsMenu requestsMenu) {
            event.setCancelled(true);
            requestsClickHandler.handle(requestsMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanRequestDetailMenu requestDetailMenu) {
            event.setCancelled(true);
            requestDetailClickHandler.handle(requestDetailMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanMembersMenu membersMenu) {
            event.setCancelled(true);
            membersClickHandler.handle(membersMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanMemberDetailMenu memberDetailMenu) {
            event.setCancelled(true);
            memberDetailClickHandler.handle(memberDetailMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanMemberKickConfirmMenu kickConfirmMenu) {
            event.setCancelled(true);
            memberKickConfirmClickHandler.handle(kickConfirmMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanSettingsMenu settingsMenu) {
            event.setCancelled(true);
            settingsClickHandler.handle(settingsMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanRoleSettingsMenu roleSettingsMenu) {
            event.setCancelled(true);
            roleSettingsClickHandler.handle(roleSettingsMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanBannerMenu bannerMenu) {
            event.setCancelled(true);
            bannerClickHandler.handle(bannerMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanListMenu listMenu) {
            event.setCancelled(true);
            listClickHandler.handle(listMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanInfoMenu infoMenu) {
            event.setCancelled(true);
            infoClickHandler.handle(infoMenu, player, event.getSlot());
            return;
        }
        if (clickedInventory.getHolder(false) instanceof ClanExtensionsMenu extensionsMenu) {
            event.setCancelled(true);
            extensionsClickHandler.handle(extensionsMenu, player, event.getSlot());
        }
    }
}
