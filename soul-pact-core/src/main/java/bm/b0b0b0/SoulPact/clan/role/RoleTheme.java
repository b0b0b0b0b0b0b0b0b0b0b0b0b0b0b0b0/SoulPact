package bm.b0b0b0.SoulPact.clan.role;

import java.util.List;

public final class RoleTheme {

    private final List<String> order;
    private final List<RoleDefinition> definitions;

    public RoleTheme(List<String> order, List<RoleDefinition> definitions) {
        this.order = List.copyOf(order);
        this.definitions = List.copyOf(definitions);
    }

    public List<String> order() {
        return order;
    }

    public List<RoleDefinition> definitions() {
        return definitions;
    }

    public RoleDefinition definition(String roleKey) {
        for (RoleDefinition definition : definitions) {
            if (definition.key().equals(roleKey)) {
                return definition;
            }
        }
        return null;
    }
}
