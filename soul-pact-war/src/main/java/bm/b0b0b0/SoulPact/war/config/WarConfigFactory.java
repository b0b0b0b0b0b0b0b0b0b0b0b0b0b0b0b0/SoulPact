package bm.b0b0b0.SoulPact.war.config;

import bm.b0b0b0.SoulPact.war.config.settings.WarBossbarSettings;
import bm.b0b0b0.SoulPact.war.config.settings.WarDeclareConfirmGuiSettings;
import bm.b0b0b0.SoulPact.war.config.settings.WarHubGuiSettings;
import bm.b0b0b0.SoulPact.war.config.settings.WarPendingDetailGuiSettings;
import bm.b0b0b0.SoulPact.war.config.settings.WarPendingListGuiSettings;
import bm.b0b0b0.SoulPact.war.config.settings.WarSettings;
import java.util.Locale;
import org.bukkit.boss.BarColor;

public final class WarConfigFactory {

    private WarConfigFactory() {
    }

    public static WarConfig from(WarSettings settings) {
        WarDeclareConfirmGuiSettings declareConfirm = settings.gui.declareConfirm;
        WarPendingListGuiSettings pendingList = settings.gui.pendingList;
        WarPendingDetailGuiSettings pendingDetail = settings.gui.pendingDetail;
        WarHubGuiSettings hub = settings.gui.hub;
        WarBossbarSettings bossbar = settings.bossbar;
        return new WarConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.ransomPercent,
                settings.captureSeconds,
                declareConfirm.rows,
                declareConfirm.slots.confirm,
                declareConfirm.slots.deny,
                pendingList.rows,
                pendingList.pageSize,
                pendingList.slots.back,
                pendingDetail.rows,
                pendingDetail.slots.accept,
                pendingDetail.slots.ransom,
                pendingDetail.slots.back,
                hub.rows,
                hub.slots.enemyFlag,
                hub.slots.pending,
                hub.slots.back,
                parseColor(bossbar.pendingColor, BarColor.YELLOW),
                parseColor(bossbar.activeColor, BarColor.RED),
                parseColor(bossbar.captureDefendingColor, BarColor.RED),
                parseColor(bossbar.captureAttackingColor, BarColor.GREEN)
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
