package bm.b0b0b0.SoulPact.quests.repository;

import bm.b0b0b0.SoulPact.quests.model.ClanQuestRecord;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ClanQuestRepository {

    List<ClanQuestRecord> findAllActive();

    Map<String, ClanQuestRecord> findByClan(long clanId);

    Optional<ClanQuestRecord> findActive(long clanId);

    boolean insertActive(ClanQuestRecord record);

    boolean reactivate(ClanQuestRecord record);

    void updateProgress(long clanId, String questId, int progress);

    void markCompleted(long clanId, String questId, int progress, long completedAt);

    boolean deleteActive(long clanId, String questId);
}
