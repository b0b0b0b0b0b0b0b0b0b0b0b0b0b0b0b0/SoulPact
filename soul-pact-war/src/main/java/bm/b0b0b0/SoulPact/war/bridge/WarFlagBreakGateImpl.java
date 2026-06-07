package bm.b0b0b0.SoulPact.war.bridge;

import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
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
        warService.onEnemyStandardBroken(breaker, defenderClanId);
        defaultDestroy.run();
    }
}
