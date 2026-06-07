package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

public final class ChestMenuSnapshot {

    private final ClanSnapshot clan;
    private final int page;
    private final int unlockedCells;
    private final int maxCells;
    private final Map<Integer, ItemStack> items;
    private final boolean leader;
    private final boolean canDeposit;
    private final boolean canWithdraw;
    private final boolean bankAvailable;
    private final double nextCellCost;

    public ChestMenuSnapshot(
            ClanSnapshot clan,
            int page,
            int unlockedCells,
            int maxCells,
            Map<Integer, ItemStack> items,
            boolean leader,
            boolean canDeposit,
            boolean canWithdraw,
            boolean bankAvailable,
            double nextCellCost
    ) {
        this.clan = clan;
        this.page = page;
        this.unlockedCells = unlockedCells;
        this.maxCells = maxCells;
        this.items = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            ItemStack value = entry.getValue();
            if (value != null && !value.getType().isAir()) {
                this.items.put(entry.getKey(), value.clone());
            }
        }
        this.leader = leader;
        this.canDeposit = canDeposit;
        this.canWithdraw = canWithdraw;
        this.bankAvailable = bankAvailable;
        this.nextCellCost = nextCellCost;
    }

    public ClanSnapshot clan() {
        return clan;
    }

    public int page() {
        return page;
    }

    public int unlockedCells() {
        return unlockedCells;
    }

    public int maxCells() {
        return maxCells;
    }

    public Map<Integer, ItemStack> items() {
        return items;
    }

    public boolean leader() {
        return leader;
    }

    public boolean canDeposit() {
        return canDeposit;
    }

    public boolean canWithdraw() {
        return canWithdraw;
    }

    public boolean bankAvailable() {
        return bankAvailable;
    }

    public double nextCellCost() {
        return nextCellCost;
    }

    public ChestMenuSnapshot withPage(int newPage) {
        return new ChestMenuSnapshot(
                clan,
                newPage,
                unlockedCells,
                maxCells,
                items,
                leader,
                canDeposit,
                canWithdraw,
                bankAvailable,
                nextCellCost
        );
    }

    public ChestMenuSnapshot withUnlockedCells(int newUnlocked, double newNextCost) {
        return new ChestMenuSnapshot(
                clan,
                page,
                newUnlocked,
                maxCells,
                items,
                leader,
                canDeposit,
                canWithdraw,
                bankAvailable,
                newNextCost
        );
    }
}
