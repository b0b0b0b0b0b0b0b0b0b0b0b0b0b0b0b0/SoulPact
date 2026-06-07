package bm.b0b0b0.SoulPact.war.bridge;

import bm.b0b0b0.SoulPact.api.war.FlagBreakWarResult;
import bm.b0b0b0.SoulPact.api.war.OwnFlagWarBreakAction;
import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class WarFlagBreakGateImpl implements WarFlagBreakGate {

    private final ClanWarService warService;

    public WarFlagBreakGateImpl(ClanWarService warService) {
        this.warService = warService;
    }

    @Override
    public boolean allowsEnemyStandardBreak(UUID breakerId, long defenderClanId) {
        return warService.allowsEnemyStandardBreak(breakerId, defenderClanId);
    }

    @Override
    public void onEnemyStandardBreak(Player breaker, long defenderClanId, Location flagLocation, Runnable defaultDestroy) {
        warService.handleBrokenFlag(breaker, defenderClanId, flagLocation, defaultDestroy);
    }

    @Override
    public Optional<OwnFlagWarBreakAction> resolveOwnFlagBreak(UUID breakerId, long baseOwnerClanId) {
        return warService.resolveOwnFlagBreak(breakerId, baseOwnerClanId);
    }

    @Override
    public void onOwnFlagBreakDuringWar(
            Player breaker,
            long baseOwnerClanId,
            Location flagLocation,
            Runnable destroyBase,
            OwnFlagWarBreakAction action
    ) {
        warService.onOwnFlagBreakDuringWar(breaker, baseOwnerClanId, flagLocation, destroyBase, action);
    }

    @Override
    public FlagBreakWarResult handleBrokenFlag(
            Player breaker,
            long flagOwnerClanId,
            Location flagLocation,
            Runnable destroyBase
    ) {
        return warService.handleBrokenFlag(breaker, flagOwnerClanId, flagLocation, destroyBase);
    }
}
