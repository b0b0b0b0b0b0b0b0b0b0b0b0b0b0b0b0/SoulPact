package bm.b0b0b0.SoulPact.war.model;

public record ActiveWarRecord(
        long id,
        long attackerClanId,
        long defenderClanId,
        long startedAt,
        String status
) {
}
