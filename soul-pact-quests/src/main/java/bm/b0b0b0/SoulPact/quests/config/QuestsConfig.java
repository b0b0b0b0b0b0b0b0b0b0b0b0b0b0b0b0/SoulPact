package bm.b0b0b0.SoulPact.quests.config;

import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import java.util.List;
import org.bukkit.Material;

public final class QuestsConfig {

    private final String locale;
    private final String fallbackLocale;
    private final long dailyDurationMillis;
    private final long dailyCooldownMillis;
    private final int progressFlushSeconds;
    private final long playerClanCacheMillis;
    private final boolean leaderOnlyManage;
    private final List<QuestDefinition> quests;
    private final int guiRows;
    private final int listStartSlot;
    private final int listEndSlot;
    private final int backSlot;
    private final Material fillerMaterial;
    private final Material availableMaterial;
    private final Material activeMaterial;
    private final Material completedMaterial;
    private final Material backMaterial;

    public QuestsConfig(
            String locale,
            String fallbackLocale,
            long dailyDurationMillis,
            long dailyCooldownMillis,
            int progressFlushSeconds,
            long playerClanCacheMillis,
            boolean leaderOnlyManage,
            List<QuestDefinition> quests,
            int guiRows,
            int listStartSlot,
            int listEndSlot,
            int backSlot,
            Material fillerMaterial,
            Material availableMaterial,
            Material activeMaterial,
            Material completedMaterial,
            Material backMaterial
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.dailyDurationMillis = dailyDurationMillis;
        this.dailyCooldownMillis = dailyCooldownMillis;
        this.progressFlushSeconds = progressFlushSeconds;
        this.playerClanCacheMillis = playerClanCacheMillis;
        this.leaderOnlyManage = leaderOnlyManage;
        this.quests = List.copyOf(quests);
        this.guiRows = guiRows;
        this.listStartSlot = listStartSlot;
        this.listEndSlot = listEndSlot;
        this.backSlot = backSlot;
        this.fillerMaterial = fillerMaterial;
        this.availableMaterial = availableMaterial;
        this.activeMaterial = activeMaterial;
        this.completedMaterial = completedMaterial;
        this.backMaterial = backMaterial;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public long dailyDurationMillis() {
        return dailyDurationMillis;
    }

    public long dailyCooldownMillis() {
        return dailyCooldownMillis;
    }

    public int progressFlushSeconds() {
        return progressFlushSeconds;
    }

    public long playerClanCacheMillis() {
        return playerClanCacheMillis;
    }

    public boolean leaderOnlyManage() {
        return leaderOnlyManage;
    }

    public List<QuestDefinition> quests() {
        return quests;
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

    public Material availableMaterial() {
        return availableMaterial;
    }

    public Material activeMaterial() {
        return activeMaterial;
    }

    public Material completedMaterial() {
        return completedMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }
}
