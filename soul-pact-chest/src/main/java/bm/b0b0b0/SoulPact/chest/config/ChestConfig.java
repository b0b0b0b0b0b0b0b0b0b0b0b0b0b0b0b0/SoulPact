package bm.b0b0b0.SoulPact.chest.config;

import org.bukkit.Material;

public final class ChestConfig {

    private final String locale;
    private final String fallbackLocale;
    private final int pages;
    private final int cellsPerPage;
    private final ChestPricingSettings pricing;
    private final int guiRows;
    private final int buyCellSlot;
    private final int bankLinkSlot;
    private final int pageOneSlot;
    private final int pageTwoSlot;
    private final int pageThreeSlot;
    private final int backSlot;
    private final int prevPageSlot;
    private final int nextPageSlot;
    private final Material fillerMaterial;
    private final Material barrierMaterial;
    private final Material buyMaterial;
    private final Material bankMaterial;
    private final Material pageActiveMaterial;
    private final Material pageInactiveMaterial;
    private final Material backMaterial;
    private final Material prevMaterial;
    private final Material nextMaterial;

    public ChestConfig(
            String locale,
            String fallbackLocale,
            int pages,
            int cellsPerPage,
            ChestPricingSettings pricing,
            int guiRows,
            int buyCellSlot,
            int bankLinkSlot,
            int pageOneSlot,
            int pageTwoSlot,
            int pageThreeSlot,
            int backSlot,
            int prevPageSlot,
            int nextPageSlot,
            Material fillerMaterial,
            Material barrierMaterial,
            Material buyMaterial,
            Material bankMaterial,
            Material pageActiveMaterial,
            Material pageInactiveMaterial,
            Material backMaterial,
            Material prevMaterial,
            Material nextMaterial
    ) {
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
        this.pages = pages;
        this.cellsPerPage = cellsPerPage;
        this.pricing = pricing;
        this.guiRows = guiRows;
        this.buyCellSlot = buyCellSlot;
        this.bankLinkSlot = bankLinkSlot;
        this.pageOneSlot = pageOneSlot;
        this.pageTwoSlot = pageTwoSlot;
        this.pageThreeSlot = pageThreeSlot;
        this.backSlot = backSlot;
        this.prevPageSlot = prevPageSlot;
        this.nextPageSlot = nextPageSlot;
        this.fillerMaterial = fillerMaterial;
        this.barrierMaterial = barrierMaterial;
        this.buyMaterial = buyMaterial;
        this.bankMaterial = bankMaterial;
        this.pageActiveMaterial = pageActiveMaterial;
        this.pageInactiveMaterial = pageInactiveMaterial;
        this.backMaterial = backMaterial;
        this.prevMaterial = prevMaterial;
        this.nextMaterial = nextMaterial;
    }

    public String locale() {
        return locale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }

    public int pages() {
        return pages;
    }

    public int cellsPerPage() {
        return cellsPerPage;
    }

    public int maxCells() {
        return pages * cellsPerPage;
    }

    public ChestPricingSettings pricing() {
        return pricing;
    }

    public int guiRows() {
        return guiRows;
    }

    public int guiSize() {
        return guiRows * 9;
    }

    public int buyCellSlot() {
        return buyCellSlot;
    }

    public int bankLinkSlot() {
        return bankLinkSlot;
    }

    public int pageTabSlot(int pageIndex) {
        return switch (pageIndex) {
            case 0 -> pageOneSlot;
            case 1 -> pageTwoSlot;
            case 2 -> pageThreeSlot;
            default -> -1;
        };
    }

    public int backSlot() {
        return backSlot;
    }

    public int prevPageSlot() {
        return prevPageSlot;
    }

    public int nextPageSlot() {
        return nextPageSlot;
    }

    public Material fillerMaterial() {
        return fillerMaterial;
    }

    public Material barrierMaterial() {
        return barrierMaterial;
    }

    public Material buyMaterial() {
        return buyMaterial;
    }

    public Material bankMaterial() {
        return bankMaterial;
    }

    public Material pageActiveMaterial() {
        return pageActiveMaterial;
    }

    public Material pageInactiveMaterial() {
        return pageInactiveMaterial;
    }

    public Material backMaterial() {
        return backMaterial;
    }

    public Material prevMaterial() {
        return prevMaterial;
    }

    public Material nextMaterial() {
        return nextMaterial;
    }
}
