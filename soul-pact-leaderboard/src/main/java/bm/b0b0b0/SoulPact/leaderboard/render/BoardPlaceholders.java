package bm.b0b0b0.SoulPact.leaderboard.render;

import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.ClanStanding;
import bm.b0b0b0.SoulPact.leaderboard.service.StandingFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BoardPlaceholders {

    private final LeaderboardMessages messages;

    public BoardPlaceholders(LeaderboardMessages messages) {
        this.messages = messages;
    }

    public Map<String, String> build(Board board, Optional<ClanStanding> standing) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("num", String.valueOf(board.rankPosition()));
        placeholders.put("type", messages.resolve("leaderboard.type." + board.statistic().key(), Map.of()));
        if (standing.isPresent()) {
            ClanStanding resolved = standing.get();
            placeholders.put("tag", resolved.tag());
            placeholders.put("name", resolved.name());
            placeholders.put("amount", StandingFormat.amount(board.statistic(), resolved.value()));
        } else {
            placeholders.put("tag", messages.resolve("leaderboard.format.empty-tag", Map.of()));
            placeholders.put("name", messages.resolve("leaderboard.format.empty-name", Map.of()));
            placeholders.put("amount", "0");
        }
        return placeholders;
    }
}
