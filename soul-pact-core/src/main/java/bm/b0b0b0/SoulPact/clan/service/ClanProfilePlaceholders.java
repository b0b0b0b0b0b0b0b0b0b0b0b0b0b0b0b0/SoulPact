package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanProfilePlaceholders {

    private static final DateTimeFormatter CREATED_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ClanProfilePlaceholders() {
    }

    public static Map<String, String> forClan(Player player, Clan clan, int memberCount, MessageService messageService) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("tag", clan.tag());
        placeholders.put("id", String.valueOf(clan.id()));
        placeholders.put("name", clan.name());
        placeholders.put("points", String.valueOf(clan.points()));
        placeholders.put("wars_won", String.valueOf(clan.warsWon()));
        placeholders.put("count", String.valueOf(memberCount));
        placeholders.put("max", String.valueOf(clan.maxSlots()));
        placeholders.put("created", formatCreated(clan.createdAt()));
        placeholders.put("description", resolveDescription(player, clan.description(), messageService));
        placeholders.put("verified", resolveBoolean(player, clan.verified(), messageService));
        placeholders.put("ff", resolveBoolean(player, clan.friendlyFire(), messageService));
        placeholders.put("recruitment", resolveRecruitment(player, clan.joinRequestsOpen(), messageService));
        return placeholders;
    }

    private static String resolveRecruitment(Player player, boolean open, MessageService messageService) {
        String key = open ? "clan.gui.value-recruitment-open" : "clan.gui.value-recruitment-closed";
        return messageService.resolve(player, key);
    }

    private static String resolveBoolean(Player player, boolean value, MessageService messageService) {
        String key = value ? "clan.gui.profile.value-yes" : "clan.gui.profile.value-no";
        return messageService.resolve(player, key);
    }

    private static String resolveDescription(Player player, String description, MessageService messageService) {
        if (description == null || description.isBlank()) {
            return messageService.resolve(player, "clan.gui.profile.value-empty-description");
        }
        return description;
    }

    private static String formatCreated(long createdAt) {
        return Instant.ofEpochMilli(createdAt)
                .atZone(ZoneId.systemDefault())
                .format(CREATED_FORMAT);
    }
}
