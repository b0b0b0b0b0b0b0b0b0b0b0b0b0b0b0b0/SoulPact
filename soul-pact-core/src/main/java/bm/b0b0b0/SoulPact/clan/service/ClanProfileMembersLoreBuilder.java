package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleTheme;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class ClanProfileMembersLoreBuilder {

    private final MessageService messageService;
    private final RoleThemeService roleThemeService;

    public ClanProfileMembersLoreBuilder(MessageService messageService, RoleThemeService roleThemeService) {
        this.messageService = messageService;
        this.roleThemeService = roleThemeService;
    }

    public List<String> build(Player player, List<ClanMember> members, int maxSlots) {
        List<String> lines = new ArrayList<>();
        Map<String, String> totalPlaceholders = Map.of(
                "count", String.valueOf(members.size()),
                "max", String.valueOf(maxSlots)
        );
        lines.add(messageService.resolve(player, "clan.gui.profile.item.members.lore-total", totalPlaceholders));
        lines.add("");
        Map<String, List<ClanMember>> grouped = members.stream()
                .collect(Collectors.groupingBy(ClanMember::role, HashMap::new, Collectors.toList()));
        RoleTheme theme = roleThemeService.theme();
        for (String roleKey : theme.order()) {
            RoleDefinition definition = theme.definition(roleKey);
            if (definition == null) {
                continue;
            }
            List<ClanMember> roleMembers = grouped.getOrDefault(roleKey, List.of());
            if (roleMembers.isEmpty()) {
                continue;
            }
            lines.add(buildRoleLine(player, definition, roleMembers));
        }
        appendUnknownRoles(player, lines, grouped, theme);
        lines.add("");
        lines.add(messageService.resolve(player, "clan.gui.profile.item.members.lore-hint"));
        return lines;
    }

    private void appendUnknownRoles(
            Player player,
            List<String> lines,
            Map<String, List<ClanMember>> grouped,
            RoleTheme theme
    ) {
        for (Map.Entry<String, List<ClanMember>> entry : grouped.entrySet()) {
            if (theme.definition(entry.getKey()) != null) {
                continue;
            }
            if (entry.getValue().isEmpty()) {
                continue;
            }
            RoleDefinition fallback = new RoleDefinition(entry.getKey(), entry.getKey(), true);
            lines.add(buildRoleLine(player, fallback, entry.getValue()));
        }
    }

    private String buildRoleLine(Player player, RoleDefinition definition, List<ClanMember> roleMembers) {
        if (roleMembers.isEmpty()) {
            return "";
        }
        if (definition.listNames()) {
            String names = roleMembers.stream()
                    .map(this::resolveDisplayName)
                    .collect(Collectors.joining(", "));
            Map<String, String> placeholders = Map.of(
                    "role", definition.title(),
                    "names", names
            );
            return messageService.resolve(player, "clan.gui.profile.item.members.lore-role-named", placeholders);
        }
        Map<String, String> placeholders = Map.of(
                "role", definition.title(),
                "count", String.valueOf(roleMembers.size())
        );
        return messageService.resolve(player, "clan.gui.profile.item.members.lore-role-count", placeholders);
    }

    private String resolveDisplayName(ClanMember member) {
        if (member.nickname() != null && !member.nickname().isBlank()) {
            return member.nickname();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member.playerId());
        String name = offlinePlayer.getName();
        return name == null || name.isBlank() ? member.playerId().toString() : name;
    }
}
