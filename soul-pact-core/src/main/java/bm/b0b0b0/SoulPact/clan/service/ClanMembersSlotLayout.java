package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.role.RoleTheme;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import bm.b0b0b0.SoulPact.core.config.GuiMembersConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ClanMembersSlotLayout {

    private final RoleThemeService roleThemeService;

    public ClanMembersSlotLayout(RoleThemeService roleThemeService) {
        this.roleThemeService = roleThemeService;
    }

    public ClanMembersPage assignPage(GuiMembersConfig config, List<ClanMember> members, int page) {
        List<ClanMembersLayoutEntry> sequence = buildSequence(members);
        int contentSize = config.contentSize();
        int totalPages = resolveTotalPages(contentSize, sequence.size());
        int safePage = Math.max(0, Math.min(page, totalPages - 1));
        int start = safePage * contentSize;
        int end = Math.min(start + contentSize, sequence.size());
        Map<Integer, ClanMember> slotMembers = new HashMap<>();
        int slotIndex = 0;
        for (int index = start; index < end; index++) {
            ClanMembersLayoutEntry entry = sequence.get(index);
            if (entry.isGap()) {
                slotIndex++;
                continue;
            }
            slotMembers.put(config.contentSlot(slotIndex), entry.member());
            slotIndex++;
        }
        return new ClanMembersPage(slotMembers, safePage, totalPages, members.size());
    }

    private List<ClanMembersLayoutEntry> buildSequence(List<ClanMember> members) {
        List<ClanMembersLayoutEntry> sequence = new ArrayList<>();
        if (members.isEmpty()) {
            return sequence;
        }
        Map<String, List<ClanMember>> grouped = groupMembers(members);
        boolean firstGroup = true;
        RoleTheme theme = roleThemeService.theme();
        for (String roleKey : theme.order()) {
            List<ClanMember> roleMembers = grouped.remove(roleKey);
            if (roleMembers == null || roleMembers.isEmpty()) {
                continue;
            }
            appendGroup(sequence, roleMembers, firstGroup);
            firstGroup = false;
        }
        List<ClanMember> remaining = grouped.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(ClanMember::joinedAt))
                .toList();
        if (!remaining.isEmpty()) {
            appendGroup(sequence, remaining, firstGroup);
        }
        return sequence;
    }

    private static void appendGroup(
            List<ClanMembersLayoutEntry> sequence,
            List<ClanMember> roleMembers,
            boolean firstGroup
    ) {
        if (!firstGroup) {
            sequence.add(ClanMembersLayoutEntry.gap());
        }
        for (ClanMember member : roleMembers) {
            sequence.add(ClanMembersLayoutEntry.member(member));
        }
    }

    private static int resolveTotalPages(int contentSize, int sequenceSize) {
        if (contentSize <= 0) {
            return 1;
        }
        if (sequenceSize == 0) {
            return 1;
        }
        return (sequenceSize + contentSize - 1) / contentSize;
    }

    private static Map<String, List<ClanMember>> groupMembers(List<ClanMember> members) {
        Map<String, List<ClanMember>> grouped = members.stream()
                .collect(Collectors.groupingBy(ClanMember::role, LinkedHashMap::new, Collectors.toList()));
        grouped.values().forEach(list -> list.sort(Comparator.comparing(ClanMember::joinedAt)));
        return grouped;
    }
}
