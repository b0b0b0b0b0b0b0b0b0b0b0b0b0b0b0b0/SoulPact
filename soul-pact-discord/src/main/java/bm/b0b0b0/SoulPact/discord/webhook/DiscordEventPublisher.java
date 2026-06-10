package bm.b0b0b0.SoulPact.discord.webhook;

import bm.b0b0b0.SoulPact.discord.config.DiscordConfig;
import bm.b0b0b0.SoulPact.discord.model.DiscordEventType;
import java.util.Map;
import java.util.function.Supplier;

public final class DiscordEventPublisher {

    private final Supplier<DiscordConfig> configSupplier;
    private final DiscordEmbedFactory embedFactory;
    private final DiscordWebhookClient webhookClient;

    public DiscordEventPublisher(
            Supplier<DiscordConfig> configSupplier,
            DiscordEmbedFactory embedFactory,
            DiscordWebhookClient webhookClient
    ) {
        this.configSupplier = configSupplier;
        this.embedFactory = embedFactory;
        this.webhookClient = webhookClient;
    }

    public void publish(DiscordEventType type, Map<String, String> placeholders) {
        DiscordConfig config = configSupplier.get();
        if (!config.webhookConfigured() || !config.enabled(type)) {
            return;
        }
        webhookClient.enqueue(embedFactory.payload(type, placeholders));
    }
}
