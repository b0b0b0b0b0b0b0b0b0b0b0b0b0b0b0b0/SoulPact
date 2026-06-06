package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMembershipHistoryEntry;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanRequestHistoryLoreBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final MessageService messageService;

    public ClanRequestHistoryLoreBuilder(MessageService messageService) {
        this.messageService = messageService;
    }

    public List<String> build(Player player, List<ClanMembershipHistoryEntry> history) {
        List<String> lore = new ArrayList<>();
        lore.add(messageService.resolve(player, "clan.gui.requests.detail.history-header"));
        if (history.isEmpty()) {
            lore.add(messageService.resolve(player, "clan.gui.requests.detail.history-empty"));
            return lore;
        }
        for (ClanMembershipHistoryEntry entry : history) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("tag", entry.clanTag());
            placeholders.put("name", entry.clanName());
            placeholders.put("role", entry.role());
            placeholders.put("left", formatDate(entry.leftAt()));
            placeholders.put("reason", resolveReason(player, entry.reason()));
            lore.add(messageService.resolve(player, "clan.gui.requests.detail.history-line", placeholders));
        }
        return lore;
    }

    private String resolveReason(Player player, String reason) {
        String key = switch (reason) {
            case "leave" -> "clan.gui.requests.detail.reason.leave";
            case "kick" -> "clan.gui.requests.detail.reason.kick";
            case "disband" -> "clan.gui.requests.detail.reason.disband";
            default -> "clan.gui.requests.detail.reason.other";
        };
        return messageService.resolve(player, key);
    }

    private static String formatDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DATE_FORMAT);
    }
}
