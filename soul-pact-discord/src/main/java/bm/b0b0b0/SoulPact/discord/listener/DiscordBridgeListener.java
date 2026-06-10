package bm.b0b0b0.SoulPact.discord.listener;

import bm.b0b0b0.SoulPact.api.event.ClanCreateEvent;
import bm.b0b0b0.SoulPact.api.event.ClanDescriptionChangeEvent;
import bm.b0b0b0.SoulPact.api.event.ClanDisbandEvent;
import bm.b0b0b0.SoulPact.api.event.ClanLeaderChangeEvent;
import bm.b0b0b0.SoulPact.api.event.ClanMemberJoinEvent;
import bm.b0b0b0.SoulPact.api.event.ClanMemberLeaveEvent;
import bm.b0b0b0.SoulPact.api.event.ClanMemberRoleChangeEvent;
import bm.b0b0b0.SoulPact.api.event.ClanQuestCompleteEvent;
import bm.b0b0b0.SoulPact.api.event.ClanTagChangeEvent;
import bm.b0b0b0.SoulPact.api.event.ClanWarEndEvent;
import bm.b0b0b0.SoulPact.api.event.ClanWarStartEvent;
import bm.b0b0b0.SoulPact.api.event.GladiatorEventStartEvent;
import bm.b0b0b0.SoulPact.api.event.GladiatorEventWinEvent;
import bm.b0b0b0.SoulPact.discord.model.DiscordEventType;
import bm.b0b0b0.SoulPact.discord.webhook.DiscordEventPublisher;
import java.util.Map;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class DiscordBridgeListener implements Listener {

    private final DiscordEventPublisher publisher;

    public DiscordBridgeListener(DiscordEventPublisher publisher) {
        this.publisher = publisher;
    }

    @EventHandler
    public void onClanCreate(ClanCreateEvent event) {
        publisher.publish(DiscordEventType.CLAN_CREATE, Map.of(
                "tag", event.tag(),
                "name", event.clanName(),
                "player", event.leaderName()
        ));
    }

    @EventHandler
    public void onClanDisband(ClanDisbandEvent event) {
        publisher.publish(DiscordEventType.CLAN_DELETE, Map.of(
                "tag", event.tag(),
                "name", event.clanName(),
                "actor", event.actorName()
        ));
    }

    @EventHandler
    public void onTagChange(ClanTagChangeEvent event) {
        publisher.publish(DiscordEventType.TAG_CHANGE, Map.of(
                "old_tag", event.oldTag(),
                "new_tag", event.newTag(),
                "actor", event.actorName()
        ));
    }

    @EventHandler
    public void onDescriptionChange(ClanDescriptionChangeEvent event) {
        publisher.publish(DiscordEventType.DESC_CHANGE, Map.of(
                "tag", event.tag(),
                "description", event.description(),
                "actor", event.actorName()
        ));
    }

    @EventHandler
    public void onRoleChange(ClanMemberRoleChangeEvent event) {
        publisher.publish(DiscordEventType.ROLE_CHANGE, Map.of(
                "tag", event.tag(),
                "player", event.playerName(),
                "old_role", event.oldRole(),
                "new_role", event.newRole()
        ));
    }

    @EventHandler
    public void onMemberJoin(ClanMemberJoinEvent event) {
        publisher.publish(DiscordEventType.MEMBER_JOIN, Map.of(
                "tag", event.tag(),
                "player", event.playerName()
        ));
    }

    @EventHandler
    public void onMemberLeave(ClanMemberLeaveEvent event) {
        DiscordEventType type = event.reason() == ClanMemberLeaveEvent.Reason.KICK
                ? DiscordEventType.MEMBER_KICK
                : DiscordEventType.MEMBER_LEAVE;
        publisher.publish(type, Map.of(
                "tag", event.tag(),
                "player", event.playerName()
        ));
    }

    @EventHandler
    public void onLeaderChange(ClanLeaderChangeEvent event) {
        publisher.publish(DiscordEventType.LEADER_CHANGE, Map.of(
                "tag", event.tag(),
                "old_leader", event.oldLeaderName(),
                "new_leader", event.newLeaderName()
        ));
    }

    @EventHandler
    public void onWarStart(ClanWarStartEvent event) {
        publisher.publish(DiscordEventType.WAR_START, Map.of(
                "attacker", event.attackerTag(),
                "defender", event.defenderTag()
        ));
    }

    @EventHandler
    public void onWarEnd(ClanWarEndEvent event) {
        if (event.hasWinner()) {
            publisher.publish(DiscordEventType.WAR_WIN, Map.of(
                    "winner", event.winnerTag(),
                    "loser", event.loserTag() == null ? "" : event.loserTag(),
                    "attacker", event.attackerTag(),
                    "defender", event.defenderTag()
            ));
            return;
        }
        publisher.publish(DiscordEventType.WAR_END, Map.of(
                "attacker", event.attackerTag(),
                "defender", event.defenderTag()
        ));
    }

    @EventHandler
    public void onQuestComplete(ClanQuestCompleteEvent event) {
        publisher.publish(DiscordEventType.QUEST_COMPLETE, Map.of(
                "tag", event.tag(),
                "quest", event.questId()
        ));
    }

    @EventHandler
    public void onGladiatorStart(GladiatorEventStartEvent event) {
        publisher.publish(DiscordEventType.GLAD_START, Map.of(
                "arena", event.arenaName()
        ));
    }

    @EventHandler
    public void onGladiatorWin(GladiatorEventWinEvent event) {
        publisher.publish(DiscordEventType.GLAD_WIN, Map.of(
                "arena", event.arenaName(),
                "winner", event.winnerTag(),
                "participants", String.valueOf(event.participantCount())
        ));
    }
}
