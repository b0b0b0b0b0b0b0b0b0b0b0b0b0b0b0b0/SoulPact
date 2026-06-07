package bm.b0b0b0.SoulPact.api.war;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WarFlagBreakGate {

    boolean allowsEnemyStandardBreak(UUID breakerId, long defenderClanId);

    void onEnemyStandardBreak(Player breaker, long defenderClanId, Location flagLocation, Runnable defaultDestroy);

    Optional<OwnFlagWarBreakAction> resolveOwnFlagBreak(UUID breakerId, long baseOwnerClanId);

    void onOwnFlagBreakDuringWar(
            Player breaker,
            long baseOwnerClanId,
            Location flagLocation,
            Runnable destroyBase,
            OwnFlagWarBreakAction action
    );

    FlagBreakWarResult handleBrokenFlag(
            Player breaker,
            long flagOwnerClanId,
            Location flagLocation,
            Runnable destroyBase
    );
}
