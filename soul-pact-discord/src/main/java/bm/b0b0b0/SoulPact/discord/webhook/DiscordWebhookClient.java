package bm.b0b0b0.SoulPact.discord.webhook;

import bm.b0b0b0.SoulPact.discord.config.DiscordConfig;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

public final class DiscordWebhookClient {

    private static final int STATUS_RATE_LIMITED = 429;
    private static final long DEFAULT_RETRY_AFTER_MILLIS = 3000;

    private final Supplier<DiscordConfig> configSupplier;
    private final Logger logger;
    private final HttpClient httpClient;
    private final ConcurrentLinkedDeque<String> queue = new ConcurrentLinkedDeque<>();
    private final ScheduledExecutorService executor;
    private volatile long pausedUntilMillis;

    public DiscordWebhookClient(Supplier<DiscordConfig> configSupplier, Logger logger) {
        this.configSupplier = configSupplier;
        this.logger = logger;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(configSupplier.get().requestTimeoutSeconds()))
                .build();
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "SoulPact-Discord-Webhook");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void start() {
        long interval = configSupplier.get().sendIntervalMillis();
        executor.scheduleWithFixedDelay(this::drainOne, interval, interval, TimeUnit.MILLISECONDS);
    }

    public void enqueue(String jsonPayload) {
        DiscordConfig config = configSupplier.get();
        if (!config.webhookConfigured()) {
            return;
        }
        while (queue.size() >= config.maxQueueSize()) {
            queue.pollFirst();
        }
        queue.addLast(jsonPayload);
    }

    public void shutdown() {
        drainRemaining();
        executor.shutdownNow();
    }

    private void drainOne() {
        if (System.currentTimeMillis() < pausedUntilMillis) {
            return;
        }
        String payload = queue.pollFirst();
        if (payload == null) {
            return;
        }
        send(payload);
    }

    private void drainRemaining() {
        String payload;
        while ((payload = queue.pollFirst()) != null) {
            send(payload);
        }
    }

    private void send(String payload) {
        DiscordConfig config = configSupplier.get();
        if (!config.webhookConfigured()) {
            return;
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.webhookUrl()))
                    .timeout(Duration.ofSeconds(config.requestTimeoutSeconds()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == STATUS_RATE_LIMITED) {
                pausedUntilMillis = System.currentTimeMillis() + retryAfterMillis(response);
                queue.addFirst(payload);
                return;
            }
            if (response.statusCode() >= 400) {
                logger.warning("Discord webhook rejected payload: HTTP " + response.statusCode());
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        } catch (Exception exception) {
            logger.warning("Discord webhook send failed: " + exception.getMessage());
        }
    }

    private long retryAfterMillis(HttpResponse<String> response) {
        return response.headers().firstValue("Retry-After")
                .map(value -> {
                    try {
                        return (long) (Double.parseDouble(value) * 1000);
                    } catch (NumberFormatException exception) {
                        return DEFAULT_RETRY_AFTER_MILLIS;
                    }
                })
                .orElse(DEFAULT_RETRY_AFTER_MILLIS);
    }
}
