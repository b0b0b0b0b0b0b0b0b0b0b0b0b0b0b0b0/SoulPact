package bm.b0b0b0.SoulPact.leaderboard.service;

import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import java.util.Locale;

public final class StandingFormat {

    private StandingFormat() {
    }

    public static String amount(BoardStatistic statistic, double value) {
        return switch (statistic) {
            case KDR -> String.format(Locale.ROOT, "%.2f", value);
            case BANK -> String.format(Locale.ROOT, "%.2f", value);
            default -> String.valueOf((long) value);
        };
    }
}
