package bm.b0b0b0.SoulPact.gladiator.listener;

import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.service.WandSelectionService;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public final class GladiatorWandListener implements Listener {

    private final GladiatorConfig config;
    private final GladiatorMessages messages;
    private final WandSelectionService selectionService;

    public GladiatorWandListener(
            GladiatorConfig config,
            GladiatorMessages messages,
            WandSelectionService selectionService
    ) {
        this.config = config;
        this.messages = messages;
        this.selectionService = selectionService;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() != config.wandMaterial()) {
            return;
        }
        if (!player.hasPermission(config.adminPermission())) {
            return;
        }
        Location location = event.getClickedBlock().getLocation();
        Map<String, String> placeholders = Map.of(
                "x", String.valueOf(location.getBlockX()),
                "y", String.valueOf(location.getBlockY()),
                "z", String.valueOf(location.getBlockZ())
        );
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setCancelled(true);
            selectionService.setFirst(player.getUniqueId(), location);
            messages.send(player, "gladiator.wand.first", placeholders);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            selectionService.setSecond(player.getUniqueId(), location);
            messages.send(player, "gladiator.wand.second", placeholders);
        }
    }
}
