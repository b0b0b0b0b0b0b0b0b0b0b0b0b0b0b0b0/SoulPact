package bm.b0b0b0.SoulPact.gladiator.gui;

import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import java.util.function.Supplier;
import org.bukkit.entity.Player;

public final class GladiatorGuiService {

    private final Supplier<GladiatorConfig> configSupplier;
    private final GladiatorMenuPopulator populator;
    private final GladiatorMessages messages;

    public GladiatorGuiService(
            Supplier<GladiatorConfig> configSupplier,
            GladiatorMenuPopulator populator,
            GladiatorMessages messages
    ) {
        this.configSupplier = configSupplier;
        this.populator = populator;
        this.messages = messages;
    }

    public void open(Player player) {
        GladiatorMenu menu = new GladiatorMenu(configSupplier.get(), populator, messages, player);
        player.openInventory(menu.getInventory());
    }
}
