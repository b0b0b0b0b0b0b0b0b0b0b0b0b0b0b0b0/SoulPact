package bm.b0b0b0.SoulPact.war.message;

import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.service.WarClanLookup;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WarPendingChatPresenter {

    private final WarMessages messages;
    private final WarClanLookup clanLookup;

    public WarPendingChatPresenter(WarMessages messages, WarClanLookup clanLookup) {
        this.messages = messages;
        this.clanLookup = clanLookup;
    }

    public void notifyDeclaration(WarDeclarationRecord declaration) {
        clanLookup.findClan(declaration.defenderClanId()).thenAccept(defenderOptional ->
                clanLookup.findClan(declaration.attackerClanId()).thenAccept(attackerOptional -> {
                    if (defenderOptional.isEmpty() || attackerOptional.isEmpty()) {
                        return;
                    }
                    var defender = defenderOptional.get();
                    var attacker = attackerOptional.get();
                    Player leader = Bukkit.getPlayer(defender.leaderId());
                    if (leader == null || !leader.isOnline()) {
                        return;
                    }
                    messages.send(leader, "war.declare.received", Map.of(
                            "attacker_tag", attacker.tag(),
                            "attacker_name", attacker.name(),
                            "attacker_id", String.valueOf(attacker.id())
                    ));
                    Component accept = messages.component(leader, "war.declare.button.accept")
                            .clickEvent(ClickEvent.runCommand("/clanwar accept " + declaration.id()));
                    Component separator = messages.component(leader, "war.declare.button.separator");
                    Component ransom = messages.component(leader, "war.declare.button.ransom")
                            .clickEvent(ClickEvent.runCommand("/clanwar ransom " + declaration.id()));
                    leader.sendMessage(accept.append(separator).append(ransom));
                })
        );
    }
}
