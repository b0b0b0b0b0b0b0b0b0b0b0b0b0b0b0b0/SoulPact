package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import bm.b0b0b0.SoulPact.api.placeholder.SoulPactPlaceholderBridge;
import org.bukkit.entity.Player;

public final class ExtensionPlaceholderBridge implements SoulPactPlaceholderBridge {

    private final ExtensionRegistry extensionRegistry;

    public ExtensionPlaceholderBridge(ExtensionRegistry extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    @Override
    public String resolve(Player player, String params) {
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (!(extension instanceof SoulPactPlaceholderBridge bridge)) {
                continue;
            }
            String value = bridge.resolve(player, params);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
