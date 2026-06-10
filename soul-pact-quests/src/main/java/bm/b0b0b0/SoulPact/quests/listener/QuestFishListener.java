package bm.b0b0b0.SoulPact.quests.listener;

import bm.b0b0b0.SoulPact.quests.model.QuestMission;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public final class QuestFishListener implements Listener {

    private final ClanQuestService questService;

    public QuestFishListener(ClanQuestService questService) {
        this.questService = questService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) {
            return;
        }
        String caughtType = event.getCaught() instanceof Item item
                ? item.getItemStack().getType().name()
                : null;
        questService.recordProgress(
                event.getPlayer().getUniqueId(),
                definition -> definition.mission() == QuestMission.FISH && definition.matchesFilter(caughtType),
                1
        );
    }
}
