package bm.b0b0b0.SoulPact.chest.config;

public final class ChestPricingSettings {

    private final double baseCost;
    private final double linearStep;
    private final int tierSize;
    private final double tierMultiplier;
    private final double maxCost;

    public ChestPricingSettings(
            double baseCost,
            double linearStep,
            int tierSize,
            double tierMultiplier,
            double maxCost
    ) {
        this.baseCost = baseCost;
        this.linearStep = linearStep;
        this.tierSize = Math.max(1, tierSize);
        this.tierMultiplier = tierMultiplier <= 0.0D ? 1.0D : tierMultiplier;
        this.maxCost = maxCost;
    }

    public double costForCell(int unlockedCells) {
        int tier = unlockedCells / tierSize;
        double linear = baseCost + linearStep * unlockedCells;
        double scaled = linear * Math.pow(tierMultiplier, tier);
        if (maxCost > 0.0D) {
            return Math.min(maxCost, scaled);
        }
        return scaled;
    }
}
