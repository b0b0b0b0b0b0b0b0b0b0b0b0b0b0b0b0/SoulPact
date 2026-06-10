package bm.b0b0b0.SoulPact.quests.placeholder;

import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.service.ActiveQuestState;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import bm.b0b0b0.SoulPact.quests.util.QuestTimeFormat;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class QuestPlaceholderResolver {

    private static final String PREFIX = "quest_";

    private final ClanQuestService questService;
    private final QuestsMessages messages;

    public QuestPlaceholderResolver(ClanQuestService questService, QuestsMessages messages) {
        this.questService = questService;
        this.messages = messages;
    }

    public String resolve(Player player, String params) {
        if (player == null || params == null) {
            return null;
        }
        String normalized = params.toLowerCase(Locale.ROOT);
        if (!normalized.startsWith(PREFIX)) {
            return null;
        }
        String key = normalized.substring(PREFIX.length());
        Optional<ActiveQuestState> state = activeState(player);
        return switch (key) {
            case "has_active" -> messages.resolveDefault(state.isPresent() ? "quests.place.yes" : "quests.place.no");
            case "active" -> state.map(value -> value.definition().id()).orElse("");
            case "active_name" -> state
                    .map(value -> messages.resolveDefault("quests.quest." + value.definition().id() + ".name"))
                    .orElseGet(() -> messages.resolveDefault("quests.place.none"));
            case "progress" -> state
                    .map(value -> String.valueOf(Math.min(value.progress(), value.definition().targetAmount())))
                    .orElse("0");
            case "target" -> state.map(value -> String.valueOf(value.definition().targetAmount())).orElse("0");
            case "percent" -> state.map(this::percent).orElse("0");
            case "time_left" -> state
                    .filter(value -> value.expiresAt() > 0)
                    .map(value -> QuestTimeFormat.remaining(messages, value.expiresAt()))
                    .orElseGet(() -> messages.resolveDefault("quests.place.none"));
            default -> null;
        };
    }

    private Optional<ActiveQuestState> activeState(Player player) {
        Long clanId = questService.cachedClanId(player.getUniqueId());
        if (clanId == null) {
            return Optional.empty();
        }
        return questService.activeState(clanId);
    }

    private String percent(ActiveQuestState state) {
        int target = Math.max(1, state.definition().targetAmount());
        int progress = Math.min(state.progress(), target);
        return String.valueOf(progress * 100 / target);
    }
}
