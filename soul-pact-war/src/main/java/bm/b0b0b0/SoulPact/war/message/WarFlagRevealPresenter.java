package bm.b0b0b0.SoulPact.war.message;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarFlagSnapshot;
import bm.b0b0b0.SoulPact.war.service.CoalitionWarBridgeLookup;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WarFlagRevealPresenter {

    private final SoulPactApi api;
    private final WarMessages messages;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;

    public WarFlagRevealPresenter(
            SoulPactApi api,
            WarMessages messages,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup
    ) {
        this.api = api;
        this.messages = messages;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
    }

    public void revealStartedWar(ActiveWarRecord war, String attackerTag, String defenderTag) {
        revealToClan(war.attackerClanId(), defenderTag, war.defenderFlag());
        revealToClan(war.defenderClanId(), attackerTag, war.attackerFlag());
        coalitionWarBridgeLookup.resolve().ifPresent(bridge -> {
            war.defenderFlag().ifPresent(flag -> bridge.onWarEnemyBaseRevealed(
                    war.attackerClanId(),
                    defenderTag,
                    flag.world(),
                    flag.x(),
                    flag.y(),
                    flag.z()
            ));
            war.attackerFlag().ifPresent(flag -> bridge.onWarEnemyBaseRevealed(
                    war.defenderClanId(),
                    attackerTag,
                    flag.world(),
                    flag.x(),
                    flag.y(),
                    flag.z()
            ));
        });
    }

    private void revealToClan(long clanId, String enemyTag, Optional<WarFlagSnapshot> flagOptional) {
        if (flagOptional.isEmpty()) {
            return;
        }
        WarFlagSnapshot flag = flagOptional.get();
        Map<String, String> placeholders = Map.of(
                "enemy_tag", enemyTag,
                "world", flag.world(),
                "x", String.valueOf(flag.x()),
                "y", String.valueOf(flag.y()),
                "z", String.valueOf(flag.z())
        );
        for (Player online : Bukkit.getOnlinePlayers()) {
            api.findClanByPlayer(online.getUniqueId()).thenAccept(clanOptional -> {
                if (clanOptional.isEmpty() || clanOptional.get().id() != clanId) {
                    return;
                }
                api.scheduler().runSync(() -> messages.send(online, "war.started.enemy-flag", placeholders));
            });
        }
    }
}
