package bm.b0b0b0.SoulPact.quests.listener;

import bm.b0b0b0.SoulPact.quests.model.QuestMission;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public final class QuestKillListener implements Listener {

    private final ClanQuestService questService;

    public QuestKillListener(ClanQuestService questService) {
        this.questService = questService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }
        QuestMission mission = victim instanceof Player ? QuestMission.KILL_PLAYERS : QuestMission.KILL_MOBS;
        String entityType = victim.getType().name();
        questService.recordProgress(
                killer.getUniqueId(),
                definition -> definition.mission() == mission && definition.matchesFilter(entityType),
                1
        );
    }
}
