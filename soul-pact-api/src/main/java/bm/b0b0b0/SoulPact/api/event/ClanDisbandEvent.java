package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanDisbandEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final String name;
    private final String actorName;

    public ClanDisbandEvent(long clanId, String tag, String name, String actorName) {
        this.clanId = clanId;
        this.tag = tag;
        this.name = name;
        this.actorName = actorName;
    }

    public long clanId() {
        return clanId;
    }

    public String tag() {
        return tag;
    }

    public String clanName() {
        return name;
    }

    public String actorName() {
        return actorName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
