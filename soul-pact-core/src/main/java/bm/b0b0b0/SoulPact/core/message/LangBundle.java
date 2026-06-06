package bm.b0b0b0.SoulPact.core.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class LangBundle {

    private final Map<String, String> entries;
    private final Map<String, List<String>> listEntries;

    public static LangBundle empty() {
        return new LangBundle(Collections.emptyMap(), Collections.emptyMap());
    }

    private LangBundle(Map<String, String> entries, Map<String, List<String>> listEntries) {
        this.entries = entries;
        this.listEntries = listEntries;
    }

    public static LangBundle fromConfiguration(FileConfiguration configuration) {
        Map<String, String> entries = new HashMap<>();
        Map<String, List<String>> listEntries = new HashMap<>();
        flatten("", configuration, entries, listEntries);
        return new LangBundle(Collections.unmodifiableMap(entries), Collections.unmodifiableMap(listEntries));
    }

    public String text(String key) {
        return entries.get(key);
    }

    public List<String> list(String key) {
        return listEntries.get(key);
    }

    private static void flatten(
            String prefix,
            ConfigurationSection section,
            Map<String, String> entries,
            Map<String, List<String>> listEntries
    ) {
        for (String key : section.getKeys(false)) {
            String path = prefix.isEmpty() ? key : prefix + "." + key;
            if (section.isConfigurationSection(key)) {
                ConfigurationSection child = section.getConfigurationSection(key);
                if (child != null) {
                    flatten(path, child, entries, listEntries);
                }
                continue;
            }
            if (section.isList(key)) {
                listEntries.put(path, section.getStringList(key));
                continue;
            }
            entries.put(path, section.getString(key, ""));
        }
    }
}
