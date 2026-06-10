package bm.b0b0b0.SoulPact.land.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.land.ClanBaseSnapshot;
import bm.b0b0b0.SoulPact.api.land.ClanLandProvider;
import bm.b0b0b0.SoulPact.land.gui.LandGuiService;
import bm.b0b0b0.SoulPact.land.service.ClanBaseService;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class LandExtension implements ClanLandProvider {

    private final ClanBaseService baseService;
    private final LandGuiService guiService;

    public LandExtension(ClanBaseService baseService, LandGuiService guiService) {
        this.baseService = baseService;
        this.guiService = guiService;
    }

    @Override
    public String id() {
        return "land";
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
    public CompletableFuture<Optional<ClanBaseSnapshot>> findBase(long clanId) {
        return baseService.findBase(clanId);
    }

    @Override
    public void applyWarCombatZone(long clanId) {
        baseService.applyWarCombatZone(clanId);
    }

    @Override
    public void restoreCombatZone(long clanId) {
        baseService.restoreCombatZone(clanId);
    }

    @Override
    public void openGui(Player player) {
        guiService.open(player);
    }

    @Override
    public void onMemberJoined(long clanId, UUID playerId) {
        baseService.addMemberToRegion(clanId, playerId);
    }

    @Override
    public void onMemberLeft(long clanId, UUID playerId) {
        baseService.removeMemberFromRegion(clanId, playerId);
    }

    @Override
    public void onLeadershipTransferred(long clanId, UUID previousLeaderId, UUID newLeaderId) {
        baseService.transferRegionOwnership(clanId, previousLeaderId, newLeaderId);
    }

    @Override
    public CompletableFuture<Void> destroyClanBase(long clanId) {
        return baseService.destroyBaseByClanId(clanId);
    }
}
