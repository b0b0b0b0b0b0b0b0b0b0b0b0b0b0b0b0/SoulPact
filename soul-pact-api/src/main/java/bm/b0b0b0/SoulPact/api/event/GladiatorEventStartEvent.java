package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class GladiatorEventStartEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String arenaName;

    public GladiatorEventStartEvent(String arenaName) {
        this.arenaName = arenaName;
    }

    public String arenaName() {
        return arenaName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
