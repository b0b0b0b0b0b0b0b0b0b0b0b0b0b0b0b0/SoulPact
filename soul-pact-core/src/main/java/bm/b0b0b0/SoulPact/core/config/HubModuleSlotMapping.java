package bm.b0b0b0.SoulPact.core.config;

import java.util.List;
import java.util.Map;

public final class HubModuleSlotMapping {

    private final Map<String, Integer> slotsByExtensionId;
    private final List<Integer> legacyOrderSlots;

    public HubModuleSlotMapping(Map<String, Integer> slotsByExtensionId, List<Integer> legacyOrderSlots) {
        this.slotsByExtensionId = Map.copyOf(slotsByExtensionId);
        this.legacyOrderSlots = List.copyOf(legacyOrderSlots);
    }

    public Map<String, Integer> slotsByExtensionId() {
        return slotsByExtensionId;
    }

    public List<Integer> legacyOrderSlots() {
        return legacyOrderSlots;
    }

    public boolean usesExtensionIds() {
        return !slotsByExtensionId.isEmpty();
    }
}
