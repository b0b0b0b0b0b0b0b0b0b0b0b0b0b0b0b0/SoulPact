package bm.b0b0b0.SoulPact.leaderboard.model;

import java.util.Locale;
import java.util.Optional;

public enum BoardKind {
    SIGN,
    STAND,
    HOLOGRAM;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<BoardKind> parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("HOLO")) {
            return Optional.of(HOLOGRAM);
        }
        try {
            return Optional.of(valueOf(normalized));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
