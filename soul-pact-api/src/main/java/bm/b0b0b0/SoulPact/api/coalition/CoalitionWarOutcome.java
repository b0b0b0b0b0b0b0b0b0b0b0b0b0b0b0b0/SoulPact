package bm.b0b0b0.SoulPact.api.coalition;

public record CoalitionWarOutcome(
        long attackerClanId,
        long defenderClanId,
        long loserClanId,
        long winnerClanId,
        long flagCaptureClanId
) {
    public long warParticipantClanId() {
        if (loserClanId == defenderClanId) {
            return attackerClanId;
        }
        return defenderClanId;
    }
}
