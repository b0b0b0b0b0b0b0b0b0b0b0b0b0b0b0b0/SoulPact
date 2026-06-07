package bm.b0b0b0.SoulPact.coalition.service;

import bm.b0b0b0.SoulPact.coalition.config.CoalitionConfig;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionMessages;
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

public final class CoalitionBossBarService {

    private final CoalitionConfig config;
    private final CoalitionMessages messages;
    private final CoalitionMembershipCache membershipCache;
    private final CoalitionWarStateTracker warStateTracker;
    private final CoalitionPlayerClanCache playerClanCache;
    private final Map<UUID, BossBar> barsByPlayer = new ConcurrentHashMap<>();

    public CoalitionBossBarService(
            CoalitionConfig config,
            CoalitionMessages messages,
            CoalitionMembershipCache membershipCache,
            CoalitionWarStateTracker warStateTracker,
            CoalitionPlayerClanCache playerClanCache
    ) {
        this.config = config;
        this.messages = messages;
        this.membershipCache = membershipCache;
        this.warStateTracker = warStateTracker;
        this.playerClanCache = playerClanCache;
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            refreshPlayer(player);
        }
    }

    public void refreshPlayer(Player player) {
        Long clanId = playerClanCache.find(player.getUniqueId());
        if (clanId == null) {
            removePlayer(player.getUniqueId());
            return;
        }
        for (long mateClanId : membershipCache.membersOf(clanId)) {
            if (mateClanId == clanId) {
                continue;
            }
            Optional<CoalitionWarStateTracker.AllyWarView> viewOptional = warStateTracker.viewFor(mateClanId);
            if (viewOptional.isEmpty()) {
                continue;
            }
            CoalitionWarStateTracker.AllyWarView view = viewOptional.get();
            applyView(player, view);
            return;
        }
        removePlayer(player.getUniqueId());
    }

    public void refreshCoalition(long clanId) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Long playerClanId = playerClanCache.find(player.getUniqueId());
            if (playerClanId != null && membershipCache.sharesCoalition(playerClanId, clanId)) {
                refreshPlayer(player);
            }
        }
    }

    public void clearAll() {
        for (BossBar bar : barsByPlayer.values()) {
            bar.removeAll();
        }
        barsByPlayer.clear();
    }

    public void removePlayer(UUID playerId) {
        BossBar bar = barsByPlayer.remove(playerId);
        if (bar != null) {
            bar.removeAll();
        }
    }

    private void applyView(Player player, CoalitionWarStateTracker.AllyWarView view) {
        String key = switch (view.phase()) {
            case DECLARED -> "coalition.bossbar.declared-on-ally";
            case ACTIVE -> "coalition.bossbar.active-for-ally";
            case CAPTURE -> "coalition.bossbar.capture-for-ally";
        };
        BarColor color = switch (view.phase()) {
            case DECLARED -> config.declaredColor();
            case ACTIVE -> config.activeColor();
            case CAPTURE -> config.captureColor();
        };
        Map<String, String> placeholders = Map.of(
                "friend_tag", view.friendTag(),
                "enemy_tag", view.enemyTag(),
                "seconds", String.valueOf(secondsLeft(view.deadlineAt()))
        );
        Component title = messages.component(player, key, placeholders);
        float progress = view.phase() == CoalitionWarStateTracker.Phase.CAPTURE
                ? captureProgress(view.deadlineAt())
                : 1.0F;
        BossBar bar = barsByPlayer.computeIfAbsent(player.getUniqueId(), ignored ->
                Bukkit.createBossBar("", color, BarStyle.SOLID)
        );
        bar.setColor(color);
        bar.setTitle(PlainTextComponentSerializer.plainText().serialize(title));
        bar.setProgress(Math.max(0.0F, Math.min(1.0F, progress)));
        if (!bar.getPlayers().contains(player)) {
            bar.addPlayer(player);
        }
    }

    private long secondsLeft(long deadlineAt) {
        if (deadlineAt <= 0L) {
            return 0L;
        }
        return Math.max(0L, (deadlineAt - System.currentTimeMillis() + 999L) / 1000L);
    }

    private float captureProgress(long deadlineAt) {
        if (deadlineAt <= 0L) {
            return 1.0F;
        }
        long remaining = deadlineAt - System.currentTimeMillis();
        if (remaining <= 0L) {
            return 0.0F;
        }
        return Math.min(1.0F, remaining / 60_000.0F);
    }
}
