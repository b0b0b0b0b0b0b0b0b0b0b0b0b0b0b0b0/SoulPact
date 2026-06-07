package bm.b0b0b0.SoulPact.clan.message;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class ClanInfoChatPresenter {

    private static final String BACK_COMMAND = "/clan";
    private static final DateTimeFormatter CREATED_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final MessageService messageService;

    public ClanInfoChatPresenter(MessageService messageService) {
        this.messageService = messageService;
    }

    public void show(Player player, Clan clan, int memberCount) {
        player.closeInventory();
        Map<String, String> placeholders = buildPlaceholders(player, clan, memberCount);
        messageService.send(player, "clan.info.header", placeholders);
        messageService.send(player, "clan.info.line-leader", placeholders);
        messageService.send(player, "clan.info.line-members", placeholders);
        messageService.send(player, "clan.info.line-wars-won", placeholders);
        messageService.send(player, "clan.info.line-verified", placeholders);
        messageService.send(player, "clan.info.line-ff", placeholders);
        messageService.send(player, "clan.info.line-created", placeholders);
        messageService.send(player, "clan.info.line-description", placeholders);
        messageService.sendRunLine(player, "clan.info.back.label", BACK_COMMAND);
    }

    private Map<String, String> buildPlaceholders(Player player, Clan clan, int memberCount) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("tag", clan.tag());
        placeholders.put("name", clan.name());
        placeholders.put("leader", resolveLeaderName(clan.leaderId()));
        placeholders.put("count", String.valueOf(memberCount));
        placeholders.put("max", String.valueOf(clan.maxSlots()));
        placeholders.put("wars_won", String.valueOf(clan.warsWon()));
        placeholders.put("points", String.valueOf(clan.points()));
        placeholders.put("verified", resolveBoolean(player, clan.verified()));
        placeholders.put("ff", resolveBoolean(player, clan.friendlyFire()));
        placeholders.put("created", formatCreated(clan.createdAt()));
        placeholders.put("description", resolveDescription(player, clan.description()));
        return placeholders;
    }

    private String resolveLeaderName(java.util.UUID leaderId) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(leaderId);
        String name = offlinePlayer.getName();
        return name == null || name.isBlank() ? leaderId.toString() : name;
    }

    private String resolveBoolean(Player player, boolean value) {
        String key = value ? "clan.info.value-yes" : "clan.info.value-no";
        return messageService.resolve(player, key);
    }

    private String resolveDescription(Player player, String description) {
        if (description == null || description.isBlank()) {
            return messageService.resolve(player, "clan.info.value-empty-description");
        }
        return description;
    }

    private String formatCreated(long createdAt) {
        return Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .format(CREATED_FORMAT);
    }
}
