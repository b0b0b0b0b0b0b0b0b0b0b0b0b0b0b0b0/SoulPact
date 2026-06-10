package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryContributorSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryEntrySnapshot;
import bm.b0b0b0.SoulPact.bank.config.BankConfig;
import bm.b0b0b0.SoulPact.bank.message.BankMessages;
import bm.b0b0b0.SoulPact.bank.service.ClanTreasuryService;
import bm.b0b0b0.SoulPact.bank.util.MoneyFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class BankMenuPopulator {

    private final BankConfig config;
    private final BankMessages messages;

    public BankMenuPopulator(BankConfig config, BankMessages messages) {
        this.config = config;
        this.messages = messages;
    }

    public void populate(Inventory inventory, Player player, BankMenuSnapshot snapshot) {
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, BankGuiItems.filler(config.fillerMaterial()));
        }
        ClanTreasuryService.TreasuryView view = snapshot.view();
        inventory.setItem(config.balanceSlot(), buildBalanceItem(player, view));
        if (snapshot.canDeposit() && !view.locked()) {
            populateDepositButtons(inventory, player);
            inventory.setItem(config.depositAllSlot(), BankGuiItems.build(
                    messages,
                    player,
                    config.depositAllMaterial(),
                    "bank.gui.item.deposit-all.name",
                    "bank.gui.item.deposit-all.lore",
                    Map.of("max", MoneyFormat.format(config.maxDeposit()))
            ));
        }
        if (snapshot.canWithdraw() && !view.locked()) {
            populateWithdrawButtons(inventory, player, view.balance());
            inventory.setItem(config.withdrawAllSlot(), BankGuiItems.build(
                    messages,
                    player,
                    config.withdrawAllMaterial(),
                    "bank.gui.item.withdraw-all.name",
                    "bank.gui.item.withdraw-all.lore",
                    Map.of("balance", MoneyFormat.format(view.balance()))
            ));
        }
        inventory.setItem(config.backSlot(), BankGuiItems.build(
                messages,
                player,
                config.backMaterial(),
                "bank.gui.item.back.name",
                "bank.gui.item.back.lore",
                Map.of()
        ));
    }

    private void populateDepositButtons(Inventory inventory, Player player) {
        List<Double> presets = config.depositPresets();
        for (int index = 0; index < presets.size(); index++) {
            double amount = presets.get(index);
            inventory.setItem(config.depositSlot(index), BankGuiItems.build(
                    messages,
                    player,
                    config.depositMaterial(),
                    "bank.gui.item.deposit.name",
                    "bank.gui.item.deposit.lore",
                    Map.of("amount", MoneyFormat.format(amount))
            ));
        }
    }

    private void populateWithdrawButtons(Inventory inventory, Player player, double balance) {
        List<Double> presets = config.withdrawPresets();
        for (int index = 0; index < presets.size(); index++) {
            double amount = presets.get(index);
            if (amount > balance) {
                continue;
            }
            inventory.setItem(config.withdrawSlot(index), BankGuiItems.build(
                    messages,
                    player,
                    config.withdrawMaterial(),
                    "bank.gui.item.withdraw.name",
                    "bank.gui.item.withdraw.lore",
                    Map.of("amount", MoneyFormat.format(amount))
            ));
        }
    }

    private ItemStack buildBalanceItem(Player player, ClanTreasuryService.TreasuryView view) {
        String lockState = view.locked()
                ? messages.resolve(player, "bank.gui.item.lock-frozen")
                : messages.resolve(player, "bank.gui.item.lock-open");
        List<String> lore = new ArrayList<>(messages.resolveList(player, "bank.gui.item.balance.lore", Map.of(
                "tag", view.clan().tag(),
                "name", view.clan().name(),
                "balance", MoneyFormat.format(view.balance()),
                "lock_state", lockState
        )));
        lore.add("");
        lore.add(messages.resolve(player, "bank.gui.section.contributors"));
        lore.addAll(formatContributorLines(player, view.topContributors()));
        lore.add("");
        lore.add(messages.resolve(player, "bank.gui.section.ledger"));
        lore.addAll(formatLedgerLines(player, view.recentEntries()));
        return BankGuiItems.named(
                messages,
                player,
                config.balanceMaterial(),
                messages.resolve(player, "bank.gui.item.balance.name"),
                lore
        );
    }

    private List<String> formatContributorLines(Player player, List<ClanTreasuryContributorSnapshot> contributors) {
        if (contributors.isEmpty()) {
            return List.of(messages.resolve(player, "bank.gui.contributor.empty"));
        }
        List<String> lines = new ArrayList<>();
        int rank = 1;
        for (ClanTreasuryContributorSnapshot contributor : contributors) {
            lines.add(messages.resolve(player, "bank.gui.contributor.line", Map.of(
                    "rank", String.valueOf(rank),
                    "player", resolveName(contributor.playerId()),
                    "amount", MoneyFormat.format(contributor.totalDeposited())
            )));
            rank++;
        }
        return lines;
    }

    private List<String> formatLedgerLines(Player player, List<ClanTreasuryEntrySnapshot> entries) {
        if (entries.isEmpty()) {
            return List.of(messages.resolve(player, "bank.gui.ledger.empty"));
        }
        List<String> lines = new ArrayList<>();
        for (ClanTreasuryEntrySnapshot entry : entries) {
            String key = switch (entry.entryType()) {
                case ClanTreasuryService.ENTRY_DEPOSIT -> "bank.gui.ledger.line-deposit";
                case ClanTreasuryService.ENTRY_WITHDRAW -> "bank.gui.ledger.line-withdraw";
                case ClanTreasuryService.ENTRY_TRANSFER_IN -> "bank.gui.ledger.line-transfer-in";
                case ClanTreasuryService.ENTRY_TRANSFER_OUT -> "bank.gui.ledger.line-transfer-out";
                case ClanTreasuryService.ENTRY_REWARD -> "bank.gui.ledger.line-reward";
                default -> "bank.gui.ledger.line-deposit";
            };
            lines.add(messages.resolve(player, key, Map.of(
                    "amount", MoneyFormat.format(entry.amount()),
                    "player", resolveName(entry.actorId())
            )));
        }
        return lines;
    }

    private String resolveName(UUID playerId) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
        String name = offlinePlayer.getName();
        return name == null ? playerId.toString().substring(0, 8) : name;
    }
}
