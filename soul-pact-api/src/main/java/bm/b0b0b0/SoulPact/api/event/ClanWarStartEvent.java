package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanWarStartEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final String attackerTag;
    private final String defenderTag;

    public ClanWarStartEvent(String attackerTag, String defenderTag) {
        this.attackerTag = attackerTag;
        this.defenderTag = defenderTag;
    }

    public String attackerTag() {
        return attackerTag;
    }

    public String defenderTag() {
        return defenderTag;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
