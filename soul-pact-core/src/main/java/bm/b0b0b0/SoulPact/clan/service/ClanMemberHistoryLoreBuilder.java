package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMembershipHistoryEntry;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanMemberHistoryLoreBuilder {

    private final MessageService messageService;

    public ClanMemberHistoryLoreBuilder(MessageService messageService) {
        this.messageService = messageService;
    }

    public List<String> build(Player player, List<ClanMembershipHistoryEntry> history) {
        List<String> lore = new ArrayList<>();
        lore.add(messageService.resolve(player, "clan.gui.members.detail.history-header"));
        if (history.isEmpty()) {
            lore.add(messageService.resolve(player, "clan.gui.members.detail.history-empty"));
            return lore;
        }
        for (ClanMembershipHistoryEntry entry : history) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", entry.clanTag());
            placeholders.put("reason", resolveReason(player, entry.reason()));
            lore.add(messageService.resolve(player, "clan.gui.members.detail.history-line", placeholders));
        }
        return lore;
    }

    private String resolveReason(Player player, String reason) {
        String key = switch (reason) {
            case "leave" -> "clan.gui.members.detail.reason.leave";
            case "kick" -> "clan.gui.members.detail.reason.kick";
            case "disband" -> "clan.gui.members.detail.reason.disband";
            default -> "clan.gui.members.detail.reason.other";
        };
        return messageService.resolve(player, key);
    }
}
