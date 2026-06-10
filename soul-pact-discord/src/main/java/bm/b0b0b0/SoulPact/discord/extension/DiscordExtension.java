package bm.b0b0b0.SoulPact.discord.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactExtension;

public final class DiscordExtension implements SoulPactExtension {

    private final Runnable reloadAction;

    public DiscordExtension(Runnable reloadAction) {
        this.reloadAction = reloadAction;
    }

    @Override
    public String id() {
        return "discord";
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
}
