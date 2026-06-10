package bm.b0b0b0.SoulPact.quests.config;

import bm.b0b0b0.SoulPact.quests.config.settings.QuestsGuiMaterialsSettings;
import bm.b0b0b0.SoulPact.quests.config.settings.QuestsGuiSettings;
import bm.b0b0b0.SoulPact.quests.config.settings.QuestsGuiSlotsSettings;
import bm.b0b0b0.SoulPact.quests.config.settings.QuestsSettings;
import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import bm.b0b0b0.SoulPact.quests.model.QuestDefinitionParser;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;

public final class QuestsConfigFactory {

    private QuestsConfigFactory() {
    }

    public static QuestsConfig from(QuestsSettings settings) {
        QuestsGuiSettings gui = settings.gui;
        QuestsGuiSlotsSettings slots = gui.slots;
        QuestsGuiMaterialsSettings materials = gui.materials;
        return new QuestsConfig(
                settings.locale,
                settings.fallbackLocale,
                Duration.ofHours(Math.max(1, settings.dailyDurationHours)).toMillis(),
                Duration.ofHours(Math.max(0, settings.dailyCooldownHours)).toMillis(),
                Math.max(5, settings.progressFlushSeconds),
                Duration.ofSeconds(Math.max(5, settings.playerClanCacheSeconds)).toMillis(),
                settings.leaderOnlyManage,
                parseQuests(settings.quests),
                clampRows(gui.rows),
                slots.listStart,
                slots.listEnd,
                slots.back,
                parseMaterial(materials.filler, Material.GRAY_STAINED_GLASS_PANE),
                parseMaterial(materials.available, Material.BOOK),
                parseMaterial(materials.active, Material.WRITABLE_BOOK),
                parseMaterial(materials.completed, Material.ENCHANTED_BOOK),
                parseMaterial(materials.back, Material.ARROW)
        );
    }

    private static List<QuestDefinition> parseQuests(List<String> specs) {
        Map<String, QuestDefinition> byId = new LinkedHashMap<>();
        if (specs != null) {
            for (String spec : specs) {
                QuestDefinitionParser.parse(spec).ifPresent(definition -> byId.put(definition.id(), definition));
            }
        }
        return new ArrayList<>(byId.values());
    }

    private static int clampRows(int rows) {
        return Math.max(1, Math.min(6, rows));
    }

    private static Material parseMaterial(String rawValue, Material fallback) {
        if (rawValue == null || rawValue.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(rawValue);
        return material == null ? fallback : material;
    }
}
