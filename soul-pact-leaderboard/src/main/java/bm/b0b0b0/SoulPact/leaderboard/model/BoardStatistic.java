package bm.b0b0b0.SoulPact.leaderboard.model;

import java.util.Locale;
import java.util.Optional;

public enum BoardStatistic {
    POINTS,
    MEMBERS,
    KILLS,
    DEATHS,
    KDR,
    WARS,
    BANK;

    public String key() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<BoardStatistic> parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(valueOf(rawValue.trim().toUpperCase(Locale.ROOT)));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
