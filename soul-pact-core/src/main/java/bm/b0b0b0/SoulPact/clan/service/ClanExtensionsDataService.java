package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.core.config.GuiExtensionsConfig;
import bm.b0b0b0.SoulPact.core.module.ExtensionRegistryImpl;
import java.util.ArrayList;
import java.util.List;

public final class ClanExtensionsDataService {

    private final ExtensionRegistryImpl extensionRegistry;
    private final GuiExtensionsConfig guiExtensionsConfig;

    public ClanExtensionsDataService(
            ExtensionRegistryImpl extensionRegistry,
            GuiExtensionsConfig guiExtensionsConfig
    ) {
        this.extensionRegistry = extensionRegistry;
        this.guiExtensionsConfig = guiExtensionsConfig;
    }

    public ClanExtensionsPage loadPage(int page) {
        int safePage = Math.max(0, page);
        int pageSize = guiExtensionsConfig.pageSize();
        List<SoulPactExtension> allExtensions = new ArrayList<>(extensionRegistry.all());
        int totalExtensions = allExtensions.size();
        if (pageSize <= 0 || totalExtensions == 0) {
            return new ClanExtensionsPage(List.of(), 0, 0, totalExtensions);
        }
        int totalPages = (int) Math.ceil((double) totalExtensions / pageSize);
        int clampedPage = Math.min(safePage, totalPages - 1);
        int offset = clampedPage * pageSize;
        int end = Math.min(offset + pageSize, totalExtensions);
        return new ClanExtensionsPage(
                allExtensions.subList(offset, end),
                clampedPage,
                totalPages,
                totalExtensions
        );
    }
}
