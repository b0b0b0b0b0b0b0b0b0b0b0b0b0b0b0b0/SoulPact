package bm.b0b0b0.SoulPact.war.model;

public record WarCaptureRecord(
        long warId,
        long holderClanId,
        long targetClanId,
        long capturedAt,
        long deadlineAt
) {
}
