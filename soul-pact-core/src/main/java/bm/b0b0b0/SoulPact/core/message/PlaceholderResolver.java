package bm.b0b0b0.SoulPact.core.message;

import java.util.Map;

public final class PlaceholderResolver {

    private PlaceholderResolver() {
    }

    public static String apply(String template, Map<String, String> placeholders) {
        if (template == null) {
            return "";
        }
        if (placeholders == null || placeholders.isEmpty()) {
            return template;
        }
        String resolved = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resolved;
    }
}
