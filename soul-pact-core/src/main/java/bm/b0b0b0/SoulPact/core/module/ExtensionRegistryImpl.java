package bm.b0b0b0.SoulPact.core.module;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ExtensionRegistryImpl implements ExtensionRegistry {

    private final Map<String, SoulPactExtension> extensions = new LinkedHashMap<>();

    @Override
    public void register(SoulPactExtension extension) {
        extensions.put(extension.id(), extension);
    }

    @Override
    public void unregister(String extensionId) {
        SoulPactExtension extension = extensions.remove(extensionId);
        if (extension != null) {
            extension.disable();
        }
    }

    @Override
    public Optional<SoulPactExtension> find(String extensionId) {
        return Optional.ofNullable(extensions.get(extensionId));
    }

    @Override
    public Collection<SoulPactExtension> all() {
        return extensions.values();
    }

    @Override
    public void reloadAll() {
        for (SoulPactExtension extension : extensions.values()) {
            extension.reload();
        }
    }

    public void disableAll() {
        for (SoulPactExtension extension : extensions.values()) {
            extension.disable();
        }
        extensions.clear();
    }
}
