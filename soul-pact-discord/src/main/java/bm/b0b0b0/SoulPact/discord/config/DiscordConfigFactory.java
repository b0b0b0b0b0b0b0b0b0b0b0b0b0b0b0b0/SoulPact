package bm.b0b0b0.SoulPact.discord.config;

import bm.b0b0b0.SoulPact.discord.config.settings.DiscordColorsSettings;
import bm.b0b0b0.SoulPact.discord.config.settings.DiscordEventsSettings;
import bm.b0b0b0.SoulPact.discord.config.settings.DiscordSettings;
import bm.b0b0b0.SoulPact.discord.model.DiscordEventType;
import java.util.EnumMap;
import java.util.Map;

public final class DiscordConfigFactory {

    private static final int DEFAULT_COLOR = 0x5865F2;

    private DiscordConfigFactory() {
    }

    public static DiscordConfig from(DiscordSettings settings) {
        return new DiscordConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.webhookUrl == null ? "" : settings.webhookUrl.trim(),
                settings.botName,
                settings.avatarUrl,
                settings.serverName,
                Math.max(500, settings.sendIntervalMillis),
                Math.max(3, settings.requestTimeoutSeconds),
                Math.max(10, settings.maxQueueSize),
                enabledMap(settings.events),
                colorMap(settings.colors)
        );
    }

    private static Map<DiscordEventType, Boolean> enabledMap(DiscordEventsSettings events) {
        Map<DiscordEventType, Boolean> enabled = new EnumMap<>(DiscordEventType.class);
        enabled.put(DiscordEventType.SERVER_START, events.serverStart);
        enabled.put(DiscordEventType.SERVER_STOP, events.serverStop);
        enabled.put(DiscordEventType.CLAN_CREATE, events.clanCreate);
        enabled.put(DiscordEventType.CLAN_DELETE, events.clanDelete);
        enabled.put(DiscordEventType.TAG_CHANGE, events.tagChange);
        enabled.put(DiscordEventType.DESC_CHANGE, events.descChange);
        enabled.put(DiscordEventType.ROLE_CHANGE, events.roleChange);
        enabled.put(DiscordEventType.MEMBER_JOIN, events.memberJoin);
        enabled.put(DiscordEventType.MEMBER_LEAVE, events.memberLeave);
        enabled.put(DiscordEventType.MEMBER_KICK, events.memberKick);
        enabled.put(DiscordEventType.LEADER_CHANGE, events.leaderChange);
        enabled.put(DiscordEventType.WAR_START, events.warStart);
        enabled.put(DiscordEventType.WAR_END, events.warEnd);
        enabled.put(DiscordEventType.WAR_WIN, events.warWin);
        enabled.put(DiscordEventType.QUEST_COMPLETE, events.questComplete);
        enabled.put(DiscordEventType.GLAD_START, events.gladStart);
        enabled.put(DiscordEventType.GLAD_WIN, events.gladWin);
        return enabled;
    }

    private static Map<DiscordEventType, Integer> colorMap(DiscordColorsSettings colors) {
        Map<DiscordEventType, Integer> parsed = new EnumMap<>(DiscordEventType.class);
        parsed.put(DiscordEventType.SERVER_START, parseColor(colors.serverStart));
        parsed.put(DiscordEventType.SERVER_STOP, parseColor(colors.serverStop));
        parsed.put(DiscordEventType.CLAN_CREATE, parseColor(colors.clanCreate));
        parsed.put(DiscordEventType.CLAN_DELETE, parseColor(colors.clanDelete));
        parsed.put(DiscordEventType.TAG_CHANGE, parseColor(colors.tagChange));
        parsed.put(DiscordEventType.DESC_CHANGE, parseColor(colors.descChange));
        parsed.put(DiscordEventType.ROLE_CHANGE, parseColor(colors.roleChange));
        parsed.put(DiscordEventType.MEMBER_JOIN, parseColor(colors.memberJoin));
        parsed.put(DiscordEventType.MEMBER_LEAVE, parseColor(colors.memberLeave));
        parsed.put(DiscordEventType.MEMBER_KICK, parseColor(colors.memberKick));
        parsed.put(DiscordEventType.LEADER_CHANGE, parseColor(colors.leaderChange));
        parsed.put(DiscordEventType.WAR_START, parseColor(colors.warStart));
        parsed.put(DiscordEventType.WAR_END, parseColor(colors.warEnd));
        parsed.put(DiscordEventType.WAR_WIN, parseColor(colors.warWin));
        parsed.put(DiscordEventType.QUEST_COMPLETE, parseColor(colors.questComplete));
        parsed.put(DiscordEventType.GLAD_START, parseColor(colors.gladStart));
        parsed.put(DiscordEventType.GLAD_WIN, parseColor(colors.gladWin));
        return parsed;
    }

    private static int parseColor(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return DEFAULT_COLOR;
        }
        try {
            return Integer.parseInt(rawValue.replace("#", "").trim(), 16);
        } catch (NumberFormatException exception) {
            return DEFAULT_COLOR;
        }
    }
}
