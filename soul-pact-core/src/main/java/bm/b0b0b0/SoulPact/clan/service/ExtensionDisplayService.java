package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ExtensionDisplayService {

    private final MessageService messageService;

    public ExtensionDisplayService(MessageService messageService) {
        this.messageService = messageService;
    }

    public String displayName(Player player, String extensionId) {
        String moduleKey = "clan.gui.extensions.modules." + extensionId + ".display-name";
        String resolved = messageService.resolve(player, moduleKey);
        if (!resolved.equals(moduleKey)) {
            return resolved;
        }
        return messageService.resolve(player, "clan.gui.extensions.item.fallback-name", Map.of("id", extensionId));
    }

    public List<String> lore(Player player, String extensionId, String displayName) {
        String moduleLoreKey = "clan.gui.extensions.modules." + extensionId + ".lore";
        List<String> moduleLore = messageService.resolveList(player, moduleLoreKey);
        if (!moduleLore.isEmpty()) {
            return moduleLore;
        }
        return messageService.resolveList(player, "clan.gui.extensions.item.entry.lore", Map.of(
                "id", extensionId,
                "display_name", displayName
        ));
    }
}
