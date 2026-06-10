package bm.b0b0b0.SoulPact.leaderboard.repository;

import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import java.util.List;
import java.util.Optional;

public interface BoardRepository {

    Board insert(Board board);

    boolean deleteById(long boardId);

    Optional<Board> findById(long boardId);

    List<Board> findAll();
}
