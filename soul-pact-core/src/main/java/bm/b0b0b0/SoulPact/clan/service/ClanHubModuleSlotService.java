package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.core.config.GuiHubConfig;
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
        List<SoulPactGuiExtension> extensions = new ArrayList<>();
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (extension instanceof SoulPactGuiExtension guiExtension) {
                extensions.add(guiExtension);
            }
        }
        List<Integer> slots = guiHubConfig.moduleSlots();
        Map<Integer, SoulPactGuiExtension> mapped = new LinkedHashMap<>();
        int count = Math.min(extensions.size(), slots.size());
        for (int index = 0; index < count; index++) {
            mapped.put(slots.get(index), extensions.get(index));
        }
        return new ClanHubModuleSlotLayout(Map.copyOf(mapped));
    }
}
