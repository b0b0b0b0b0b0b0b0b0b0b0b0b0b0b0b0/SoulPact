package bm.b0b0b0.SoulPact.gladiator.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.api.placeholder.SoulPactPlaceholderBridge;
import bm.b0b0b0.SoulPact.gladiator.gui.GladiatorGuiService;
import bm.b0b0b0.SoulPact.gladiator.placeholder.GladiatorPlaceholderResolver;
import org.bukkit.entity.Player;

public final class GladiatorExtension implements SoulPactGuiExtension, SoulPactPlaceholderBridge {

    private final GladiatorGuiService guiService;
    private final GladiatorPlaceholderResolver placeholderResolver;
    private final Runnable reloadAction;

    public GladiatorExtension(
            GladiatorGuiService guiService,
            GladiatorPlaceholderResolver placeholderResolver,
            Runnable reloadAction
    ) {
        this.guiService = guiService;
        this.placeholderResolver = placeholderResolver;
        this.reloadAction = reloadAction;
    }

    @Override
    public String id() {
        return "gladiator";
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
    public void openGui(Player player) {
        guiService.open(player);
    }

    @Override
    public String resolve(Player player, String params) {
        return placeholderResolver.resolve(player, params);
    }
}
