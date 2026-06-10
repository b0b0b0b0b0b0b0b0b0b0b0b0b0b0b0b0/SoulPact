package bm.b0b0b0.SoulPact.quests.service;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import java.util.List;
import java.util.Optional;

public record QuestsOverview(
        ClanSnapshot clan,
        List<QuestEntryView> entries,
        boolean canManage
) {

    public Optional<QuestEntryView> activeEntry() {
        return entries.stream()
                .filter(entry -> entry.state() == QuestEntryView.QuestEntryState.ACTIVE)
                .findFirst();
    }
}
