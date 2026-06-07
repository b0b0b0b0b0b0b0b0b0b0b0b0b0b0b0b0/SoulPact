package bm.b0b0b0.SoulPact.land.gui;

import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.model.BaseExpansionAxis;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import org.bukkit.entity.Player;

public final class LandClickHandler {

    private final LandGuiService guiService;
    private final LandClanNavigation clanNavigation;
    private final ClanBaseService baseService;
    private final LandConfig config;

    public LandClickHandler(
            LandGuiService guiService,
            LandClanNavigation clanNavigation,
            ClanBaseService baseService,
            LandConfig config
    ) {
        this.guiService = guiService;
        this.clanNavigation = clanNavigation;
        this.baseService = baseService;
        this.config = config;
    }

    public void handle(LandMenu menu, Player player, int slot) {
        if (slot == config.backSlot()) {
            clanNavigation.openHub(player);
            return;
        }
        if (slot == config.infoSlot() && menu.snapshot().leader()) {
            clanNavigation.openBanner(player);
            return;
        }
        if (!menu.snapshot().leader()) {
            return;
        }
        if (menu.snapshot().base().isEmpty()) {
            return;
        }
        var base = menu.snapshot().base().get();
        Runnable refresh = () -> guiService.refresh(player, menu.snapshot().clan());
        BaseExpansionAxis axis = resolveExpansionAxis(slot);
        if (axis != null) {
            baseService.expandBase(
                    player,
                    menu.snapshot().clan(),
                    base,
                    axis,
                    refresh
            );
            return;
        }
        if (slot == config.pvpSlot()) {
            baseService.togglePvp(player, menu.snapshot().clan(), base, refresh);
            return;
        }
        if (slot == config.mobSpawnSlot()) {
            baseService.toggleMobSpawn(player, menu.snapshot().clan(), base, refresh);
            return;
        }
        if (slot == config.borderColorSlot()) {
            baseService.cycleBorderColor(player, menu.snapshot().clan(), base, refresh);
        }
    }

    private BaseExpansionAxis resolveExpansionAxis(int slot) {
        if (slot == config.expandNorthSlot()) {
            return BaseExpansionAxis.NORTH;
        }
        if (slot == config.expandWestSlot()) {
            return BaseExpansionAxis.WEST;
        }
        if (slot == config.expandEastSlot()) {
            return BaseExpansionAxis.EAST;
        }
        if (slot == config.expandSouthSlot()) {
            return BaseExpansionAxis.SOUTH;
        }
        return null;
    }
}
