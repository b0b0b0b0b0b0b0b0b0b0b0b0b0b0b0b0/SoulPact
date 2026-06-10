package bm.b0b0b0.SoulPact.quests.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.api.placeholder.SoulPactPlaceholderBridge;
import bm.b0b0b0.SoulPact.quests.gui.QuestsGuiService;
import bm.b0b0b0.SoulPact.quests.placeholder.QuestPlaceholderResolver;
import org.bukkit.entity.Player;

public final class QuestsExtension implements SoulPactGuiExtension, SoulPactPlaceholderBridge {

    private final QuestsGuiService guiService;
    private final QuestPlaceholderResolver placeholderResolver;
    private final Runnable reloadAction;

    public QuestsExtension(
            QuestsGuiService guiService,
            QuestPlaceholderResolver placeholderResolver,
            Runnable reloadAction
    ) {
        this.guiService = guiService;
        this.placeholderResolver = placeholderResolver;
        this.reloadAction = reloadAction;
    }

    @Override
    public String id() {
        return "quests";
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
