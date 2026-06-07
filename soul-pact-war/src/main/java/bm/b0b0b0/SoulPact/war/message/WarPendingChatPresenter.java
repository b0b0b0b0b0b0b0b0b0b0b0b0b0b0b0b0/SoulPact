package bm.b0b0b0.SoulPact.war.message;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.service.WarClanLookup;
import java.util.Map;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WarPendingChatPresenter {

    private final SoulPactApi api;
    private final WarConfig config;
    private final WarMessages messages;
    private final WarClanLookup clanLookup;

    public WarPendingChatPresenter(
            SoulPactApi api,
            WarConfig config,
            WarMessages messages,
            WarClanLookup clanLookup
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.clanLookup = clanLookup;
    }

    public void notifyDeclaration(WarDeclarationRecord declaration) {
        clanLookup.findClan(declaration.defenderClanId()).thenAccept(defenderOptional ->
                clanLookup.findClan(declaration.attackerClanId()).thenAccept(attackerOptional -> {
                    if (defenderOptional.isEmpty() || attackerOptional.isEmpty()) {
                        return;
                    }
                    ClanSnapshot defender = defenderOptional.get();
                    ClanSnapshot attacker = attackerOptional.get();
                    Map<String, String> placeholders = Map.of(
                            "attacker_tag", attacker.tag(),
                            "attacker_name", attacker.name(),
                            "attacker_id", String.valueOf(attacker.id())
                    );
                    Map<String, String> ransomPlaceholders = Map.of(
                            "ransom_percent", String.valueOf((int) (config.ransomPercent() * 100.0D))
                    );
                    forEachOnlineClanMember(defender.id(), (player, clan) -> {
                        if (clan.leaderId().equals(player.getUniqueId())) {
                            messages.send(player, "war.declare.received", placeholders);
                            Component accept = messages.component(player, "war.declare.button.accept")
                                    .clickEvent(ClickEvent.runCommand("/clanwar accept " + declaration.id()));
                            Component separator = messages.component(player, "war.declare.button.separator");
                            Component ransom = messages.component(player, "war.declare.button.ransom", ransomPlaceholders)
                                    .clickEvent(ClickEvent.runCommand("/clanwar ransom " + declaration.id()));
                            player.sendMessage(accept.append(separator).append(ransom));
                            return;
                        }
                        messages.send(player, "war.declare.received-member", placeholders);
                    });
                })
        );
    }

    private void forEachOnlineClanMember(long clanId, BiConsumer<Player, ClanSnapshot> action) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> {
                if (clanOptional.isEmpty() || clanOptional.get().id() != clanId) {
                    return;
                }
                api.scheduler().runSync(() -> action.accept(player, clanOptional.get()));
            });
        }
    }
}
