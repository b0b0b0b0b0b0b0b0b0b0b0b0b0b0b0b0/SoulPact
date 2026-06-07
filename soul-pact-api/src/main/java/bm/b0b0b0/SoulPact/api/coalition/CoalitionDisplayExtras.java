package bm.b0b0b0.SoulPact.api.coalition;

import java.util.List;

public record CoalitionDisplayExtras(
        String coalitionLine,
        List<CoalitionAllySnapshot> allies
) {
    public static CoalitionDisplayExtras empty() {
        return new CoalitionDisplayExtras("", List.of());
    }
}
