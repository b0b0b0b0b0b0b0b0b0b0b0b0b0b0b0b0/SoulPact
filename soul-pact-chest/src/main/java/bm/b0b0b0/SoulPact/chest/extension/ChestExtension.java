package bm.b0b0b0.SoulPact.chest.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.api.chest.ClanChestSpoilsApi;
import bm.b0b0b0.SoulPact.api.chest.ClanChestSpoilsProvider;
import bm.b0b0b0.SoulPact.chest.gui.ChestGuiService;
import org.bukkit.entity.Player;

public final class ChestExtension implements SoulPactGuiExtension, ClanChestSpoilsProvider {

    private final ChestGuiService guiService;
    private final ClanChestSpoilsApi spoilsApi;

    public ChestExtension(ChestGuiService guiService, ClanChestSpoilsApi spoilsApi) {
        this.guiService = guiService;
        this.spoilsApi = spoilsApi;
    }

    @Override
    public String id() {
        return "chest";
    }

    @Override
    public void enable(SoulPactApi api) {
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
    }

    @Override
    public void openGui(Player player) {
        guiService.open(player);
    }

    @Override
    public ClanChestSpoilsApi spoils() {
        return spoilsApi;
    }
}
