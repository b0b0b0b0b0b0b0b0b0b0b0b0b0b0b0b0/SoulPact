package bm.b0b0b0.SoulPact.coalition.listener;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionBossBarService;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionPlayerClanCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class CoalitionPlayerCacheListener implements Listener {

    private final SoulPactApi api;
    private final CoalitionPlayerClanCache playerClanCache;
    private final CoalitionBossBarService bossBarService;

    public CoalitionPlayerCacheListener(
            SoulPactApi api,
            CoalitionPlayerClanCache playerClanCache,
            CoalitionBossBarService bossBarService
    ) {
        this.api = api;
        this.playerClanCache = playerClanCache;
        this.bossBarService = bossBarService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        api.findClanByPlayer(event.getPlayer().getUniqueId()).thenAccept(clanOptional -> {
            clanOptional.ifPresent(clan -> playerClanCache.put(event.getPlayer().getUniqueId(), clan.id()));
            api.scheduler().runSync(() -> bossBarService.refreshPlayer(event.getPlayer()));
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerClanCache.remove(event.getPlayer().getUniqueId());
        bossBarService.removePlayer(event.getPlayer().getUniqueId());
    }

    public static BukkitTask startBossBarTask(JavaPlugin plugin, CoalitionBossBarService bossBarService) {
        return plugin.getServer().getScheduler().runTaskTimer(plugin, bossBarService::tick, 20L, 20L);
    }
}
