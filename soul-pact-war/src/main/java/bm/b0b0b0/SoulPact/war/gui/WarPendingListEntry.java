package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;

public record WarPendingListEntry(
        WarDeclarationRecord declaration,
        String attackerTag,
        String attackerName
) {
}
