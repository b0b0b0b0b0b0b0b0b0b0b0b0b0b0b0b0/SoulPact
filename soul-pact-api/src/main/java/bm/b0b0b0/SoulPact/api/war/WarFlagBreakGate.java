package bm.b0b0b0.SoulPact.api.war;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface WarFlagBreakGate {

    boolean allowsEnemyStandardBreak(UUID breakerId, long defenderClanId);

    void onEnemyStandardBreak(Player breaker, long defenderClanId, Location flagLocation, Runnable defaultDestroy);
}
