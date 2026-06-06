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

public final class ClanMemberDetailLoreBuilder {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final MessageService messageService;
    private final ClanMemberHistoryLoreBuilder historyLoreBuilder;

    public ClanMemberDetailLoreBuilder(
            MessageService messageService,
            ClanMemberHistoryLoreBuilder historyLoreBuilder
    ) {
        this.messageService = messageService;
        this.historyLoreBuilder = historyLoreBuilder;
    }

    public List<String> build(Player viewer, ClanMemberDetailSnapshot snapshot) {
        List<String> lore = new ArrayList<>();
        Map<String, String> infoPlaceholders = new HashMap<>();
        infoPlaceholders.put("role", snapshot.roleTitle());
        infoPlaceholders.put("joined", formatDate(snapshot.member().joinedAt()));
        infoPlaceholders.put("kills", String.valueOf(snapshot.member().kills()));
        infoPlaceholders.put("deaths", String.valueOf(snapshot.member().deaths()));
        lore.add(messageService.resolve(viewer, "clan.gui.members.detail.line-role", infoPlaceholders));
        lore.add(messageService.resolve(viewer, "clan.gui.members.detail.line-joined", infoPlaceholders));
        lore.add(messageService.resolve(viewer, "clan.gui.members.detail.line-stats", infoPlaceholders));
        lore.add("");
        lore.add(messageService.resolve(viewer, "clan.gui.members.detail.achievements-header"));
        lore.add(messageService.resolve(viewer, "clan.gui.members.detail.achievements-empty"));
        lore.add("");
        lore.addAll(historyLoreBuilder.build(viewer, snapshot.history()));
        return lore;
    }

    private static String formatDate(long timestamp) {
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(DATE_FORMAT);
    }
}
