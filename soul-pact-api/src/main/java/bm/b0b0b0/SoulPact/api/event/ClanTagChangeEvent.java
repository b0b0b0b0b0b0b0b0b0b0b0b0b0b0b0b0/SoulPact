package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanTagChangeEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String oldTag;
    private final String newTag;
    private final String actorName;

    public ClanTagChangeEvent(long clanId, String oldTag, String newTag, String actorName) {
        this.clanId = clanId;
        this.oldTag = oldTag;
        this.newTag = newTag;
        this.actorName = actorName;
    }

    public long clanId() {
        return clanId;
    }

    public String oldTag() {
        return oldTag;
    }

    public String newTag() {
        return newTag;
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
