package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import java.util.HashSet;
import java.util.Set;

public final class WarLandCombatService {

    private final WarLandBridgeLookup landBridgeLookup;
    private final CoalitionWarBridgeLookup coalitionBridgeLookup;
    private final WarStateCache stateCache;

    public WarLandCombatService(
            WarLandBridgeLookup landBridgeLookup,
            CoalitionWarBridgeLookup coalitionBridgeLookup,
            WarStateCache stateCache
    ) {
        this.landBridgeLookup = landBridgeLookup;
        this.coalitionBridgeLookup = coalitionBridgeLookup;
        this.stateCache = stateCache;
    }

    public void enableForWar(ActiveWarRecord war) {
        landBridgeLookup.resolve().ifPresent(land -> {
            Set<Long> clanIds = collectCombatClans(war.attackerClanId(), war.defenderClanId());
            for (long clanId : clanIds) {
                land.applyWarCombatZone(clanId);
            }
            stateCache.rememberCombatZones(war.id(), clanIds);
        });
    }

    public void disableForWar(long warId) {
        restoreCombatForClans(stateCache.forgetCombatZones(warId));
    }

    public void restoreCombatForClans(Set<Long> clanIds) {
        if (clanIds.isEmpty()) {
            return;
        }
        landBridgeLookup.resolve().ifPresent(land -> {
            for (long clanId : clanIds) {
                land.restoreCombatZone(clanId);
            }
        });
    }

    private Set<Long> collectCombatClans(long attackerClanId, long defenderClanId) {
        Set<Long> clanIds = new HashSet<>();
        coalitionBridgeLookup.resolve().ifPresentOrElse(
                bridge -> {
                    clanIds.addAll(bridge.coalitionClanIds(attackerClanId));
                    clanIds.addAll(bridge.coalitionClanIds(defenderClanId));
                },
                () -> {
                    clanIds.add(attackerClanId);
                    clanIds.add(defenderClanId);
                }
        );
        return clanIds;
    }
}
