package bm.b0b0b0.SoulPact.core.integration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class IntegrationRegistry {

    private final Map<String, PluginIntegration> integrations = new LinkedHashMap<>();

    public void register(PluginIntegration integration) {
        integrations.put(integration.id(), integration);
    }

    public void hookAll() {
        for (PluginIntegration integration : integrations.values()) {
            integration.hook();
        }
    }

    public Collection<PluginIntegration> all() {
        return integrations.values();
    }

    public Optional<PluginIntegration> find(String id) {
        return Optional.ofNullable(integrations.get(id));
    }

    public boolean isAvailable(String id) {
        return find(id).map(PluginIntegration::available).orElse(false);
    }

    public List<PluginIntegration> availableIntegrations() {
        List<PluginIntegration> result = new ArrayList<>();
        for (PluginIntegration integration : integrations.values()) {
            if (integration.available()) {
                result.add(integration);
            }
        }
        return result;
    }
}
