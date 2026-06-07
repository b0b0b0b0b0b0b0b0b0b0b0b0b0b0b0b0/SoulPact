package bm.b0b0b0.SoulPact.war.gui;

import java.util.Optional;

public record WarHubViewData(
        boolean viewerIsLeader,
        int pendingCount,
        Optional<WarEnemyTarget> enemy
) {
}
