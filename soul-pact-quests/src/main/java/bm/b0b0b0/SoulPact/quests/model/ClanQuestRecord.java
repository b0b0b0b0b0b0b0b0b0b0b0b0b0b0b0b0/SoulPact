package bm.b0b0b0.SoulPact.quests.model;

import java.util.UUID;

public record ClanQuestRecord(
        long clanId,
        String questId,
        QuestStatus status,
        int progress,
        long startedAt,
        long expiresAt,
        long completedAt,
        UUID startedBy
) {
}
