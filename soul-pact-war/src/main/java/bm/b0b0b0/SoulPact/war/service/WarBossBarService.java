package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge;
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
    private final WarClanLookup clanLookup;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;
    private final Map<UUID, BossBar> barsByPlayer = new ConcurrentHashMap<>();

    public WarBossBarService(
            WarConfig config,
            WarMessages messages,
            WarStateCache stateCache,
            WarPlayerClanCache playerClanCache,
            WarClanLookup clanLookup,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup
    ) {
        this.config = config;
        this.messages = messages;
        this.stateCache = stateCache;
        this.playerClanCache = playerClanCache;
        this.clanLookup = clanLookup;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
    }

    public void refreshPlayer(Player player) {
        Long clanId = resolveClanId(player.getUniqueId());
        if (clanId == null) {
            removePlayer(player.getUniqueId());
            return;
        }
        Optional<ActiveWarRecord> warOptional = findWarForParticipant(clanId);
        if (warOptional.isPresent()) {
            ActiveWarRecord war = warOptional.get();
            Optional<WarCaptureState> captureOptional = stateCache.captureForWar(war.id());
            if (captureOptional.isPresent()) {
                WarCaptureState capture = captureOptional.get();
                long secondsLeft = secondsLeft(capture.deadlineAt());
                float progress = captureProgress(capture.deadlineAt());
                long playerSide = warSideRoot(war, clanId);
                long holderSide = warSideRoot(war, capture.holderClanId());
                long targetSide = warSideRoot(war, capture.targetClanId());
                if (playerSide != 0L && playerSide == targetSide) {
                    applyBar(
                            player,
                            "war.bossbar.capture-defending",
                            Map.of("seconds", String.valueOf(secondsLeft)),
                            config.captureDefendingColor(),
                            progress
                    );
                    return;
                }
                if (playerSide != 0L && playerSide == holderSide) {
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
            long playerSide = warSideRoot(war, clanId);
            if (playerSide == war.attackerClanId()) {
                applyBar(player, "war.bossbar.active-attacker", Map.of(), config.activeColor(), 1.0F);
                return;
            }
            if (playerSide == war.defenderClanId()) {
                applyBar(player, "war.bossbar.active-defender", Map.of(), config.activeColor(), 1.0F);
                return;
            }
        }
        if (stateCache.hasPendingForAttacker(clanId)) {
            applyBar(player, "war.bossbar.pending-attacker", Map.of(), config.pendingColor(), 1.0F);
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
            Long playerClanId = resolveClanId(player.getUniqueId());
            if (playerClanId != null && playerClanId == clanId) {
                refreshPlayer(player);
            }
        }
    }

    public void refreshWarClans(ActiveWarRecord war) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Long clanId = resolveClanId(player.getUniqueId());
            if (clanId == null) {
                continue;
            }
            findWarForParticipant(clanId).ifPresent(activeWar -> {
                if (activeWar.id() == war.id()) {
                    refreshPlayer(player);
                }
            });
        }
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

    private Long resolveClanId(UUID playerId) {
        Long cached = playerClanCache.find(playerId);
        if (cached != null) {
            return cached;
        }
        Optional<Long> lookedUp = clanLookup.findClanIdByPlayerSync(playerId);
        lookedUp.ifPresent(clanId -> playerClanCache.put(playerId, clanId));
        return lookedUp.orElse(null);
    }

    private Optional<ActiveWarRecord> findWarForParticipant(long clanId) {
        Optional<ActiveWarRecord> direct = stateCache.activeWarFor(clanId);
        if (direct.isPresent()) {
            return direct;
        }
        return coalitionWarBridgeLookup.resolve().flatMap(bridge -> findCoalitionParticipantWar(clanId, bridge));
    }

    private Optional<ActiveWarRecord> findCoalitionParticipantWar(long clanId, CoalitionWarBridge bridge) {
        for (ActiveWarRecord war : stateCache.allActiveWars()) {
            if (isCoalitionWarAlly(clanId, war, bridge)) {
                return Optional.of(war);
            }
        }
        return Optional.empty();
    }

    private boolean isCoalitionWarAlly(long clanId, ActiveWarRecord war, CoalitionWarBridge bridge) {
        if (clanId == war.attackerClanId() || clanId == war.defenderClanId()) {
            return false;
        }
        return bridge.coalitionClanIds(war.attackerClanId()).contains(clanId)
                || bridge.coalitionClanIds(war.defenderClanId()).contains(clanId);
    }

    private long warSideRoot(ActiveWarRecord war, long clanId) {
        if (clanId == war.attackerClanId()) {
            return war.attackerClanId();
        }
        if (clanId == war.defenderClanId()) {
            return war.defenderClanId();
        }
        return coalitionWarBridgeLookup.resolve()
                .map(bridge -> {
                    if (bridge.coalitionClanIds(war.attackerClanId()).contains(clanId)) {
                        return war.attackerClanId();
                    }
                    if (bridge.coalitionClanIds(war.defenderClanId()).contains(clanId)) {
                        return war.defenderClanId();
                    }
                    return 0L;
                })
                .orElse(0L);
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
        long millisLeft = deadlineAt - System.currentTimeMillis();
        if (millisLeft <= 0L) {
            return 0L;
        }
        return (millisLeft + 999L) / 1000L;
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
