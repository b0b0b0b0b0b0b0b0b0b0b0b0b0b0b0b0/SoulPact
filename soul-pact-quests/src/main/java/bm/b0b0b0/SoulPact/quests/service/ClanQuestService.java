package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.quests.config.QuestsConfig;
import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.model.ClanQuestRecord;
import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import bm.b0b0b0.SoulPact.quests.model.QuestStatus;
import bm.b0b0b0.SoulPact.quests.model.QuestType;
import bm.b0b0b0.SoulPact.quests.repository.ClanQuestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanQuestService {

    private final SoulPactApi api;
    private final QuestsConfig config;
    private final QuestCatalog catalog;
    private final ClanQuestRepository repository;
    private final QuestProgressTracker tracker;
    private final PlayerClanCache playerClanCache;
    private final QuestRewardService rewardService;
    private final QuestsMessages messages;

    public ClanQuestService(
            SoulPactApi api,
            QuestsConfig config,
            QuestCatalog catalog,
            ClanQuestRepository repository,
            QuestProgressTracker tracker,
            PlayerClanCache playerClanCache,
            QuestRewardService rewardService,
            QuestsMessages messages
    ) {
        this.api = api;
        this.config = config;
        this.catalog = catalog;
        this.repository = repository;
        this.tracker = tracker;
        this.playerClanCache = playerClanCache;
        this.rewardService = rewardService;
        this.messages = messages;
    }

    public CompletableFuture<Void> loadActiveStates() {
        return api.scheduler().runAsync(() -> {
            long now = System.currentTimeMillis();
            for (ClanQuestRecord record : repository.findAllActive()) {
                Optional<QuestDefinition> definition = catalog.find(record.questId());
                if (definition.isEmpty()) {
                    repository.deleteActive(record.clanId(), record.questId());
                    continue;
                }
                ActiveQuestState state = new ActiveQuestState(
                        record.clanId(),
                        definition.get(),
                        record.startedAt(),
                        record.expiresAt(),
                        record.startedBy(),
                        record.progress()
                );
                if (state.isExpired(now)) {
                    repository.deleteActive(record.clanId(), record.questId());
                    continue;
                }
                tracker.putIfAbsent(state);
            }
        });
    }

    public void recordProgress(UUID playerId, java.util.function.Predicate<QuestDefinition> missionMatcher, int amount) {
        Long cachedClanId = playerClanCache.cachedClanId(playerId);
        if (cachedClanId != null) {
            applyProgress(cachedClanId, playerId, missionMatcher, amount);
            return;
        }
        playerClanCache.lookup(playerId, clanId -> {
            if (clanId != null) {
                applyProgress(clanId, playerId, missionMatcher, amount);
            }
        });
    }

    public CompletableFuture<QuestActionResult> start(Player player, String questId) {
        Optional<QuestDefinition> definitionOptional = catalog.find(questId);
        if (definitionOptional.isEmpty()) {
            return CompletableFuture.completedFuture(QuestActionResult.UNKNOWN_QUEST);
        }
        QuestDefinition definition = definitionOptional.get();
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(QuestActionResult.NOT_IN_CLAN);
            }
            ClanSnapshot clan = clanOptional.get();
            if (!canManage(player, clan)) {
                return CompletableFuture.completedFuture(QuestActionResult.NO_MANAGE_PERMISSION);
            }
            return api.scheduler().supplyAsync(() -> executeStart(player, clan, definition));
        });
    }

    public CompletableFuture<QuestActionResult> abandon(Player player) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(QuestActionResult.NOT_IN_CLAN);
            }
            ClanSnapshot clan = clanOptional.get();
            if (!canManage(player, clan)) {
                return CompletableFuture.completedFuture(QuestActionResult.NO_MANAGE_PERMISSION);
            }
            return api.scheduler().supplyAsync(() -> executeAbandon(clan));
        });
    }

    public CompletableFuture<Optional<QuestsOverview>> loadOverview(Player player) {
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            ClanSnapshot clan = clanOptional.get();
            return api.scheduler().supplyAsync(() -> Optional.of(buildOverview(player, clan)));
        });
    }

    public void flushProgress() {
        long now = System.currentTimeMillis();
        for (ActiveQuestState state : tracker.all()) {
            if (state.isExpired(now)) {
                expire(state);
                continue;
            }
            if (state.consumeDirty()) {
                repository.updateProgress(state.clanId(), state.definition().id(), Math.min(state.progress(), state.definition().targetAmount()));
            }
        }
    }

    public Optional<ActiveQuestState> activeState(long clanId) {
        return tracker.state(clanId);
    }

    public Long cachedClanId(UUID playerId) {
        return playerClanCache.cachedClanId(playerId);
    }

    private void applyProgress(long clanId, UUID playerId, java.util.function.Predicate<QuestDefinition> missionMatcher, int amount) {
        Optional<ActiveQuestState> stateOptional = tracker.state(clanId);
        if (stateOptional.isEmpty()) {
            return;
        }
        ActiveQuestState state = stateOptional.get();
        if (!missionMatcher.test(state.definition())) {
            return;
        }
        if (state.isExpired(System.currentTimeMillis())) {
            api.scheduler().runAsync(() -> expire(state));
            return;
        }
        state.increment(amount);
        if (state.reachedTarget() && state.tryTriggerCompletion()) {
            api.scheduler().runAsync(() -> complete(state, playerId));
        }
    }

    private QuestActionResult executeStart(Player player, ClanSnapshot clan, QuestDefinition definition) {
        if (tracker.hasActive(clan.id())) {
            return QuestActionResult.ALREADY_ACTIVE;
        }
        Map<String, ClanQuestRecord> records = repository.findByClan(clan.id());
        if (records.values().stream().anyMatch(record -> record.status() == QuestStatus.ACTIVE)) {
            return QuestActionResult.ALREADY_ACTIVE;
        }
        long now = System.currentTimeMillis();
        ClanQuestRecord existing = records.get(definition.id());
        if (existing != null && existing.status() == QuestStatus.COMPLETED) {
            if (definition.type() == QuestType.ONCE) {
                return QuestActionResult.ALREADY_COMPLETED;
            }
            if (now < existing.completedAt() + config.dailyCooldownMillis()) {
                return QuestActionResult.ON_COOLDOWN;
            }
        }
        long expiresAt = definition.type() == QuestType.DAILY ? now + config.dailyDurationMillis() : 0L;
        ClanQuestRecord record = new ClanQuestRecord(
                clan.id(),
                definition.id(),
                QuestStatus.ACTIVE,
                0,
                now,
                expiresAt,
                0L,
                player.getUniqueId()
        );
        boolean persisted = existing == null ? repository.insertActive(record) : repository.reactivate(record);
        if (!persisted) {
            return QuestActionResult.FAILED;
        }
        ActiveQuestState state = new ActiveQuestState(clan.id(), definition, now, expiresAt, player.getUniqueId(), 0);
        if (!tracker.putIfAbsent(state)) {
            return QuestActionResult.ALREADY_ACTIVE;
        }
        broadcastToClan(clan.id(), "quests.broadcast.started", Map.of(
                "quest", questName(definition),
                "player", player.getName(),
                "tag", clan.tag()
        ));
        return QuestActionResult.STARTED;
    }

    private QuestActionResult executeAbandon(ClanSnapshot clan) {
        Optional<ActiveQuestState> stateOptional = tracker.state(clan.id());
        if (stateOptional.isEmpty()) {
            return QuestActionResult.NO_ACTIVE_QUEST;
        }
        ActiveQuestState state = stateOptional.get();
        tracker.remove(clan.id());
        repository.deleteActive(clan.id(), state.definition().id());
        broadcastToClan(clan.id(), "quests.broadcast.abandoned", Map.of(
                "quest", questName(state.definition()),
                "tag", clan.tag()
        ));
        return QuestActionResult.ABANDONED;
    }

    private void complete(ActiveQuestState state, UUID finisherId) {
        long clanId = state.clanId();
        QuestDefinition definition = state.definition();
        tracker.remove(clanId);
        repository.markCompleted(clanId, definition.id(), definition.targetAmount(), System.currentTimeMillis());
        api.findClanByPlayer(finisherId).thenAccept(clanOptional -> {
            String tag = clanOptional.map(ClanSnapshot::tag).orElse("");
            rewardService.award(clanId, definition, finisherId, tag);
            broadcastToClan(clanId, "quests.broadcast.completed", Map.of(
                    "quest", questName(definition),
                    "points", String.valueOf(definition.rewardPoints()),
                    "tag", tag
            ));
        });
    }

    private void expire(ActiveQuestState state) {
        if (!state.tryTriggerCompletion()) {
            return;
        }
        tracker.remove(state.clanId());
        repository.deleteActive(state.clanId(), state.definition().id());
        broadcastToClan(state.clanId(), "quests.broadcast.expired", Map.of(
                "quest", questName(state.definition())
        ));
    }

    private QuestsOverview buildOverview(Player player, ClanSnapshot clan) {
        Map<String, ClanQuestRecord> records = repository.findByClan(clan.id());
        Optional<ActiveQuestState> activeState = tracker.state(clan.id());
        long now = System.currentTimeMillis();
        List<QuestEntryView> entries = new ArrayList<>();
        for (QuestDefinition definition : catalog.all()) {
            entries.add(buildEntry(definition, records.get(definition.id()), activeState.orElse(null), now));
        }
        return new QuestsOverview(clan, List.copyOf(entries), canManage(player, clan));
    }

    private QuestEntryView buildEntry(
            QuestDefinition definition,
            ClanQuestRecord record,
            ActiveQuestState activeState,
            long now
    ) {
        if (activeState != null && activeState.definition().id().equals(definition.id())) {
            return new QuestEntryView(
                    definition,
                    QuestEntryView.QuestEntryState.ACTIVE,
                    Math.min(activeState.progress(), definition.targetAmount()),
                    activeState.expiresAt(),
                    0L
            );
        }
        if (record != null && record.status() == QuestStatus.COMPLETED) {
            if (definition.type() == QuestType.ONCE) {
                return new QuestEntryView(definition, QuestEntryView.QuestEntryState.COMPLETED, definition.targetAmount(), 0L, 0L);
            }
            long cooldownEndsAt = record.completedAt() + config.dailyCooldownMillis();
            if (now < cooldownEndsAt) {
                return new QuestEntryView(definition, QuestEntryView.QuestEntryState.ON_COOLDOWN, 0, 0L, cooldownEndsAt);
            }
        }
        return new QuestEntryView(definition, QuestEntryView.QuestEntryState.AVAILABLE, 0, 0L, 0L);
    }

    private boolean canManage(Player player, ClanSnapshot clan) {
        if (!config.leaderOnlyManage()) {
            return true;
        }
        return clan.leaderId().equals(player.getUniqueId());
    }

    private String questName(QuestDefinition definition) {
        return messages.resolveDefault("quests.quest." + definition.id() + ".name");
    }

    private void broadcastToClan(long clanId, String key, Map<String, String> placeholders) {
        api.scheduler().runSync(() -> {
            for (Player online : Bukkit.getOnlinePlayers()) {
                playerClanCache.lookup(online.getUniqueId(), onlineClanId -> {
                    if (onlineClanId == null || onlineClanId != clanId) {
                        return;
                    }
                    api.scheduler().runSync(() -> {
                        if (online.isOnline()) {
                            messages.send(online, key, placeholders);
                        }
                    });
                });
            }
        });
    }
}
