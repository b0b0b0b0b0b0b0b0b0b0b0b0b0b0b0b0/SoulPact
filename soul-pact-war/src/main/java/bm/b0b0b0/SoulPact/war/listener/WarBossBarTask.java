package bm.b0b0b0.SoulPact.war.listener;

import bm.b0b0b0.SoulPact.war.service.WarBossBarService;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public final class WarBossBarTask extends BukkitRunnable {

    private final WarBossBarService bossBarService;

    public WarBossBarTask(WarBossBarService bossBarService) {
        this.bossBarService = bossBarService;
    }

    public static void start(JavaPlugin plugin, WarBossBarService bossBarService) {
        new WarBossBarTask(bossBarService).runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void run() {
        bossBarService.tick();
    }
}
