package bm.b0b0b0.SoulPact.quests.listener;

import bm.b0b0b0.SoulPact.quests.model.QuestMission;
import bm.b0b0b0.SoulPact.quests.service.ClanQuestService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public final class QuestBlockListener implements Listener {

    private final ClanQuestService questService;

    public QuestBlockListener(ClanQuestService questService) {
        this.questService = questService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        String material = event.getBlock().getType().name();
        questService.recordProgress(
                event.getPlayer().getUniqueId(),
                definition -> definition.mission() == QuestMission.BREAK_BLOCKS && definition.matchesFilter(material),
                1
        );
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        String material = event.getBlockPlaced().getType().name();
        questService.recordProgress(
                event.getPlayer().getUniqueId(),
                definition -> definition.mission() == QuestMission.PLACE_BLOCKS && definition.matchesFilter(material),
                1
        );
    }
}
