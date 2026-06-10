package bm.b0b0b0.SoulPact.leaderboard.service;

import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BoardCatalog {

    private final List<Board> boards = new CopyOnWriteArrayList<>();

    public void replaceAll(List<Board> loaded) {
        boards.clear();
        boards.addAll(loaded);
    }

    public void add(Board board) {
        boards.add(board);
    }

    public void remove(long boardId) {
        boards.removeIf(board -> board.id() == boardId);
    }

    public Optional<Board> find(long boardId) {
        return boards.stream().filter(board -> board.id() == boardId).findFirst();
    }

    public List<Board> all() {
        return new ArrayList<>(boards);
    }

    public Set<BoardStatistic> usedStatistics() {
        Set<BoardStatistic> used = EnumSet.noneOf(BoardStatistic.class);
        for (Board board : boards) {
            used.add(board.statistic());
        }
        return used;
    }

    public boolean isEmpty() {
        return boards.isEmpty();
    }
}
