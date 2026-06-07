package bm.b0b0b0.SoulPact.coalition.bridge;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionTreasuryDistribution;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge;
import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarOutcome;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionMessages;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionBossBarService;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionClanLookup;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionMembershipCache;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionService;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionTreasuryDistributor;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionWarStateTracker;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CoalitionWarBridgeImpl implements CoalitionWarBridge {

    private final SoulPactApi api;
    private final CoalitionMessages messages;
    private final CoalitionService coalitionService;
    private final CoalitionMembershipCache membershipCache;
    private final CoalitionWarStateTracker warStateTracker;
    private final CoalitionBossBarService bossBarService;
    private final CoalitionClanLookup clanLookup;
    private final CoalitionTreasuryDistributor treasuryDistributor;

    public CoalitionWarBridgeImpl(
            SoulPactApi api,
            CoalitionMessages messages,
            CoalitionService coalitionService,
            CoalitionMembershipCache membershipCache,
            CoalitionWarStateTracker warStateTracker,
            CoalitionBossBarService bossBarService,
            CoalitionClanLookup clanLookup,
            CoalitionTreasuryDistributor treasuryDistributor
    ) {
        this.api = api;
        this.messages = messages;
        this.coalitionService = coalitionService;
        this.membershipCache = membershipCache;
        this.warStateTracker = warStateTracker;
        this.bossBarService = bossBarService;
        this.clanLookup = clanLookup;
        this.treasuryDistributor = treasuryDistributor;
    }

    @Override
    public CompletableFuture<Boolean> canDeclareWar(long attackerClanId, long defenderClanId) {
        return coalitionService.canDeclareWar(attackerClanId, defenderClanId);
    }

    @Override
    public boolean allowsAllyFlagBreak(
            long breakerClanId,
            long baseOwnerClanId,
            long warAttackerClanId,
            long warDefenderClanId
    ) {
        return coalitionService.allowsAllyFlagBreak(
                breakerClanId,
                baseOwnerClanId,
                warAttackerClanId,
                warDefenderClanId
        );
    }

    @Override
    public void onWarDeclared(long attackerClanId, long defenderClanId) {
        applyAllyPhase(defenderClanId, attackerClanId, CoalitionWarStateTracker.Phase.DECLARED, 0L);
        notifyCoalitionAllies(defenderClanId, "coalition.war.declared-on-ally", attackerClanId);
    }

    @Override
    public void onWarStarted(long attackerClanId, long defenderClanId) {
        applyAllyPhase(defenderClanId, attackerClanId, CoalitionWarStateTracker.Phase.ACTIVE, 0L);
        applyAllyPhase(attackerClanId, defenderClanId, CoalitionWarStateTracker.Phase.ACTIVE, 0L);
        notifyCoalitionAllies(defenderClanId, "coalition.war.started-for-ally", defenderClanId);
        notifyCoalitionAllies(attackerClanId, "coalition.war.started-for-ally", attackerClanId);
    }

    @Override
    public void onCaptureStarted(long targetClanId, long holderClanId, long deadlineAt) {
        applyAllyPhase(targetClanId, holderClanId, CoalitionWarStateTracker.Phase.CAPTURE, deadlineAt);
        applyAllyPhase(holderClanId, targetClanId, CoalitionWarStateTracker.Phase.CAPTURE, deadlineAt);
        bossBarService.refreshCoalition(holderClanId);
        bossBarService.refreshCoalition(targetClanId);
    }

    @Override
    public CompletableFuture<CoalitionTreasuryDistribution> distributeVictoryTreasury(CoalitionWarOutcome outcome) {
        return treasuryDistributor.distribute(outcome);
    }

    @Override
    public void onWarEnded(CoalitionWarOutcome outcome, boolean disbanded) {
        warStateTracker.clearForClan(outcome.attackerClanId());
        warStateTracker.clearForClan(outcome.defenderClanId());
        clanLookup.findClan(outcome.winnerClanId()).thenCombine(
                clanLookup.findClan(outcome.loserClanId()),
                (winnerOptional, loserOptional) -> {
                    String winnerTag = winnerOptional.map(clan -> clan.tag())
                            .orElse(String.valueOf(outcome.winnerClanId()));
                    String loserTag = loserOptional.map(clan -> clan.tag())
                            .orElse(String.valueOf(outcome.loserClanId()));
                    api.scheduler().runSync(() -> broadcastWarEnded(outcome, winnerTag, loserTag));
                    return null;
                }
        );
        membershipCache.removeClan(outcome.loserClanId());
        bossBarService.refreshCoalition(outcome.winnerClanId());
        bossBarService.refreshCoalition(outcome.loserClanId());
    }

    private void broadcastWarEnded(CoalitionWarOutcome outcome, String winnerTag, String loserTag) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            api.findClanByPlayer(online.getUniqueId()).thenAccept(viewerClanOptional -> {
                if (viewerClanOptional.isEmpty()) {
                    return;
                }
                long viewerClanId = viewerClanOptional.get().id();
                if (viewerClanId == outcome.winnerClanId() || viewerClanId == outcome.loserClanId()) {
                    return;
                }
                if (membershipCache.sharesCoalition(viewerClanId, outcome.winnerClanId())) {
                    api.scheduler().runSync(() -> messages.send(online, "coalition.war.ally-victory", Map.of(
                            "winner_tag", winnerTag,
                            "loser_tag", loserTag
                    )));
                } else if (membershipCache.sharesCoalition(viewerClanId, outcome.loserClanId())) {
                    api.scheduler().runSync(() -> messages.send(online, "coalition.war.ally-defeat", Map.of(
                            "loser_tag", loserTag
                    )));
                }
            });
        }
    }

    private void applyAllyPhase(long friendClanId, long enemyClanId, CoalitionWarStateTracker.Phase phase, long deadlineAt) {
        clanLookup.findClan(friendClanId).thenCombine(clanLookup.findClan(enemyClanId), (friendOptional, enemyOptional) -> {
            if (friendOptional.isEmpty() || enemyOptional.isEmpty()) {
                return false;
            }
            warStateTracker.setView(friendClanId, new CoalitionWarStateTracker.AllyWarView(
                    friendClanId,
                    friendOptional.get().tag(),
                    enemyClanId,
                    enemyOptional.get().tag(),
                    phase,
                    deadlineAt
            ));
            bossBarService.refreshCoalition(friendClanId);
            return true;
        });
    }

    private void notifyCoalitionAllies(long friendClanId, String messageKey, long subjectClanId) {
        clanLookup.findClan(subjectClanId).thenAccept(subjectOptional -> {
            if (subjectOptional.isEmpty()) {
                return;
            }
            String friendTag = subjectOptional.get().tag();
            for (long allyClanId : membershipCache.otherMembers(friendClanId)) {
                clanLookup.findClan(allyClanId).thenAccept(allyOptional -> allyOptional.ifPresent(ally -> {
                    Player leader = Bukkit.getPlayer(ally.leaderId());
                    if (leader != null && leader.isOnline()) {
                        messages.send(leader, messageKey, Map.of("friend_tag", friendTag));
                    }
                }));
            }
        });
    }
}
