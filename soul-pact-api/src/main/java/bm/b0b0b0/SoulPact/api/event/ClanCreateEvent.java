package bm.b0b0b0.SoulPact.api.event;

import java.util.UUID;
import org.bukkit.event.HandlerList;

public final class ClanCreateEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final String name;
    private final UUID leaderId;
    private final String leaderName;

    public ClanCreateEvent(long clanId, String tag, String name, UUID leaderId, String leaderName) {
        this.clanId = clanId;
        this.tag = tag;
        this.name = name;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
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

    public UUID leaderId() {
        return leaderId;
    }

    public String leaderName() {
        return leaderName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
