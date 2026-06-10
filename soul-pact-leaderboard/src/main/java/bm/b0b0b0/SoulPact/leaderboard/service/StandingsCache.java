package bm.b0b0b0.SoulPact.leaderboard.service;

import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import bm.b0b0b0.SoulPact.leaderboard.model.ClanStanding;
import bm.b0b0b0.SoulPact.leaderboard.repository.ClanStandingQuery;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class StandingsCache {

    private final ClanStandingQuery query;
    private volatile Map<BoardStatistic, List<ClanStanding>> standings = new EnumMap<>(BoardStatistic.class);

    public StandingsCache(ClanStandingQuery query) {
        this.query = query;
    }

    public void refresh(Set<BoardStatistic> statistics, int topSize) {
        Map<BoardStatistic, List<ClanStanding>> fresh = new EnumMap<>(BoardStatistic.class);
        for (BoardStatistic statistic : statistics) {
            fresh.put(statistic, query.top(statistic, topSize));
        }
        standings = fresh;
    }

    public Optional<ClanStanding> standing(BoardStatistic statistic, int rankPosition) {
        List<ClanStanding> list = standings.get(statistic);
        if (list == null || rankPosition < 1 || rankPosition > list.size()) {
            return Optional.empty();
        }
        return Optional.of(list.get(rankPosition - 1));
    }

    public List<ClanStanding> top(BoardStatistic statistic) {
        return standings.getOrDefault(statistic, List.of());
    }
}
