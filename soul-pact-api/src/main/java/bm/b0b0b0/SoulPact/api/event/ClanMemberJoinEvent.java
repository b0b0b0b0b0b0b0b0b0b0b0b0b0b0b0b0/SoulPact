package bm.b0b0b0.SoulPact.api.event;

import java.util.UUID;
import org.bukkit.event.HandlerList;

public final class ClanMemberJoinEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final UUID playerId;
    private final String playerName;

    public ClanMemberJoinEvent(long clanId, String tag, UUID playerId, String playerName) {
        this.clanId = clanId;
        this.tag = tag;
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public long clanId() {
        return clanId;
    }

    public String tag() {
        return tag;
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
