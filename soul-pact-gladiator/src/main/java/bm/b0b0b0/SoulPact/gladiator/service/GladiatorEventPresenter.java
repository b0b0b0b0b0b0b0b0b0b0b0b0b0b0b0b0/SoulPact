package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.gladiator.config.GladiatorConfig;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public final class GladiatorEventPresenter {

    private final GladiatorConfig config;
    private final GladiatorMessages messages;

    public GladiatorEventPresenter(GladiatorConfig config, GladiatorMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    public void broadcastAll(String key, Map<String, String> placeholders) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            messages.send(online, key, placeholders);
        }
    }

    public void sendTo(Set<UUID> playerIds, String key, Map<String, String> placeholders) {
        for (UUID playerId : playerIds) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                messages.send(player, key, placeholders);
            }
        }
    }

    public void updateLobbyBossBar(GladiatorEvent event, int totalSeconds) {
        event.bossBar().name(messages.componentDefault("gladiator.bossbar.lobby", Map.of(
                "arena", event.arena().name(),
                "seconds", String.valueOf(Math.max(0, event.countdownRemaining())),
                "players", String.valueOf(event.fighters().size()),
                "clans", String.valueOf(event.remainingClans().size())
        )));
        event.bossBar().progress(clampProgress(event.countdownRemaining(), totalSeconds));
    }

    public void updateFightBossBar(GladiatorEvent event) {
        event.bossBar().name(messages.componentDefault("gladiator.bossbar.fight", Map.of(
                "arena", event.arena().name(),
                "players", String.valueOf(event.fighters().size()),
                "clans", String.valueOf(event.remainingClans().size())
        )));
        event.bossBar().progress(1.0F);
    }

    public void showBossBar(GladiatorEvent event, UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.showBossBar(event.bossBar());
        }
    }

    public void hideBossBar(GladiatorEvent event, UUID playerId) {
        Player player = Bukkit.getPlayer(playerId);
        if (player != null) {
            player.hideBossBar(event.bossBar());
        }
    }

    public void hideBossBarAll(GladiatorEvent event) {
        for (UUID playerId : event.everyone()) {
            hideBossBar(event, playerId);
        }
    }

    public void playSound(Set<UUID> playerIds, Sound sound) {
        if (sound == null) {
            return;
        }
        for (UUID playerId : playerIds) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.playSound(player.getLocation(), sound, config.soundVolume(), config.soundPitch());
            }
        }
    }

    private float clampProgress(int remaining, int total) {
        if (total <= 0) {
            return 1.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, remaining / (float) total));
    }
}
