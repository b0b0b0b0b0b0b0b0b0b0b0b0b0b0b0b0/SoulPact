package bm.b0b0b0.SoulPact.leaderboard.render;

import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.ClanStanding;
import bm.b0b0b0.SoulPact.leaderboard.service.StandingsCache;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Location;

public final class BoardRenderService {

    private final StandingsCache standingsCache;
    private final BoardPlaceholders boardPlaceholders;
    private final SignBoardRenderer signRenderer;
    private final StandBoardRenderer standRenderer;
    private final HologramBoardRenderer hologramRenderer;

    public BoardRenderService(
            StandingsCache standingsCache,
            BoardPlaceholders boardPlaceholders,
            SignBoardRenderer signRenderer,
            StandBoardRenderer standRenderer,
            HologramBoardRenderer hologramRenderer
    ) {
        this.standingsCache = standingsCache;
        this.boardPlaceholders = boardPlaceholders;
        this.signRenderer = signRenderer;
        this.standRenderer = standRenderer;
        this.hologramRenderer = hologramRenderer;
    }

    public void render(Board board) {
        Optional<Location> locationOptional = board.location();
        if (locationOptional.isEmpty()) {
            return;
        }
        Location location = locationOptional.get();
        if (!location.isChunkLoaded()) {
            return;
        }
        Optional<ClanStanding> standing = standingsCache.standing(board.statistic(), board.rankPosition());
        Map<String, String> placeholders = boardPlaceholders.build(board, standing);
        switch (board.kind()) {
            case SIGN -> signRenderer.render(location, placeholders);
            case STAND -> standRenderer.render(board, location, standing, placeholders);
            case HOLOGRAM -> hologramRenderer.render(board, location, placeholders);
        }
    }

    public void removeRendered(Board board) {
        Optional<Location> locationOptional = board.location();
        if (locationOptional.isEmpty()) {
            return;
        }
        Location location = locationOptional.get();
        if (!location.isChunkLoaded()) {
            return;
        }
        standRenderer.removeEntities(board, location);
        hologramRenderer.removeEntities(board, location);
    }
}
