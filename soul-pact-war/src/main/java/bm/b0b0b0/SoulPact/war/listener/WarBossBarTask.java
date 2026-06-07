package bm.b0b0b0.SoulPact.war.listener;

import bm.b0b0b0.SoulPact.war.service.WarBossBarService;
import bm.b0b0b0.SoulPact.war.service.WarVictoryService;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class WarBossBarTask extends BukkitRunnable {

    private final WarBossBarService bossBarService;
    private final WarVictoryService victoryService;

    public WarBossBarTask(WarBossBarService bossBarService, WarVictoryService victoryService) {
        this.bossBarService = bossBarService;
        this.victoryService = victoryService;
    }

    public static void start(JavaPlugin plugin, WarBossBarService bossBarService, WarVictoryService victoryService) {
        new WarBossBarTask(bossBarService, victoryService).runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        victoryService.resolveDueCaptures();
        bossBarService.tick();
    }
}
