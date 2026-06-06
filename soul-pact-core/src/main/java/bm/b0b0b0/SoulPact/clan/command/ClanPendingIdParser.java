package bm.b0b0b0.SoulPact.clan.command;

import java.util.Optional;

public final class ClanPendingIdParser {

    private ClanPendingIdParser() {
    }

    public static Optional<Long> parse(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return Optional.empty();
        }
        try {
            long value = Long.parseLong(rawValue.trim());
            return value > 0 ? Optional.of(value) : Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
