package bm.b0b0b0.SoulPact.gladiator.config;

import org.bukkit.Material;
import org.bukkit.Sound;

public final class GladiatorConfig {

    private final String locale;
    private final String fallbackLocale;
    private final String adminPermission;
    private final int lobbyCountdownSeconds;
    private final int minClans;
    private final int boundsCheckSeconds;
    private final int scheduleCheckSeconds;
    private final long playerClanCacheMillis;
    private final Material wandMaterial;
    private final Sound startSound;
    private final Sound eliminateSound;
    private final Sound winSound;
    private final float soundVolume;
    private final float soundPitch;
    private final int guiRows;
    private final int listStartSlot;
    private final int listEndSlot;
    private final int backSlot;
    private final Material fillerMaterial;
    private final Material arenaDefaultMaterial;
    private final Material backMaterial;

    public GladiatorConfig(
            String locale,
            String fallbackLocale,
            String adminPermission,
            int lobbyCountdownSeconds,
            int minClans,
            int boundsCheckSeconds,
            int scheduleCheckSeconds,
            long playerClanCacheMillis,
            Material wandMaterial,
            Sound startSound,
            Sound eliminateSound,
            Sound winSound,
            float soundVolume,
            float soundPitch,
            int guiRows,
            int listStartSlot,
            int listEndSlot,
            int backSlot,
            Material fillerMaterial,
            Material arenaDefaultMaterial,
            Material backMaterial
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.adminPermission = adminPermission;
        this.lobbyCountdownSeconds = lobbyCountdownSeconds;
        this.minClans = minClans;
        this.boundsCheckSeconds = boundsCheckSeconds;
        this.scheduleCheckSeconds = scheduleCheckSeconds;
        this.playerClanCacheMillis = playerClanCacheMillis;
        this.wandMaterial = wandMaterial;
        this.startSound = startSound;
        this.eliminateSound = eliminateSound;
        this.winSound = winSound;
        this.soundVolume = soundVolume;
        this.soundPitch = soundPitch;
        this.guiRows = guiRows;
        this.listStartSlot = listStartSlot;
        this.listEndSlot = listEndSlot;
        this.backSlot = backSlot;
        this.fillerMaterial = fillerMaterial;
        this.arenaDefaultMaterial = arenaDefaultMaterial;
        this.backMaterial = backMaterial;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public String adminPermission() {
        return adminPermission;
    }

    public int lobbyCountdownSeconds() {
        return lobbyCountdownSeconds;
    }

    public int minClans() {
        return minClans;
    }

    public int boundsCheckSeconds() {
        return boundsCheckSeconds;
    }

    public int scheduleCheckSeconds() {
        return scheduleCheckSeconds;
    }

    public long playerClanCacheMillis() {
        return playerClanCacheMillis;
    }

    public Material wandMaterial() {
        return wandMaterial;
    }

    public Sound startSound() {
        return startSound;
    }

    public Sound eliminateSound() {
        return eliminateSound;
    }

    public Sound winSound() {
        return winSound;
    }

    public float soundVolume() {
        return soundVolume;
    }

    public float soundPitch() {
        return soundPitch;
    }

    public int guiRows() {
        return guiRows;
    }

    public int guiSize() {
        return guiRows * 9;
    }

    public int listStartSlot() {
        return listStartSlot;
    }

    public int listEndSlot() {
        return listEndSlot;
    }

    public int backSlot() {
        return backSlot;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    public Material arenaDefaultMaterial() {
        return arenaDefaultMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }
}
