package bm.b0b0b0.SoulPact.leaderboard.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardKind;
import bm.b0b0b0.SoulPact.leaderboard.model.BoardStatistic;
import bm.b0b0b0.SoulPact.leaderboard.render.BoardRenderService;
import bm.b0b0b0.SoulPact.leaderboard.repository.BoardRepository;
import java.util.Optional;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public final class BoardCreationService {

    private final SoulPactApi api;
    private final BoardRepository repository;
    private final BoardCatalog catalog;
    private final BoardUpdateService updateService;
    private final BoardRenderService renderService;

    public BoardCreationService(
            SoulPactApi api,
            BoardRepository repository,
            BoardCatalog catalog,
            BoardUpdateService updateService,
            BoardRenderService renderService
    ) {
        this.api = api;
        this.repository = repository;
        this.catalog = catalog;
        this.updateService = updateService;
        this.renderService = renderService;
    }

    public enum CreationResult {
        CREATED,
        NO_TARGET,
        NOT_A_SIGN
    }

    public void create(
            Player player,
            BoardStatistic statistic,
            int rankPosition,
            BoardKind kind,
            Consumer<CreationResult> callback
    ) {
        Optional<Location> placement = resolvePlacement(player, kind);
        if (placement.isEmpty()) {
            callback.accept(kind == BoardKind.SIGN ? CreationResult.NOT_A_SIGN : CreationResult.NO_TARGET);
            return;
        }
        Location location = placement.get();
        Board draft = new Board(
                0,
                statistic,
                rankPosition,
                kind,
                location.getWorld().getName(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw()
        );
        api.scheduler().supplyAsync(() -> repository.insert(draft)).thenAccept(saved ->
                api.scheduler().runSync(() -> {
                    catalog.add(saved);
                    updateService.updateAll();
                    callback.accept(CreationResult.CREATED);
                })
        );
    }

    public void delete(long boardId, Consumer<Boolean> callback) {
        Optional<Board> boardOptional = catalog.find(boardId);
        api.scheduler().supplyAsync(() -> repository.deleteById(boardId)).thenAccept(deleted ->
                api.scheduler().runSync(() -> {
                    if (deleted) {
                        boardOptional.ifPresent(renderService::removeRendered);
                        catalog.remove(boardId);
                    }
                    callback.accept(deleted);
                })
        );
    }

    private Optional<Location> resolvePlacement(Player player, BoardKind kind) {
        Block target = player.getTargetBlockExact(8);
        if (target == null) {
            return Optional.empty();
        }
        if (kind == BoardKind.SIGN) {
            if (!(target.getState() instanceof Sign)) {
                return Optional.empty();
            }
            return Optional.of(target.getLocation());
        }
        Location above = target.getLocation().add(0.5, 1.0, 0.5);
        above.setYaw(player.getLocation().getYaw() + 180.0F);
        return Optional.of(above);
    }
}
