package bm.b0b0b0.SoulPact.coalition.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayBridge;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionProvider;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge;
import bm.b0b0b0.SoulPact.coalition.gui.CoalitionGuiService;
import org.bukkit.entity.Player;

public final class CoalitionExtension implements CoalitionProvider {

    private final CoalitionGuiService guiService;
    private final CoalitionDisplayBridge displayBridge;
    private final CoalitionWarBridge warBridge;

    public CoalitionExtension(
            CoalitionGuiService guiService,
            CoalitionDisplayBridge displayBridge,
            CoalitionWarBridge warBridge
    ) {
        this.guiService = guiService;
        this.displayBridge = displayBridge;
        this.warBridge = warBridge;
    }

    @Override
    public String id() {
        return "coalition";
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
    public CoalitionDisplayBridge display() {
        return displayBridge;
    }

    @Override
    public CoalitionWarBridge war() {
        return warBridge;
    }

    @Override
    public void openGui(Player player) {
        guiService.open(player);
    }
}
