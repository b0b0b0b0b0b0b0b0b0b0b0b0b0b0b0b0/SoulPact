package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.service.ClanMembersPage;
import bm.b0b0b0.SoulPact.clan.service.ClanMembersSnapshot;
import bm.b0b0b0.SoulPact.core.config.GuiMembersConfig;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class ClanMembersMenu implements InventoryHolder {

    private final GuiMembersConfig guiMembersConfig;
    private final ClanMembersSnapshot snapshot;
    private final ClanMembersNav navigation;
    private final ClanMembersPage membersPage;
    private final Inventory inventory;

    public ClanMembersMenu(
            GuiMembersConfig guiMembersConfig,
            ClanMembersMenuPopulator populator,
            MessageService messageService,
            Player player,
            ClanMembersSnapshot snapshot,
            ClanMembersNav navigation
    ) {
        this.guiMembersConfig = guiMembersConfig;
        this.snapshot = snapshot;
        this.navigation = navigation;
        this.membersPage = populator.resolvePage(snapshot, navigation.membersPage());
        this.inventory = Bukkit.createInventory(
                this,
                guiMembersConfig.size(),
                messageService.component(player, "clan.gui.members.title", Map.of(
                        "tag", snapshot.clan().tag(),
                        "name", snapshot.clan().name(),
                        "count", String.valueOf(snapshot.members().size()),
                        "page", String.valueOf(membersPage.page() + 1),
                        "pages", String.valueOf(Math.max(membersPage.totalPages(), 1))
                ))
        );
        populator.populate(inventory, player, membersPage);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public GuiMembersConfig config() {
        return guiMembersConfig;
    }

    public ClanMembersSnapshot snapshot() {
        return snapshot;
    }

    public ClanMembersNav navigation() {
        return navigation;
    }

    public ClanMembersPage membersPage() {
        return membersPage;
    }

    public int slotPrevious() {
        return guiMembersConfig.previousSlot();
    }

    public int slotNext() {
        return guiMembersConfig.nextSlot();
    }

    public UUID memberIdAtSlot(int slot) {
        return membersPage.memberIdAtSlot(slot);
    }
}
