package bm.b0b0b0.SoulPact.war.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.war.ClanWarProvider;
import bm.b0b0b0.SoulPact.api.war.ClanWarUiBridge;
import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import bm.b0b0b0.SoulPact.war.gui.WarGuiService;
import org.bukkit.entity.Player;

public final class WarExtension implements ClanWarProvider {

    private final WarGuiService guiService;
    private final ClanWarUiBridge uiBridge;
    private final WarFlagBreakGate flagBreakGate;

    public WarExtension(WarGuiService guiService, ClanWarUiBridge uiBridge, WarFlagBreakGate flagBreakGate) {
        this.guiService = guiService;
        this.uiBridge = uiBridge;
        this.flagBreakGate = flagBreakGate;
    }

    @Override
    public String id() {
        return "war";
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
        guiService.openPendingList(player);
    }

    @Override
    public ClanWarUiBridge ui() {
        return uiBridge;
    }

    @Override
    public WarFlagBreakGate flagBreak() {
        return flagBreakGate;
    }
}
