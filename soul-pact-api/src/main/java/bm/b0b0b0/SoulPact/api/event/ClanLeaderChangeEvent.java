package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanLeaderChangeEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final String oldLeaderName;
    private final String newLeaderName;

    public ClanLeaderChangeEvent(long clanId, String tag, String oldLeaderName, String newLeaderName) {
        this.clanId = clanId;
        this.tag = tag;
        this.oldLeaderName = oldLeaderName;
        this.newLeaderName = newLeaderName;
    }

    public long clanId() {
        return clanId;
    }

    public String tag() {
        return tag;
    }

    public String oldLeaderName() {
        return oldLeaderName;
    }

    public String newLeaderName() {
        return newLeaderName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
