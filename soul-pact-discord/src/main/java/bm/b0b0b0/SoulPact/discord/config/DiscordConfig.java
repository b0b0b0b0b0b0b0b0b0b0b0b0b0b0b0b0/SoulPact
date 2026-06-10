package bm.b0b0b0.SoulPact.discord.config;

import bm.b0b0b0.SoulPact.discord.model.DiscordEventType;
import java.util.Map;

public final class DiscordConfig {

    private final String locale;
    private final String fallbackLocale;
    private final String webhookUrl;
    private final String botName;
    private final String avatarUrl;
    private final String serverName;
    private final long sendIntervalMillis;
    private final int requestTimeoutSeconds;
    private final int maxQueueSize;
    private final Map<DiscordEventType, Boolean> enabledByType;
    private final Map<DiscordEventType, Integer> colorsByType;

    public DiscordConfig(
            String locale,
            String fallbackLocale,
            String webhookUrl,
            String botName,
            String avatarUrl,
            String serverName,
            long sendIntervalMillis,
            int requestTimeoutSeconds,
            int maxQueueSize,
            Map<DiscordEventType, Boolean> enabledByType,
            Map<DiscordEventType, Integer> colorsByType
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.webhookUrl = webhookUrl;
        this.botName = botName;
        this.avatarUrl = avatarUrl;
        this.serverName = serverName;
        this.sendIntervalMillis = sendIntervalMillis;
        this.requestTimeoutSeconds = requestTimeoutSeconds;
        this.maxQueueSize = maxQueueSize;
        this.enabledByType = Map.copyOf(enabledByType);
        this.colorsByType = Map.copyOf(colorsByType);
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public String webhookUrl() {
        return webhookUrl;
    }

    public boolean webhookConfigured() {
        return webhookUrl != null && !webhookUrl.isBlank();
    }

    public String botName() {
        return botName;
    }

    public String avatarUrl() {
        return avatarUrl;
    }

    public String serverName() {
        return serverName;
    }

    public long sendIntervalMillis() {
        return sendIntervalMillis;
    }

    public int requestTimeoutSeconds() {
        return requestTimeoutSeconds;
    }

    public int maxQueueSize() {
        return maxQueueSize;
    }

    public boolean enabled(DiscordEventType type) {
        return enabledByType.getOrDefault(type, true);
    }

    public int color(DiscordEventType type) {
        return colorsByType.getOrDefault(type, 0x5865F2);
    }
}
