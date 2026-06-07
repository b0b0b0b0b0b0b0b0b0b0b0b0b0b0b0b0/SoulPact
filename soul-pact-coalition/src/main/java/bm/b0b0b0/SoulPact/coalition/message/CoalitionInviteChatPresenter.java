package bm.b0b0b0.SoulPact.coalition.message;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteRecord;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionClanLookup;
import java.util.Map;
import java.util.function.BiConsumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class CoalitionInviteChatPresenter {

    private final SoulPactApi api;
    private final CoalitionMessages messages;
    private final CoalitionClanLookup clanLookup;

    public CoalitionInviteChatPresenter(
            SoulPactApi api,
            CoalitionMessages messages,
            CoalitionClanLookup clanLookup
    ) {
        this.api = api;
        this.messages = messages;
        this.clanLookup = clanLookup;
    }

    public void notifyInvite(CoalitionInviteRecord invite) {
        clanLookup.findClan(invite.targetClanId()).thenAccept(targetOptional ->
                clanLookup.findClan(invite.inviterClanId()).thenAccept(inviterOptional -> {
                    if (targetOptional.isEmpty() || inviterOptional.isEmpty()) {
                        return;
                    }
                    ClanSnapshot inviter = inviterOptional.get();
                    Map<String, String> placeholders = Map.of(
                            "inviter_tag", inviter.tag(),
                            "inviter_name", inviter.name()
                    );
                    forEachOnlineClanMember(targetOptional.get().id(), (player, clan) -> {
                        if (clan.leaderId().equals(player.getUniqueId())) {
                            messages.send(player, "coalition.invite.received", placeholders);
                            Component accept = messages.component(player, "coalition.invite.button.accept")
                                    .clickEvent(ClickEvent.runCommand("/clancoalition accept " + invite.id()));
                            Component separator = messages.component(player, "coalition.invite.button.separator");
                            Component deny = messages.component(player, "coalition.invite.button.deny")
                                    .clickEvent(ClickEvent.runCommand("/clancoalition deny " + invite.id()));
                            Component block = messages.component(player, "coalition.invite.button.block")
                                    .clickEvent(ClickEvent.runCommand("/clancoalition block " + invite.id()));
                            player.sendMessage(accept.append(separator).append(deny).append(separator).append(block));
                            return;
                        }
                        messages.send(player, "coalition.invite.received-member", placeholders);
                    });
                })
        );
    }

    public void notifyInviterAccepted(CoalitionInviteRecord invite) {
        notifyInviterOutcome(
                invite,
                "coalition.invite.inviter-accepted",
                "coalition.invite.inviter-accepted-member"
        );
    }

    public void notifyInviterDenied(CoalitionInviteRecord invite) {
        notifyInviterOutcome(
                invite,
                "coalition.invite.inviter-denied",
                "coalition.invite.inviter-denied-member"
        );
    }

    public void notifyInviterBlocked(CoalitionInviteRecord invite) {
        notifyInviterOutcome(
                invite,
                "coalition.invite.inviter-blocked",
                "coalition.invite.inviter-blocked-member"
        );
    }

    private void notifyInviterOutcome(CoalitionInviteRecord invite, String leaderKey, String memberKey) {
        clanLookup.findClan(invite.inviterClanId()).thenAccept(inviterOptional ->
                clanLookup.findClan(invite.targetClanId()).thenAccept(targetOptional -> {
                    if (inviterOptional.isEmpty() || targetOptional.isEmpty()) {
                        return;
                    }
                    ClanSnapshot target = targetOptional.get();
                    Map<String, String> placeholders = Map.of(
                            "target_tag", target.tag(),
                            "target_name", target.name()
                    );
                    forEachOnlineClanMember(inviterOptional.get().id(), (player, clan) -> {
                        if (clan.leaderId().equals(player.getUniqueId())) {
                            messages.send(player, leaderKey, placeholders);
                            return;
                        }
                        messages.send(player, memberKey, placeholders);
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
