package bm.b0b0b0.SoulPact.api.war;

public record ClanWarInfoExtras(
        String treasuryLine,
        boolean showDeclareWar,
        String declareWarBlockReasonId
) {
    public ClanWarInfoExtras(String treasuryLine, boolean showDeclareWar) {
        this(treasuryLine, showDeclareWar, "");
    }
}
