package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class ActiveQuestState {

    private final long clanId;
    private final QuestDefinition definition;
    private final long startedAt;
    private final long expiresAt;
    private final UUID startedBy;
    private final AtomicInteger progress;
    private final AtomicBoolean dirty = new AtomicBoolean(false);
    private final AtomicBoolean completionTriggered = new AtomicBoolean(false);

    public ActiveQuestState(
            long clanId,
            QuestDefinition definition,
            long startedAt,
            long expiresAt,
            UUID startedBy,
            int progress
    ) {
        this.clanId = clanId;
        this.definition = definition;
        this.startedAt = startedAt;
        this.expiresAt = expiresAt;
        this.startedBy = startedBy;
        this.progress = new AtomicInteger(progress);
    }

    public long clanId() {
        return clanId;
    }

    public QuestDefinition definition() {
        return definition;
    }

    public long startedAt() {
        return startedAt;
    }

    public long expiresAt() {
        return expiresAt;
    }

    public UUID startedBy() {
        return startedBy;
    }

    public int progress() {
        return progress.get();
    }

    public int increment(int amount) {
        dirty.set(true);
        return progress.addAndGet(amount);
    }

    public boolean consumeDirty() {
        return dirty.getAndSet(false);
    }

    public boolean isExpired(long now) {
        return expiresAt > 0 && now >= expiresAt;
    }

    public boolean reachedTarget() {
        return progress.get() >= definition.targetAmount();
    }

    public boolean tryTriggerCompletion() {
        return completionTriggered.compareAndSet(false, true);
    }
}
