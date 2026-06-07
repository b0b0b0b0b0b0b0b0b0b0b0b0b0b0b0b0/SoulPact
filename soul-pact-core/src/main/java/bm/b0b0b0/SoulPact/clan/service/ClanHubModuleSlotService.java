package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
import bm.b0b0b0.SoulPact.core.config.HubModuleSlotMapping;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ClanHubModuleSlotService {

    private final ExtensionRegistryImpl extensionRegistry;
    private final GuiHubConfig guiHubConfig;

    public ClanHubModuleSlotService(ExtensionRegistryImpl extensionRegistry, GuiHubConfig guiHubConfig) {
        this.extensionRegistry = extensionRegistry;
        this.guiHubConfig = guiHubConfig;
    }

    public ClanHubModuleSlotLayout resolve() {
        HubModuleSlotMapping mapping = guiHubConfig.moduleSlotMapping();
        Map<Integer, SoulPactGuiExtension> mapped = new LinkedHashMap<>();
        if (mapping.usesExtensionIds()) {
            assignByExtensionId(mapping, mapped);
        } else {
            assignByRegistrationOrder(mapping, mapped);
        }
        return new ClanHubModuleSlotLayout(Map.copyOf(mapped));
    }

    private void assignByExtensionId(HubModuleSlotMapping mapping, Map<Integer, SoulPactGuiExtension> mapped) {
        Map<String, Integer> slotsByExtensionId = mapping.slotsByExtensionId();
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (!(extension instanceof SoulPactGuiExtension guiExtension)) {
                continue;
            }
            Integer slot = slotsByExtensionId.get(extension.id());
            if (slot == null) {
                continue;
            }
            mapped.put(slot, guiExtension);
        }
    }

    private void assignByRegistrationOrder(HubModuleSlotMapping mapping, Map<Integer, SoulPactGuiExtension> mapped) {
        List<SoulPactGuiExtension> extensions = new ArrayList<>();
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (extension instanceof SoulPactGuiExtension guiExtension) {
                extensions.add(guiExtension);
            }
        }
        List<Integer> slots = mapping.legacyOrderSlots();
        int count = Math.min(extensions.size(), slots.size());
        for (int index = 0; index < count; index++) {
            mapped.put(slots.get(index), extensions.get(index));
        }
    }
}
