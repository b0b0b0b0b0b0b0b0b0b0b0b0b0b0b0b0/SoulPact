package bm.b0b0b0.SoulPact.chest.service;

import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import java.util.OptionalInt;

public final class ChestGuiLayout {

    private static final int STORAGE_FIRST_ROW = 1;
    private static final int STORAGE_LAST_ROW = 4;

    private final ChestConfig config;

    public ChestGuiLayout(ChestConfig config) {
        this.config = config;
    }

    public boolean isControlSlot(int slot) {
        if (slot == config.buyCellSlot()
                || slot == config.bankLinkSlot()
                || slot == config.backSlot()
                || slot == config.prevPageSlot()
                || slot == config.nextPageSlot()) {
            return true;
        }
        for (int page = 0; page < config.pages(); page++) {
            if (slot == config.pageTabSlot(page)) {
                return true;
            }
        }
        return false;
    }

    public boolean isStorageSlot(int slot) {
        int row = slot / 9;
        return row >= STORAGE_FIRST_ROW && row <= STORAGE_LAST_ROW;
    }

    public OptionalInt cellIndex(int page, int slot) {
        if (!isStorageSlot(slot)) {
            return OptionalInt.empty();
        }
        int row = slot / 9;
        int column = slot % 9;
        int indexInPage = (row - STORAGE_FIRST_ROW) * 9 + column;
        if (indexInPage < 0 || indexInPage >= config.cellsPerPage()) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(page * config.cellsPerPage() + indexInPage);
    }

    public int slotForCell(int page, int cellIndexInPage) {
        int row = STORAGE_FIRST_ROW + cellIndexInPage / 9;
        int column = cellIndexInPage % 9;
        return row * 9 + column;
    }

    public boolean isUnlocked(int cellIndex, int unlockedCells) {
        return cellIndex >= 0 && cellIndex < unlockedCells;
    }
}
