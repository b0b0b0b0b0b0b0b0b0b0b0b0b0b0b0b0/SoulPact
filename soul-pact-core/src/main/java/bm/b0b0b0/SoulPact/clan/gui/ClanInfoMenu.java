package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanInfoViewSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiInfoConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanInfoMenu implements InventoryHolder {

    private final GuiInfoConfig guiInfoConfig;
    private final long clanId;
    private final int listPage;
    private final ClanInfoViewSnapshot snapshot;
    private final Inventory inventory;

    public ClanInfoMenu(
            GuiInfoConfig guiInfoConfig,
            ClanInfoMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanInfoViewSnapshot snapshot,
            int listPage
    ) {
        this.guiInfoConfig = guiInfoConfig;
        this.clanId = snapshot.clan().id();
        this.listPage = listPage;
        this.snapshot = snapshot;
        this.inventory = Bukkit.createInventory(
                this,
                guiInfoConfig.size(),
                messageService.component(player, "clan.gui.info.title", java.util.Map.of(
                        "tag", snapshot.clan().tag(),
                        "name", snapshot.clan().name()
                ))
        );
        populator.populate(inventory, player, snapshot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public long clanId() {
        return clanId;
    }

    public int listPage() {
        return listPage;
    }

    public ClanInfoViewSnapshot snapshot() {
        return snapshot;
    }

    public int slotAction() {
        return guiInfoConfig.actionSlot();
    }

    public int slotDeclareWar() {
        return guiInfoConfig.declareWarSlot();
    }

    public int slotInviteCoalition() {
        return guiInfoConfig.inviteCoalitionSlot();
    }

    public int slotMembers() {
        return guiInfoConfig.membersSlot();
    }

    public int slotBack() {
        return guiInfoConfig.backSlot();
    }

    public int slotAllyFirst() {
        return guiInfoConfig.allyFirstSlot();
    }

    public int slotAllySecond() {
        return guiInfoConfig.allySecondSlot();
    }

    public int slotAllyThird() {
        return guiInfoConfig.allyThirdSlot();
    }

    public int allySlotIndex(int slot) {
        if (slot == slotAllyFirst()) {
            return 0;
        }
        if (slot == slotAllySecond()) {
            return 1;
        }
        if (slot == slotAllyThird()) {
            return 2;
        }
        return -1;
    }
}
