package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.clan.role.RoleDefinition;
import bm.b0b0b0.SoulPact.clan.role.RoleTheme;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.config.PlaceholderConfig;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanPlaceholderSnapshotFactory {

    private final PlaceholderConfig placeholderConfig;
    private final RoleThemeService roleThemeService;

    public ClanPlaceholderSnapshotFactory(
            PlaceholderConfig placeholderConfig,
            RoleThemeService roleThemeService
    ) {
        this.placeholderConfig = placeholderConfig;
        this.roleThemeService = roleThemeService;
    }

    public ClanPlaceholderSnapshot build(
            UUID playerId,
            ClanPlaceholderClanBundle bundle,
            ClanPlaceholderMembershipRow membership
    ) {
        ClanPlaceholderMemberRow self = findMember(bundle.members(), playerId);
        int clanKills = 0;
        int clanDeaths = 0;
        List<String> memberNames = new ArrayList<>();
        List<UUID> memberPlayerIds = new ArrayList<>();
        Set<UUID> memberIds = new HashSet<>();
        for (ClanPlaceholderMemberRow member : bundle.members()) {
            clanKills += member.kills();
            clanDeaths += member.deaths();
            memberNames.add(PlaceholderTextUtil.resolvePlayerName(member.playerId()));
            memberPlayerIds.add(member.playerId());
            memberIds.add(member.playerId());
        }
        List<String> onlineNames = collectOnlineNames(memberIds);
        RoleTheme theme = roleThemeService.theme();
        String memberRole = self == null ? membership.role() : self.role();
        int roleRank = roleRank(theme, memberRole);
        int statsJoined = bundle.historyStats().getOrDefault("join", bundle.members().size());
        int statsLeave = bundle.historyStats().getOrDefault("leave", 0);
        int statsKick = bundle.historyStats().getOrDefault("kick", 0);
        ClanPlaceholderComputed computed = compute(bundle, memberRole, clanKills, clanDeaths, memberNames, onlineNames);
        return new ClanPlaceholderSnapshot(
                true,
                bundle.id(),
                bundle.tag(),
                bundle.name(),
                bundle.description(),
                bundle.leaderId(),
                bundle.leaderName(),
                bundle.points(),
                bundle.warsWon(),
                bundle.warsLost(),
                bundle.maxSlots(),
                bundle.members().size(),
                bundle.verified(),
                bundle.friendlyFire(),
                bundle.joinOpen(),
                bundle.createdAt(),
                memberRole,
                self == null ? membership.kills() : self.kills(),
                self == null ? membership.deaths() : self.deaths(),
                clanKills,
                clanDeaths,
                onlineNames.size(),
                memberNames,
                onlineNames,
                memberPlayerIds,
                bundle.allyTags(),
                bundle.bankBalance(),
                statsJoined,
                statsLeave,
                statsKick,
                bundle.bannerData(),
                roleRank,
                computed
        );
    }

    public ClanPlaceholderSnapshot refreshOnlinePresence(ClanPlaceholderSnapshot snapshot) {
        if (!snapshot.hasClan()) {
            return snapshot;
        }
        Set<UUID> memberIds = new HashSet<>(snapshot.memberPlayerIds());
        List<String> onlineNames = collectOnlineNames(memberIds);
        String onlineMembersLine = String.join(placeholderConfig.membersSeparator(), onlineNames);
        return snapshot.withOnlinePresence(onlineNames.size(), onlineNames, onlineMembersLine);
    }

    private static List<String> collectOnlineNames(Set<UUID> memberIds) {
        List<String> onlineNames = new ArrayList<>();
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (memberIds.contains(online.getUniqueId())) {
                onlineNames.add(online.getName());
            }
        }
        return onlineNames;
    }

    private ClanPlaceholderComputed compute(
            ClanPlaceholderClanBundle bundle,
            String memberRole,
            int clanKills,
            int clanDeaths,
            List<String> memberNames,
            List<String> onlineNames
    ) {
        String tagNoColor = PlaceholderTextUtil.stripColors(bundle.tag());
        String tagFormatted = PlaceholderTextUtil.applyTemplate(
                placeholderConfig.tagFormated(),
                Map.of("tag", bundle.tag())
        );
        String tagFormattedNoColor = PlaceholderTextUtil.applyTemplate(
                placeholderConfig.tagFormatedNocolor(),
                Map.of("tag", tagNoColor)
        );
        String hasClanFormatted = PlaceholderTextUtil.applyTemplate(
                placeholderConfig.hasClanFormated(),
                Map.of("tag", tagNoColor, "name", PlaceholderTextUtil.stripColors(bundle.name()))
        );
        String creationDate = PlaceholderTextUtil.formatDate(bundle.createdAt());
        int level = PlaceholderTextUtil.clanLevel(
                bundle.points(),
                placeholderConfig.pointsPerLevel(),
                placeholderConfig.maxClanLevel()
        );
        return new ClanPlaceholderComputed(
                hasClanFormatted,
                withLevelSymbol(hasClanFormatted, placeholderConfig.levelUpSymbol()),
                withLevelSymbol(hasClanFormatted, placeholderConfig.levelDownSymbol()),
                tagNoColor,
                tagFormatted,
                tagFormattedNoColor,
                withLevelSymbol(tagFormatted, placeholderConfig.levelUpSymbol()),
                withLevelSymbol(tagFormatted, placeholderConfig.levelDownSymbol()),
                PlaceholderTextUtil.stripColors(bundle.description()),
                PlaceholderTextUtil.applyTemplate(
                        placeholderConfig.leaderFormated(),
                        Map.of("leader", bundle.leaderName())
                ),
                creationDate,
                PlaceholderTextUtil.applyTemplate(
                        placeholderConfig.creationDateFormated(),
                        Map.of("date", creationDate)
                ),
                String.join(placeholderConfig.membersSeparator(), memberNames),
                String.join(placeholderConfig.membersSeparator(), onlineNames),
                String.join(placeholderConfig.alliesSeparator(), bundle.allyTags()),
                PlaceholderTextUtil.applyTemplate(
                        placeholderConfig.verifiedTagFormated(),
                        Map.of("tag", tagNoColor)
                ),
                PlaceholderTextUtil.formatMoney(bundle.bankBalance()),
                patentFormatted(memberRole),
                patentName(memberRole),
                PlaceholderTextUtil.formatKdr(clanKills, clanDeaths),
                String.valueOf(level),
                level >= placeholderConfig.maxClanLevel() ? "true" : "false",
                String.valueOf(PlaceholderTextUtil.pointsToNextLevel(
                        bundle.points(),
                        placeholderConfig.pointsPerLevel(),
                        placeholderConfig.maxClanLevel()
                )),
                bundle.friendlyFire() ? placeholderConfig.booleanYes() : placeholderConfig.booleanNo(),
                bundle.joinOpen() ? placeholderConfig.booleanYes() : placeholderConfig.booleanNo()
        );
    }

    private static ClanPlaceholderMemberRow findMember(List<ClanPlaceholderMemberRow> members, UUID playerId) {
        for (ClanPlaceholderMemberRow member : members) {
            if (member.playerId().equals(playerId)) {
                return member;
            }
        }
        return null;
    }

    private static int roleRank(RoleTheme theme, String roleKey) {
        List<String> order = theme.order();
        for (int index = 0; index < order.size(); index++) {
            if (order.get(index).equals(roleKey)) {
                return index + 1;
            }
        }
        return order.size();
    }

    private String patentName(String roleKey) {
        RoleDefinition definition = roleThemeService.theme().definition(roleKey);
        if (definition == null) {
            return roleKey;
        }
        return PlaceholderTextUtil.stripColors(definition.title());
    }

    private String patentFormatted(String roleKey) {
        RoleDefinition definition = roleThemeService.theme().definition(roleKey);
        if (definition == null) {
            return roleKey;
        }
        return definition.title();
    }

    private static String withLevelSymbol(String value, String symbol) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return symbol + value;
    }
}
