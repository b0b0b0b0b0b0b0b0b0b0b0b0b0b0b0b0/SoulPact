package bm.b0b0b0.SoulPact.api.event;

import java.util.UUID;
import org.bukkit.event.HandlerList;

public final class ClanMemberRoleChangeEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final UUID playerId;
    private final String playerName;
    private final String oldRole;
    private final String newRole;

    public ClanMemberRoleChangeEvent(
            long clanId,
            String tag,
            UUID playerId,
            String playerName,
            String oldRole,
            String newRole
    ) {
        this.clanId = clanId;
        this.tag = tag;
        this.playerId = playerId;
        this.playerName = playerName;
        this.oldRole = oldRole;
        this.newRole = newRole;
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

    public String oldRole() {
        return oldRole;
    }

    public String newRole() {
        return newRole;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
