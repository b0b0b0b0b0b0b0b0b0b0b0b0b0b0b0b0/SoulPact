package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class QuestCatalog {

    private final AtomicReference<Map<String, QuestDefinition>> definitions = new AtomicReference<>(Map.of());

    public QuestCatalog(List<QuestDefinition> initial) {
        update(initial);
    }

    public void update(List<QuestDefinition> quests) {
        Map<String, QuestDefinition> byId = new LinkedHashMap<>();
        for (QuestDefinition definition : quests) {
            byId.put(definition.id(), definition);
        }
        definitions.set(Map.copyOf(byId));
    }

    public Optional<QuestDefinition> find(String questId) {
        if (questId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(definitions.get().get(questId.toLowerCase(Locale.ROOT)));
    }

    public List<QuestDefinition> all() {
        return List.copyOf(definitions.get().values());
    }

    public int size() {
        return definitions.get().size();
    }
}
