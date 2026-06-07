package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarGuiItems;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public final class WarPendingDetailMenuPopulator {

    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));

    private final WarConfig config;
    private final WarMessages messages;
    private final ClanWarService warService;

    public WarPendingDetailMenuPopulator(WarConfig config, WarMessages messages, ClanWarService warService) {
        this.config = config;
        this.messages = messages;
        this.warService = warService;
    }

    public void populate(
            Inventory inventory,
            Player player,
            WarDeclarationRecord declaration,
            String attackerTag,
            String attackerName
    ) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, WarGuiItems.filler(Material.GRAY_STAINED_GLASS_PANE));
        }
        Map<String, String> placeholders = Map.of(
                "id", String.valueOf(declaration.id()),
                "attacker_id", String.valueOf(declaration.attackerClanId()),
                "attacker_tag", attackerTag,
                "attacker_name", attackerName,
                "ransom_percent", String.valueOf((int) (config.ransomPercent() * 100.0D)),
                "ransom_amount", "…"
        );
        inventory.setItem(config.pendingAcceptSlot(), WarGuiItems.build(
                messages,
                player,
                Material.LIME_DYE,
                "war.gui.pending-detail.item.accept.name",
                "war.gui.pending-detail.item.accept.lore",
                placeholders
        ));
        inventory.setItem(config.pendingRansomSlot(), WarGuiItems.build(
                messages,
                player,
                Material.GOLD_INGOT,
                "war.gui.pending-detail.item.ransom.name",
                "war.gui.pending-detail.item.ransom.lore",
                placeholders
        ));
        inventory.setItem(config.pendingBackSlot(), WarGuiItems.build(
                messages,
                player,
                Material.ARROW,
                "war.gui.pending-detail.item.back.name",
                "war.gui.pending-detail.item.back.lore",
                Map.of()
        ));
        warService.treasuryLineForList(declaration.defenderClanId()).thenAccept(balanceLine -> {
            Map<String, String> enriched = Map.of(
                    "id", String.valueOf(declaration.id()),
                    "attacker_id", String.valueOf(declaration.attackerClanId()),
                    "attacker_tag", attackerTag,
                    "attacker_name", attackerName,
                    "ransom_percent", String.valueOf((int) (config.ransomPercent() * 100.0D)),
                    "ransom_amount", balanceLine.isBlank() ? "0" : MONEY.format(parseBalance(balanceLine) * config.ransomPercent())
            );
            inventory.setItem(config.pendingRansomSlot(), WarGuiItems.build(
                    messages,
                    player,
                    Material.GOLD_INGOT,
                    "war.gui.pending-detail.item.ransom.name",
                    "war.gui.pending-detail.item.ransom.lore",
                    enriched
            ));
        });
    }

    private double parseBalance(String formatted) {
        try {
            return Double.parseDouble(formatted.replace(",", ""));
        } catch (NumberFormatException exception) {
            return 0.0D;
        }
    }
}
