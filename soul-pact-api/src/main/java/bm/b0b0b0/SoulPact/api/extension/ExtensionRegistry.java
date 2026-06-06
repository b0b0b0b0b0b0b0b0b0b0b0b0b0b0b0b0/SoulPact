package bm.b0b0b0.SoulPact.api.extension;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import java.util.Collection;
import java.util.Optional;

public interface ExtensionRegistry {

    void register(SoulPactExtension extension);

    void unregister(String extensionId);

    Optional<SoulPactExtension> find(String extensionId);

    Collection<SoulPactExtension> all();

    void reloadAll();
}
