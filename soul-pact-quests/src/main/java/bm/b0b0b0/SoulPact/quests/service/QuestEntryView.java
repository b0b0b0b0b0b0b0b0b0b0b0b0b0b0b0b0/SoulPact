package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;

public record QuestEntryView(
        QuestDefinition definition,
        QuestEntryState state,
        int progress,
        long expiresAt,
        long cooldownEndsAt
) {

    public enum QuestEntryState {
        AVAILABLE,
        ACTIVE,
        ON_COOLDOWN,
        COMPLETED
    }
}
