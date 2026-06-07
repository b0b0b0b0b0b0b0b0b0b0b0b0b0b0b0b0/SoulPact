package bm.b0b0b0.SoulPact.land.config;

public record LandExpansionSettings(
        int step,
        int maxExtent,
        double baseCost,
        double costPerBlock
) {
    public double costForExtent(int currentExtent, int defaultRadius) {
        if (currentExtent < defaultRadius) {
            currentExtent = defaultRadius;
        }
        int tier = Math.max(0, (currentExtent - defaultRadius) / step);
        return baseCost + costPerBlock * tier;
    }
}
