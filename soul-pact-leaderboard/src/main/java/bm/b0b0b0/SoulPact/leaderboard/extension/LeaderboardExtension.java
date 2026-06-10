package bm.b0b0b0.SoulPact.leaderboard.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.api.placeholder.SoulPactPlaceholderBridge;
import bm.b0b0b0.SoulPact.leaderboard.placeholder.LeaderboardPlaceholderResolver;
import org.bukkit.entity.Player;

public final class LeaderboardExtension implements SoulPactExtension, SoulPactPlaceholderBridge {

    private final LeaderboardPlaceholderResolver placeholderResolver;
    private final Runnable reloadAction;

    public LeaderboardExtension(LeaderboardPlaceholderResolver placeholderResolver, Runnable reloadAction) {
        this.placeholderResolver = placeholderResolver;
        this.reloadAction = reloadAction;
    }

    @Override
    public String id() {
        return "leaderboard";
    }

    @Override
    public void enable(SoulPactApi api) {
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
        reloadAction.run();
    }

    @Override
    public String resolve(Player player, String params) {
        return placeholderResolver.resolve(player, params);
    }
}
