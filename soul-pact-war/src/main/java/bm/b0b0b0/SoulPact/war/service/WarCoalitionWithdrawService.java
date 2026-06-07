package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.repository.WarRepository;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WarCoalitionWithdrawService {

    private final SoulPactApi api;
    private final WarMessages messages;
    private final WarRepository repository;
    private final WarStateCache stateCache;
    private final WarBossBarService bossBarService;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;
    private final WarLandCombatService landCombatService;

    public WarCoalitionWithdrawService(
            SoulPactApi api,
            WarMessages messages,
            WarRepository repository,
            WarStateCache stateCache,
            WarBossBarService bossBarService,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup,
            WarLandCombatService landCombatService
    ) {
        this.api = api;
        this.messages = messages;
        this.repository = repository;
        this.stateCache = stateCache;
        this.bossBarService = bossBarService;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
        this.landCombatService = landCombatService;
    }

    public void withdrawFromOwnFlagBreak(ActiveWarRecord war, long triggerClanId) {
        withdrawCoalitionSide(war, triggerClanId, "war.coalition-withdraw.self-flag");
    }

    public void withdrawFromCapture(ActiveWarRecord war, long capturedClanId) {
        withdrawCoalitionSide(war, capturedClanId, "war.coalition-withdraw.flag-captured");
    }

    private void withdrawCoalitionSide(ActiveWarRecord war, long triggerClanId, String broadcastKey) {
        Set<Long> withdrawnClans = collectWithdrawnClans(war, triggerClanId);
        if (withdrawnClans.isEmpty()) {
            return;
        }
        landCombatService.restoreCombatForClans(withdrawnClans);
        stateCache.removeCombatClansFromWar(war.id(), withdrawnClans);
        stateCache.clearCapture(war.id());
        api.scheduler().runAsync(() -> repository.clearCapture(war.id()));
        coalitionWarBridgeLookup.resolve().ifPresent(bridge ->
                bridge.onCoalitionWithdrawnFromWar(
                        war.id(),
                        war.attackerClanId(),
                        war.defenderClanId(),
                        triggerClanId
                )
        );
        api.scheduler().runSync(() -> {
            bossBarService.refreshWarClans(war);
            broadcastWithdraw(broadcastKey, String.valueOf(triggerClanId));
        });
    }

    private Set<Long> collectWithdrawnClans(ActiveWarRecord war, long triggerClanId) {
        Set<Long> coalitionMembers = coalitionWarBridgeLookup.resolve()
                .map(bridge -> bridge.coalitionClanIds(triggerClanId))
                .orElse(Set.of(triggerClanId));
        Set<Long> withdrawn = new HashSet<>();
        for (long clanId : coalitionMembers) {
            if (clanId != war.attackerClanId() && clanId != war.defenderClanId()) {
                withdrawn.add(clanId);
            }
        }
        return withdrawn;
    }

    private void broadcastWithdraw(String messageKey, String triggerClanId) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            messages.send(online, messageKey, Map.of("clan", triggerClanId));
        }
    }
}
