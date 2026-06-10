package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanWarEndEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String attackerTag;
    private final String defenderTag;
    private final String winnerTag;
    private final String loserTag;

    public ClanWarEndEvent(String attackerTag, String defenderTag, String winnerTag, String loserTag) {
        this.attackerTag = attackerTag;
        this.defenderTag = defenderTag;
        this.winnerTag = winnerTag;
        this.loserTag = loserTag;
    }

    public String attackerTag() {
        return attackerTag;
    }

    public String defenderTag() {
        return defenderTag;
    }

    public String winnerTag() {
        return winnerTag;
    }

    public String loserTag() {
        return loserTag;
    }

    public boolean hasWinner() {
        return winnerTag != null && !winnerTag.isBlank();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
