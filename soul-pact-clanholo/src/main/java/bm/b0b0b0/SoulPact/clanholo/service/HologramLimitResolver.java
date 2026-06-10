package bm.b0b0b0.SoulPact.clanholo.service;

import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.entity.Player;

public final class HologramLimitResolver {

    private final Supplier<ClanHoloConfig> configSupplier;

    public HologramLimitResolver(Supplier<ClanHoloConfig> configSupplier) {
        this.configSupplier = configSupplier;
    }

    public int resolve(Player player) {
        ClanHoloConfig config = configSupplier.get();
        int limit = config.maxHologramsDefault();
        for (Map.Entry<String, Integer> entry : config.maxHologramsByPermission().entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                limit = Math.max(limit, entry.getValue());
            }
        }
        return limit;
    }
}
