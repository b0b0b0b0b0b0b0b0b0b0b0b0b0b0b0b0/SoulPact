package bm.b0b0b0.SoulPact.leaderboard.listener;

import bm.b0b0b0.SoulPact.api.event.ClanCreateEvent;
import bm.b0b0b0.SoulPact.api.event.ClanDisbandEvent;
import bm.b0b0b0.SoulPact.api.event.ClanMemberJoinEvent;
import bm.b0b0b0.SoulPact.api.event.ClanMemberLeaveEvent;
import bm.b0b0b0.SoulPact.api.event.ClanQuestCompleteEvent;
import bm.b0b0b0.SoulPact.api.event.ClanWarEndEvent;
import bm.b0b0b0.SoulPact.leaderboard.service.BoardUpdateService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class LeaderboardEventListener implements Listener {

    private final BoardUpdateService updateService;

    public LeaderboardEventListener(BoardUpdateService updateService) {
        this.updateService = updateService;
    }

    @EventHandler
    public void onClanCreate(ClanCreateEvent event) {
        updateService.requestEventUpdate();
    }

    @EventHandler
    public void onClanDisband(ClanDisbandEvent event) {
        updateService.requestEventUpdate();
    }

    @EventHandler
    public void onMemberJoin(ClanMemberJoinEvent event) {
        updateService.requestEventUpdate();
    }

    @EventHandler
    public void onMemberLeave(ClanMemberLeaveEvent event) {
        updateService.requestEventUpdate();
    }

    @EventHandler
    public void onWarEnd(ClanWarEndEvent event) {
        updateService.requestEventUpdate();
    }

    @EventHandler
    public void onQuestComplete(ClanQuestCompleteEvent event) {
        updateService.requestEventUpdate();
    }
}
