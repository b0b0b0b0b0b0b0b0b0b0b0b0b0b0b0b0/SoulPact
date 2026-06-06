package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanProfileSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiProfileConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanProfileMenu implements InventoryHolder {

    private final GuiProfileConfig guiProfileConfig;
    private final Inventory inventory;
    private final boolean empty;
    private final ClanProfileSnapshot snapshot;
    private final java.util.UUID viewerId;
    private final boolean requestsView;
    private final boolean memberCanLeave;

    public ClanProfileMenu(
            GuiProfileConfig guiProfileConfig,
            ClanProfileMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanProfileSnapshot snapshot
    ) {
        this.guiProfileConfig = guiProfileConfig;
        this.empty = false;
        this.snapshot = snapshot;
        this.viewerId = player.getUniqueId();
        this.requestsView = snapshot.requestsView();
        this.memberCanLeave = !snapshot.clan().leaderId().equals(viewerId) && !requestsView;
        this.inventory = Bukkit.createInventory(
                this,
                guiProfileConfig.size(),
                messageService.component(player, "clan.gui.profile.title")
        );
        populator.populate(inventory, player, snapshot);
    }

    public ClanProfileMenu(
            GuiProfileConfig guiProfileConfig,
            ClanProfileMenuPopulator populator,
            MessageService messageService,
            Player player
    ) {
        this.guiProfileConfig = guiProfileConfig;
        this.empty = true;
        this.snapshot = null;
        this.viewerId = player.getUniqueId();
        this.requestsView = false;
        this.memberCanLeave = false;
        this.inventory = Bukkit.createInventory(
                this,
                guiProfileConfig.size(),
                messageService.component(player, "clan.gui.profile.empty.title")
        );
        populator.populateEmpty(inventory, player);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiProfileConfig config() {
        return guiProfileConfig;
    }

    public int slotBack() {
        return guiProfileConfig.backSlot();
    }

    public boolean empty() {
        return empty;
    }

    public int slotEmptyCreate() {
        return guiProfileConfig.emptyCreateSlot();
    }

    public int slotEmptyList() {
        return guiProfileConfig.emptyListSlot();
    }

    public int slotRequests() {
        return guiProfileConfig.requestsSlot();
    }

    public int slotMembers() {
        return guiProfileConfig.membersSlot();
    }

    public long clanId() {
        return snapshot.clan().id();
    }

    public boolean leaderView() {
        return requestsView;
    }

    public boolean memberCanLeave() {
        return memberCanLeave;
    }

    public int slotLeave() {
        return guiProfileConfig.leaveSlot();
    }

    public int slotBanner() {
        return guiProfileConfig.bannerSlot();
    }

    public boolean viewerIsLeader() {
        return !empty && snapshot.viewerIsLeader();
    }
}
