package bm.b0b0b0.SoulPact.coalition.config;

import bm.b0b0b0.SoulPact.coalition.config.settings.CoalitionBossbarSettings;
import bm.b0b0b0.SoulPact.coalition.config.settings.CoalitionSettings;
import bm.b0b0b0.SoulPact.coalition.config.settings.CoalitionTreasurySettings;
import java.util.Locale;
import org.bukkit.boss.BarColor;

public final class CoalitionConfigFactory {

    private CoalitionConfigFactory() {
    }

    public static CoalitionConfig from(CoalitionSettings settings) {
        CoalitionTreasurySettings treasury = settings.treasury;
        CoalitionBossbarSettings bossbar = settings.bossbar;
        return new CoalitionConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.maxMembers,
                treasury.warSharePercent,
                treasury.captureSharePercent,
                treasury.poolSharePercent,
                settings.gui.hub.rows,
                settings.gui.hub.slots.memberStart,
                settings.gui.hub.slots.invite,
                settings.gui.hub.slots.leave,
                parseColor(bossbar.declaredColor, BarColor.YELLOW),
                parseColor(bossbar.activeColor, BarColor.RED),
                parseColor(bossbar.captureColor, BarColor.RED)
        );
    }

    private static BarColor parseColor(String rawValue, BarColor fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        try {
            return BarColor.valueOf(rawValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return fallback;
        }
    }
}
