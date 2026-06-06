package bm.b0b0b0.SoulPact.clan.message;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanInvite;
import bm.b0b0b0.SoulPact.clan.model.ClanJoinRequest;
import bm.b0b0b0.SoulPact.clan.service.ClanPlayerNames;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;

public final class ClanPendingChatPresenter {

    private final MessageService messageService;

    public ClanPendingChatPresenter(MessageService messageService) {
        this.messageService = messageService;
    }

    public void showInvite(Player player, ClanInvite invite, Clan clan, String inviterName) {
        messageService.send(player, "clan.invite.received", Map.of(
                "inviter", inviterName,
                "tag", clan.tag(),
                "name", clan.name(),
                "id", String.valueOf(clan.id())
        ));
        Component accept = messageService.component(player, "clan.invite.button.accept")
                .clickEvent(ClickEvent.runCommand("/clan invite accept " + invite.id()));
        Component separator = messageService.component(player, "clan.pending.button-separator");
        Component deny = messageService.component(player, "clan.invite.button.deny")
                .clickEvent(ClickEvent.runCommand("/clan invite deny " + invite.id()));
        player.sendMessage(accept.append(separator).append(deny));
    }

    public void showJoinRequest(Player leader, ClanJoinRequest request, Clan clan, String playerName) {
        messageService.send(leader, "clan.request.received", Map.of(
                "player", playerName,
                "tag", clan.tag(),
                "name", clan.name(),
                "id", String.valueOf(clan.id())
        ));
        Component accept = messageService.component(leader, "clan.request.button.accept")
                .clickEvent(ClickEvent.runCommand("/clan request accept " + request.id()));
        Component separator = messageService.component(leader, "clan.pending.button-separator");
        Component deny = messageService.component(leader, "clan.request.button.deny")
                .clickEvent(ClickEvent.runCommand("/clan request deny " + request.id()));
        Component block = messageService.component(leader, "clan.request.button.block")
                .clickEvent(ClickEvent.runCommand("/clan request block " + request.id()));
        leader.sendMessage(accept.append(separator).append(deny).append(separator).append(block));
    }

    public void showRequestAccepted(Player player, Clan clan) {
        messageService.send(player, "clan.request.accepted-player", Map.of(
                "tag", clan.tag(),
                "name", clan.name()
        ));
        Component openClan = messageService.component(player, "clan.request.button.open-clan")
                .clickEvent(ClickEvent.runCommand("/clan profile"));
        player.sendMessage(openClan);
    }
}
