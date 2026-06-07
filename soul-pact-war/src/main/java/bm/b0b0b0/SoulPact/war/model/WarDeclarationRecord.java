package bm.b0b0b0.SoulPact.war.model;

public record WarDeclarationRecord(
        long id,
        long attackerClanId,
        long defenderClanId,
        java.util.UUID declaredBy,
        long createdAt,
        String status
) {
}
