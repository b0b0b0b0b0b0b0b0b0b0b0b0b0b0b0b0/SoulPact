package bm.b0b0b0.SoulPact.api.land;

import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanLandProvider extends SoulPactGuiExtension {

    CompletableFuture<Optional<ClanBaseSnapshot>> findBase(long clanId);

    default void applyWarCombatZone(long clanId) {
    }

    default void restoreCombatZone(long clanId) {
    }

    default void onMemberJoined(long clanId, UUID playerId) {
    }

    default void onMemberLeft(long clanId, UUID playerId) {
    }

    default void onLeadershipTransferred(long clanId, UUID previousLeaderId, UUID newLeaderId) {
    }

    default CompletableFuture<Void> destroyClanBase(long clanId) {
        return CompletableFuture.completedFuture(null);
    }
}
