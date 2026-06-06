package bm.b0b0b0.SoulPact.clan.listener;

import bm.b0b0b0.SoulPact.clan.gui.ClanGuiClickDispatcher;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class ClanGuiListener implements Listener {

    private final ClanGuiClickDispatcher clickDispatcher;

    public ClanGuiListener(ClanGuiClickDispatcher clickDispatcher) {
        this.clickDispatcher = clickDispatcher;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        clickDispatcher.handle(event);
    }
}
