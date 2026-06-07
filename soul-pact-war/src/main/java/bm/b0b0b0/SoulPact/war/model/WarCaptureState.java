package bm.b0b0b0.SoulPact.war.model;

public record WarCaptureState(
        long holderClanId,
        long targetClanId,
        long deadlineAt
) {
}
