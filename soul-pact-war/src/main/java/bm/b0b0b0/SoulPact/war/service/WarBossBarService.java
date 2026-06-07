package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.model.ActiveWarRecord;
import bm.b0b0b0.SoulPact.war.model.WarCaptureState;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public final class WarBossBarService {

    private final WarConfig config;
    private final WarMessages messages;
    private final WarStateCache stateCache;
    private final WarPlayerClanCache playerClanCache;
    private final Map<UUID, BossBar> barsByPlayer = new ConcurrentHashMap<>();

    public WarBossBarService(
            WarConfig config,
            WarMessages messages,
            WarStateCache stateCache,
            WarPlayerClanCache playerClanCache
    ) {
        this.config = config;
        this.messages = messages;
        this.stateCache = stateCache;
        this.playerClanCache = playerClanCache;
    }

    public void refreshPlayer(Player player) {
        Long clanId = playerClanCache.find(player.getUniqueId());
        if (clanId == null) {
            removePlayer(player.getUniqueId());
            return;
        }
        Optional<ActiveWarRecord> warOptional = stateCache.activeWarFor(clanId);
        if (warOptional.isPresent()) {
            ActiveWarRecord war = warOptional.get();
            Optional<WarCaptureState> captureOptional = stateCache.captureForWar(war.id());
            if (captureOptional.isPresent()) {
                WarCaptureState capture = captureOptional.get();
                long secondsLeft = secondsLeft(capture.deadlineAt());
                float progress = captureProgress(capture.deadlineAt());
                if (clanId == capture.targetClanId()) {
                    applyBar(
                            player,
                            "war.bossbar.capture-defending",
                            Map.of("seconds", String.valueOf(secondsLeft)),
                            config.captureDefendingColor(),
                            progress
                    );
                    return;
                }
                if (clanId == capture.holderClanId()) {
                    applyBar(
                            player,
                            "war.bossbar.capture-attacking",
                            Map.of("seconds", String.valueOf(secondsLeft)),
                            config.captureAttackingColor(),
                            progress
                    );
                    return;
                }
            }
            if (clanId == war.attackerClanId()) {
                applyBar(player, "war.bossbar.active-attacker", Map.of(), config.activeColor(), 1.0F);
                return;
            }
            applyBar(player, "war.bossbar.active-defender", Map.of(), config.activeColor(), 1.0F);
            return;
        }
        if (stateCache.hasPendingForDefender(clanId)) {
            applyBar(player, "war.bossbar.pending-defender", Map.of(), config.pendingColor(), 1.0F);
            return;
        }
        removePlayer(player.getUniqueId());
    }

    public void refreshClan(long clanId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Long playerClanId = playerClanCache.find(player.getUniqueId());
            if (playerClanId != null && playerClanId == clanId) {
                refreshPlayer(player);
            }
        }
    }

    public void refreshWarClans(ActiveWarRecord war) {
        refreshClan(war.attackerClanId());
        refreshClan(war.defenderClanId());
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayer(player);
        }
    }

    public void removePlayer(UUID playerId) {
        BossBar bossBar = barsByPlayer.remove(playerId);
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void clearAll() {
        for (BossBar bossBar : barsByPlayer.values()) {
            bossBar.removeAll();
        }
        barsByPlayer.clear();
    }

    private void applyBar(Player player, String key, Map<String, String> placeholders, BarColor color, float progress) {
        Component title = messages.component(player, key, placeholders);
        String plainTitle = PlainTextComponentSerializer.plainText().serialize(title);
        BossBar bossBar = barsByPlayer.computeIfAbsent(player.getUniqueId(), ignored ->
                Bukkit.createBossBar(plainTitle, color, BarStyle.SOLID)
        );
        bossBar.setTitle(plainTitle);
        bossBar.setColor(color);
        bossBar.setProgress(Math.max(0.0F, Math.min(1.0F, progress)));
        if (!bossBar.getPlayers().contains(player)) {
            bossBar.addPlayer(player);
        }
        bossBar.setVisible(true);
    }

    private long secondsLeft(long deadlineAt) {
        return Math.max(0L, (deadlineAt - System.currentTimeMillis() + 999L) / 1000L);
    }

    private float captureProgress(long deadlineAt) {
        long totalMillis = config.captureSeconds() * 1000L;
        if (totalMillis <= 0L) {
            return 0.0F;
        }
        long remaining = Math.max(0L, deadlineAt - System.currentTimeMillis());
        return (float) remaining / (float) totalMillis;
    }
}
