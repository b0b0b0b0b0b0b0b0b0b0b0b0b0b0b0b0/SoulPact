package bm.b0b0b0.SoulPact.coalition.message;

import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteRecord;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionClanLookup;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CoalitionInviteChatPresenter {

    private final CoalitionMessages messages;
    private final CoalitionClanLookup clanLookup;

    public CoalitionInviteChatPresenter(CoalitionMessages messages, CoalitionClanLookup clanLookup) {
        this.messages = messages;
        this.clanLookup = clanLookup;
    }

    public void notifyInvite(CoalitionInviteRecord invite) {
        clanLookup.findClan(invite.targetClanId()).thenAccept(targetOptional ->
                clanLookup.findClan(invite.inviterClanId()).thenAccept(inviterOptional -> {
                    if (targetOptional.isEmpty() || inviterOptional.isEmpty()) {
                        return;
                    }
                    var target = targetOptional.get();
                    var inviter = inviterOptional.get();
                    Player leader = Bukkit.getPlayer(target.leaderId());
                    if (leader == null || !leader.isOnline()) {
                        return;
                    }
                    messages.send(leader, "coalition.invite.received", Map.of(
                            "inviter_tag", inviter.tag(),
                            "inviter_name", inviter.name()
                    ));
                    Component accept = messages.component(leader, "coalition.invite.button.accept")
                            .clickEvent(ClickEvent.runCommand("/clancoalition accept " + invite.id()));
                    Component separator = messages.component(leader, "coalition.invite.button.separator");
                    Component deny = messages.component(leader, "coalition.invite.button.deny")
                            .clickEvent(ClickEvent.runCommand("/clancoalition deny " + invite.id()));
                    leader.sendMessage(accept.append(separator).append(deny));
                })
        );
    }
}
