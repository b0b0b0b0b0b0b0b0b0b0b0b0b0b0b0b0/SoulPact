package bm.b0b0b0.SoulPact.coalition.gui;

import bm.b0b0b0.SoulPact.coalition.message.CoalitionMessages;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionService;
import java.util.Map;
import org.bukkit.entity.Player;

public final class CoalitionGuiService {

    private final CoalitionMessages messages;
    private final CoalitionService coalitionService;

    public CoalitionGuiService(CoalitionMessages messages, CoalitionService coalitionService) {
        this.messages = messages;
        this.coalitionService = coalitionService;
    }

    public void open(Player player) {
        coalitionService.listMembersForLeader(player).thenAccept(members -> {
            if (members.isEmpty()) {
                return;
            }
            messages.send(player, "coalition.gui.hub.header", Map.of("count", String.valueOf(members.size())));
            for (int index = 0; index < members.size(); index++) {
                var member = members.get(index);
                messages.send(player, "coalition.gui.hub.member-line", Map.of(
                        "index", String.valueOf(index + 1),
                        "tag", member.tag(),
                        "name", member.name()
                ));
            }
            messages.send(player, "coalition.gui.hub.footer");
        });
    }
}
