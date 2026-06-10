package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class ClanPlaceholderInvalidatorRegistry {

    private static final AtomicReference<ClanPlaceholderInvalidator> INVALIDATOR = new AtomicReference<>();

    private ClanPlaceholderInvalidatorRegistry() {
    }

    public static void install(ClanPlaceholderInvalidator invalidator) {
        INVALIDATOR.set(invalidator);
    }

    public static void uninstall() {
        INVALIDATOR.set(null);
    }

    public static void invalidatePlayer(UUID playerId) {
        ClanPlaceholderInvalidator invalidator = INVALIDATOR.get();
        if (invalidator != null) {
            invalidator.invalidatePlayer(playerId);
        }
    }

    public static void invalidateClan(long clanId) {
        ClanPlaceholderInvalidator invalidator = INVALIDATOR.get();
        if (invalidator != null) {
            invalidator.invalidateClan(clanId);
        }
    }

    public static void invalidateAll() {
        ClanPlaceholderInvalidator invalidator = INVALIDATOR.get();
        if (invalidator != null) {
            invalidator.invalidateAll();
        }
    }
}
