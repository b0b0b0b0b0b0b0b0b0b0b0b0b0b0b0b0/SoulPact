package bm.b0b0b0.SoulPact.quests.gui;

import bm.b0b0b0.SoulPact.quests.config.QuestsConfig;
import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import bm.b0b0b0.SoulPact.quests.model.QuestDefinition;
import bm.b0b0b0.SoulPact.quests.service.QuestEntryView;
import bm.b0b0b0.SoulPact.quests.service.QuestsOverview;
import bm.b0b0b0.SoulPact.quests.util.QuestTimeFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class QuestsMenuPopulator {

    private final QuestsConfig config;
    private final QuestsMessages messages;

    public QuestsMenuPopulator(QuestsConfig config, QuestsMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    public void populate(
            Inventory inventory,
            Player player,
            QuestsOverview overview,
            Map<Integer, QuestEntryView> entriesBySlot
    ) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, QuestsGuiItems.filler(config.fillerMaterial()));
        }
        int slot = config.listStartSlot();
        for (QuestEntryView entry : overview.entries()) {
            if (slot > config.listEndSlot() || slot >= inventory.getSize()) {
                break;
            }
            inventory.setItem(slot, buildQuestItem(player, overview, entry));
            entriesBySlot.put(slot, entry);
            slot++;
        }
        inventory.setItem(config.backSlot(), QuestsGuiItems.build(
                messages,
                player,
                config.backMaterial(),
                "quests.gui.item.back.name",
                "quests.gui.item.back.lore",
                Map.of()
        ));
    }

    private ItemStack buildQuestItem(Player player, QuestsOverview overview, QuestEntryView entry) {
        QuestDefinition definition = entry.definition();
        Map<String, String> placeholders = basePlaceholders(player, entry);
        List<String> lore = new ArrayList<>(messages.resolveList(player, "quests.gui.item.quest.lore", placeholders));
        lore.addAll(rewardLines(player, definition, placeholders));
        lore.addAll(stateLines(player, overview, entry, placeholders));
        return QuestsGuiItems.named(
                materialFor(entry.state()),
                messages.resolve(player, "quests.gui.item.quest.name", placeholders),
                lore
        );
    }

    private Map<String, String> basePlaceholders(Player player, QuestEntryView entry) {
        QuestDefinition definition = entry.definition();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("quest", messages.resolve(player, "quests.quest." + definition.id() + ".name"));
        placeholders.put("description", messages.resolve(player, "quests.quest." + definition.id() + ".description"));
        placeholders.put("type", messages.resolve(player, "quests.type." + definition.type().name().toLowerCase(java.util.Locale.ROOT)));
        placeholders.put("progress", String.valueOf(entry.progress()));
        placeholders.put("target", String.valueOf(definition.targetAmount()));
        placeholders.put("points", String.valueOf(definition.rewardPoints()));
        placeholders.put("treasury", String.valueOf(definition.rewardTreasury()));
        return placeholders;
    }

    private List<String> rewardLines(Player player, QuestDefinition definition, Map<String, String> placeholders) {
        List<String> lines = new ArrayList<>();
        lines.add(messages.resolve(player, "quests.gui.section.rewards"));
        if (definition.rewardPoints() != 0) {
            lines.add(messages.resolve(player, "quests.gui.reward.points", placeholders));
        }
        if (definition.rewardTreasury() > 0D) {
            lines.add(messages.resolve(player, "quests.gui.reward.treasury", placeholders));
        }
        if (!definition.rewardCommands().isEmpty()) {
            lines.add(messages.resolve(player, "quests.gui.reward.extra", placeholders));
        }
        if (lines.size() == 1) {
            lines.add(messages.resolve(player, "quests.gui.reward.none", placeholders));
        }
        return lines;
    }

    private List<String> stateLines(
            Player player,
            QuestsOverview overview,
            QuestEntryView entry,
            Map<String, String> placeholders
    ) {
        List<String> lines = new ArrayList<>();
        lines.add("");
        switch (entry.state()) {
            case ACTIVE -> {
                lines.add(messages.resolve(player, "quests.gui.state.active", placeholders));
                if (entry.expiresAt() > 0) {
                    lines.add(messages.resolve(player, "quests.gui.state.expires", Map.of(
                            "time", QuestTimeFormat.remaining(messages, entry.expiresAt())
                    )));
                }
                if (overview.canManage()) {
                    lines.add(messages.resolve(player, "quests.gui.action.abandon"));
                }
            }
            case AVAILABLE -> {
                if (overview.activeEntry().isPresent()) {
                    lines.add(messages.resolve(player, "quests.gui.state.blocked", placeholders));
                } else if (overview.canManage()) {
                    lines.add(messages.resolve(player, "quests.gui.action.start"));
                } else {
                    lines.add(messages.resolve(player, "quests.gui.state.available", placeholders));
                }
            }
            case ON_COOLDOWN -> lines.add(messages.resolve(player, "quests.gui.state.cooldown", Map.of(
                    "time", QuestTimeFormat.remaining(messages, entry.cooldownEndsAt())
            )));
            case COMPLETED -> lines.add(messages.resolve(player, "quests.gui.state.completed", placeholders));
        }
        return lines;
    }

    private Material materialFor(QuestEntryView.QuestEntryState state) {
        return switch (state) {
            case AVAILABLE -> config.availableMaterial();
            case ACTIVE -> config.activeMaterial();
            case ON_COOLDOWN, COMPLETED -> config.completedMaterial();
        };
    }
}
