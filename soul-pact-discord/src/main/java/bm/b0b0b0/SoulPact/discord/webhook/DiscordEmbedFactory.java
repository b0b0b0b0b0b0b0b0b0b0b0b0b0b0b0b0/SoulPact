package bm.b0b0b0.SoulPact.discord.webhook;

import bm.b0b0b0.SoulPact.discord.config.DiscordConfig;
import bm.b0b0b0.SoulPact.discord.message.DiscordMessages;
import bm.b0b0b0.SoulPact.discord.model.DiscordEventType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class DiscordEmbedFactory {

    private final Supplier<DiscordConfig> configSupplier;
    private final DiscordMessages messages;

    public DiscordEmbedFactory(Supplier<DiscordConfig> configSupplier, DiscordMessages messages) {
        this.configSupplier = configSupplier;
        this.messages = messages;
    }

    public String payload(DiscordEventType type, Map<String, String> placeholders) {
        DiscordConfig config = configSupplier.get();
        Map<String, String> enriched = new HashMap<>(placeholders);
        enriched.putIfAbsent("server", config.serverName());

        JsonObject embed = new JsonObject();
        embed.addProperty("title", messages.resolve(embedKey(type, "title"), enriched));
        String description = joinLines(messages.resolveList(embedKey(type, "description"), enriched));
        if (!description.isBlank()) {
            embed.addProperty("description", description);
        }
        embed.addProperty("color", config.color(type));
        embed.addProperty("timestamp", Instant.now().toString());
        JsonObject footer = new JsonObject();
        footer.addProperty("text", messages.resolve("discord.embed.footer", enriched));
        embed.add("footer", footer);

        JsonObject payload = new JsonObject();
        if (!config.botName().isBlank()) {
            payload.addProperty("username", config.botName());
        }
        if (!config.avatarUrl().isBlank()) {
            payload.addProperty("avatar_url", config.avatarUrl());
        }
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        payload.add("embeds", embeds);
        return payload.toString();
    }

    private String embedKey(DiscordEventType type, String suffix) {
        return "discord.embed." + type.key() + "." + suffix;
    }

    private String joinLines(List<String> lines) {
        return String.join("\n", lines);
    }
}
