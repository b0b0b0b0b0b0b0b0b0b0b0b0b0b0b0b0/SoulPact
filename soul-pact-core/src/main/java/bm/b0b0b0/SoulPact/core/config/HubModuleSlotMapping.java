package bm.b0b0b0.SoulPact.core.config;

import java.util.List;
import java.util.Map;

public final class HubModuleSlotMapping {

    private final Map<String, Integer> slotsByExtensionId;
    private final List<Integer> legacyOrderSlots;
    private final List<Integer> overflowSlots;

    public HubModuleSlotMapping(
            Map<String, Integer> slotsByExtensionId,
            List<Integer> legacyOrderSlots,
            List<Integer> overflowSlots
    ) {
        this.slotsByExtensionId = Map.copyOf(slotsByExtensionId);
        this.legacyOrderSlots = List.copyOf(legacyOrderSlots);
        this.overflowSlots = List.copyOf(overflowSlots);
    }

    public Map<String, Integer> slotsByExtensionId() {
        return slotsByExtensionId;
    }

    public List<Integer> legacyOrderSlots() {
        return legacyOrderSlots;
    }

    public List<Integer> overflowSlots() {
        return overflowSlots;
    }

    public boolean usesExtensionIds() {
        return !slotsByExtensionId.isEmpty();
    }
}
