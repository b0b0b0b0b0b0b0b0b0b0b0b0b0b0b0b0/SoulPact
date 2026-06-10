package bm.b0b0b0.SoulPact.land.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.land.config.LandConfig;
import bm.b0b0b0.SoulPact.land.message.LandMessages;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import org.bukkit.entity.Player;

public final class LandGuiService {

    private final SoulPactApi api;
    private final LandConfig config;
    private final LandMessages messages;
    private final ClanBaseService baseService;
    private final LandMenuPopulator populator;
    private final LandClickHandler clickHandler;

    public LandGuiService(
            SoulPactApi api,
            LandConfig config,
            LandMessages messages,
            ClanBaseService baseService
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.baseService = baseService;
        this.populator = new LandMenuPopulator(config, messages, baseService);
        LandClanNavigation clanNavigation = new LandClanNavigation(api);
        this.clickHandler = new LandClickHandler(this, clanNavigation, baseService, config);
    }

    public LandClickHandler clickHandler() {
        return clickHandler;
    }

    public void open(Player player) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (clanOptional.isEmpty()) {
                messages.send(player, "land.error.not-in-clan");
                return;
            }
            openLoaded(player, clanOptional.get());
        }));
    }

    public void refresh(Player player, ClanSnapshot clan) {
        openLoaded(player, clan);
    }

    private void openLoaded(Player player, ClanSnapshot clan) {
        baseService.findBaseRecord(clan.id()).thenCombine(
                api.clanAccess().hasPermission(clan.id(), player.getUniqueId(), ClanPermissionKeys.LAND_MANAGE),
                (baseOptional, canManage) -> new LandMenuSnapshot(clan, baseOptional, canManage)
        ).thenAccept(snapshot -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            LandMenu menu = new LandMenu(config, messages, populator, player, snapshot);
            player.openInventory(menu.getInventory());
        }));
    }
}
