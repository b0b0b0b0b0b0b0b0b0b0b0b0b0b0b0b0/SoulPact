package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanDescriptionChangeEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final String description;
    private final String actorName;

    public ClanDescriptionChangeEvent(long clanId, String tag, String description, String actorName) {
        this.clanId = clanId;
        this.tag = tag;
        this.description = description;
        this.actorName = actorName;
    }

    public long clanId() {
        return clanId;
    }

    public String tag() {
        return tag;
    }

    public String description() {
        return description;
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
