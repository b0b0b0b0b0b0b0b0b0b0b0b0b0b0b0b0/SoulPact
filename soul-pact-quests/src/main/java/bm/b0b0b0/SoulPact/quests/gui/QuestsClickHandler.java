package bm.b0b0b0.SoulPact.quests.gui;

import bm.b0b0b0.SoulPact.quests.config.QuestsConfig;
import bm.b0b0b0.SoulPact.quests.service.QuestEntryView;
import org.bukkit.entity.Player;

public final class QuestsClickHandler {

    private final QuestsGuiService guiService;
    private final QuestsClanNavigation clanNavigation;
    private final QuestsConfig config;

    public QuestsClickHandler(QuestsGuiService guiService, QuestsClanNavigation clanNavigation, QuestsConfig config) {
        this.guiService = guiService;
        this.clanNavigation = clanNavigation;
        this.config = config;
    }

    public void handle(QuestsMenu menu, Player player, int slot) {
        if (slot == config.backSlot()) {
            clanNavigation.openHub(player);
            return;
        }
        QuestEntryView entry = menu.entryAt(slot);
        if (entry == null || !menu.overview().canManage()) {
            return;
        }
        switch (entry.state()) {
            case ACTIVE -> guiService.abandon(player);
            case AVAILABLE -> {
                if (menu.overview().activeEntry().isEmpty()) {
                    guiService.start(player, entry.definition().id());
                }
            }
            case ON_COOLDOWN, COMPLETED -> {
            }
        }
    }
}
