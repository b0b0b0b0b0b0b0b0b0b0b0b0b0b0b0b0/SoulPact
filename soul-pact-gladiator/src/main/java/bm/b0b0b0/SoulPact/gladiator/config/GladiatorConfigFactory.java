package bm.b0b0b0.SoulPact.gladiator.config;

import bm.b0b0b0.SoulPact.gladiator.config.settings.GladiatorGuiMaterialsSettings;
import bm.b0b0b0.SoulPact.gladiator.config.settings.GladiatorGuiSettings;
import bm.b0b0b0.SoulPact.gladiator.config.settings.GladiatorGuiSlotsSettings;
import bm.b0b0b0.SoulPact.gladiator.config.settings.GladiatorSettings;
import bm.b0b0b0.SoulPact.gladiator.config.settings.GladiatorSoundSettings;
import java.time.Duration;
import java.util.Locale;
import org.bukkit.Material;
import org.bukkit.Sound;

public final class GladiatorConfigFactory {

    private GladiatorConfigFactory() {
    }

    public static GladiatorConfig from(GladiatorSettings settings) {
        GladiatorGuiSettings gui = settings.gui;
        GladiatorGuiSlotsSettings slots = gui.slots;
        GladiatorGuiMaterialsSettings materials = gui.materials;
        GladiatorSoundSettings sounds = settings.sounds;
        return new GladiatorConfig(
                settings.locale,
                settings.fallbackLocale,
                settings.adminPermission,
                Math.max(5, settings.lobbyCountdownSeconds),
                Math.max(2, settings.minClans),
                Math.max(0, settings.boundsCheckSeconds),
                Math.max(10, settings.scheduleCheckSeconds),
                Duration.ofSeconds(Math.max(5, settings.playerClanCacheSeconds)).toMillis(),
                parseMaterial(settings.wandMaterial, Material.BLAZE_ROD),
                parseSound(sounds.start),
                parseSound(sounds.eliminate),
                parseSound(sounds.win),
                (float) sounds.volume,
                (float) sounds.pitch,
                clampRows(gui.rows),
                slots.listStart,
                slots.listEnd,
                slots.back,
                parseMaterial(materials.filler, Material.GRAY_STAINED_GLASS_PANE),
                parseMaterial(materials.arenaDefault, Material.IRON_SWORD),
                parseMaterial(materials.back, Material.ARROW)
        );
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

    private static Sound parseSound(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return Sound.valueOf(rawValue.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
