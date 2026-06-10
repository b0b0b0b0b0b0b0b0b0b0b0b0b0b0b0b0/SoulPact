package bm.b0b0b0.SoulPact.leaderboard.placeholder;

import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import bm.b0b0b0.SoulPact.leaderboard.model.ClanStanding;
import bm.b0b0b0.SoulPact.leaderboard.service.StandingFormat;
import bm.b0b0b0.SoulPact.leaderboard.service.StandingsCache;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class LeaderboardPlaceholderResolver {

    private static final String PREFIX = "lb_";

    private final StandingsCache standingsCache;
    private final LeaderboardMessages messages;

    public LeaderboardPlaceholderResolver(StandingsCache standingsCache, LeaderboardMessages messages) {
        this.standingsCache = standingsCache;
        this.messages = messages;
    }

    public String resolve(Player player, String params) {
        if (params == null || !params.toLowerCase(Locale.ROOT).startsWith(PREFIX)) {
            return null;
        }
        String[] parts = params.substring(PREFIX.length()).split("_", 3);
        if (parts.length < 2) {
            return null;
        }
        Optional<BoardStatistic> statistic = BoardStatistic.parse(parts[0]);
        if (statistic.isEmpty()) {
            return null;
        }
        int rank;
        try {
            rank = Integer.parseInt(parts[1]);
        } catch (NumberFormatException exception) {
            return null;
        }
        String field = parts.length < 3 ? "tag" : parts[2].toLowerCase(Locale.ROOT);
        Optional<ClanStanding> standing = standingsCache.standing(statistic.get(), rank);
        return switch (field) {
            case "tag" -> standing.map(ClanStanding::tag)
                    .orElseGet(() -> messages.resolve("leaderboard.format.empty-tag", Map.of()));
            case "name" -> standing.map(ClanStanding::name)
                    .orElseGet(() -> messages.resolve("leaderboard.format.empty-name", Map.of()));
            case "amount", "value" -> standing
                    .map(resolved -> StandingFormat.amount(statistic.get(), resolved.value()))
                    .orElse("0");
            default -> null;
        };
    }
}
