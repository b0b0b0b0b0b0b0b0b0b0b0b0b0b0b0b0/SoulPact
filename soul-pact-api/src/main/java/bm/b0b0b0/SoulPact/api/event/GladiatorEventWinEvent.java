package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class GladiatorEventWinEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String arenaName;
    private final String winnerTag;
    private final int participantCount;

    public GladiatorEventWinEvent(String arenaName, String winnerTag, int participantCount) {
        this.arenaName = arenaName;
        this.winnerTag = winnerTag;
        this.participantCount = participantCount;
    }

    public String arenaName() {
        return arenaName;
    }

    public String winnerTag() {
        return winnerTag;
    }

    public int participantCount() {
        return participantCount;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
