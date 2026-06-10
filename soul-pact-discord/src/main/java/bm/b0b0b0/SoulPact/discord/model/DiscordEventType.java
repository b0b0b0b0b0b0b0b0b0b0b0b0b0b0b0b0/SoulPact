package bm.b0b0b0.SoulPact.discord.model;

import java.util.Locale;

public enum DiscordEventType {
    SERVER_START,
    SERVER_STOP,
    CLAN_CREATE,
    CLAN_DELETE,
    TAG_CHANGE,
    DESC_CHANGE,
    ROLE_CHANGE,
    MEMBER_JOIN,
    MEMBER_LEAVE,
    MEMBER_KICK,
    LEADER_CHANGE,
    WAR_START,
    WAR_END,
    WAR_WIN,
    QUEST_COMPLETE,
    GLAD_START,
    GLAD_WIN;

    public String key() {
        return name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
