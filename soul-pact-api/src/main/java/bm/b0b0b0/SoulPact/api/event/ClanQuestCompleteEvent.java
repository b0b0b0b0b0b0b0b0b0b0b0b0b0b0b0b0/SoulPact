package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.event.HandlerList;

public final class ClanQuestCompleteEvent extends SoulPactEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final long clanId;
    private final String tag;
    private final String questId;

    public ClanQuestCompleteEvent(long clanId, String tag, String questId) {
        this.clanId = clanId;
        this.tag = tag;
        this.questId = questId;
    }

    public long clanId() {
        return clanId;
    }

    public String tag() {
        return tag;
    }

    public String questId() {
        return questId;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
