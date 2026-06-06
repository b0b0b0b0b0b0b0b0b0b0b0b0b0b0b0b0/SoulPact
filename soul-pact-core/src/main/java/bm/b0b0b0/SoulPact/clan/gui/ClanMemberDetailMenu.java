package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.model.ClanMemberManagementAction;
import bm.b0b0b0.SoulPact.clan.service.ClanMemberDetailSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiMemberDetailConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Collections;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanMemberDetailMenu implements InventoryHolder {

    private final GuiMemberDetailConfig guiMemberDetailConfig;
    private final ClanMemberDetailSnapshot snapshot;
    private final ClanMembersNav navigation;
    private final Map<Integer, ClanMemberManagementAction> actionSlots;
    private final Inventory inventory;

    public ClanMemberDetailMenu(
            GuiMemberDetailConfig guiMemberDetailConfig,
            ClanMemberDetailMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanMemberDetailSnapshot snapshot,
            ClanMembersNav navigation
    ) {
        this.guiMemberDetailConfig = guiMemberDetailConfig;
        this.snapshot = snapshot;
        this.navigation = navigation;
        this.inventory = Bukkit.createInventory(
                this,
                guiMemberDetailConfig.size(),
                messageService.component(player, "clan.gui.members.detail.title", Map.of(
                        "player", snapshot.playerName()
                ))
        );
        this.actionSlots = Collections.unmodifiableMap(populator.populate(inventory, player, snapshot));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiMemberDetailConfig config() {
        return guiMemberDetailConfig;
    }

    public ClanMemberDetailSnapshot snapshot() {
        return snapshot;
    }

    public ClanMembersNav navigation() {
        return navigation;
    }

    public ClanMemberManagementAction actionAtSlot(int slot) {
        return actionSlots.get(slot);
    }
}
