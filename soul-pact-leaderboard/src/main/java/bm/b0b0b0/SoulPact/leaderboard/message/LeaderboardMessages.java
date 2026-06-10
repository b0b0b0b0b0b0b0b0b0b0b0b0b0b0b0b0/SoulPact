package bm.b0b0b0.SoulPact.leaderboard.message;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LeaderboardMessages {

    private final JavaPlugin plugin;
    private final String locale;
    private final String fallbackLocale;
    private final Map<String, FileConfiguration> bundles = new HashMap<>();

    public LeaderboardMessages(JavaPlugin plugin, String locale, String fallbackLocale) {
        this.plugin = plugin;
        this.locale = locale;
        this.fallbackLocale = fallbackLocale;
    }

    public void load() {
        bundles.clear();
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        copyDefault("ru.yml", langFolder);
        copyDefault("en.yml", langFolder);
        try (Stream<java.nio.file.Path> paths = Files.list(langFolder.toPath())) {
            paths.filter(path -> path.toString().endsWith(".yml"))
                    .forEach(path -> bundles.put(path.getFileName().toString().replace(".yml", ""), YamlConfiguration.loadConfiguration(path.toFile())));
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to load leaderboard lang: " + exception.getMessage());
        }
    }

    public String resolve(String key, Map<String, String> placeholders) {
        return applyPlaceholders(resolveRaw(key), placeholders);
    }

    public List<String> resolveList(String key, Map<String, String> placeholders) {
        FileConfiguration bundle = primaryBundle();
        List<String> values = bundle.getStringList(key);
        if (values.isEmpty()) {
            FileConfiguration fallback = bundle(fallbackLocale);
            values = fallback == null ? List.of() : fallback.getStringList(key);
        }
        return values.stream().map(line -> applyPlaceholders(line, placeholders)).toList();
    }

    public Component component(String key, Map<String, String> placeholders) {
        return LeaderboardTextParser.parse(resolve(key, placeholders));
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(component(key, placeholders));
    }

    private String applyPlaceholders(String value, Map<String, String> placeholders) {
        String resolved = value;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resolved;
    }

    private String resolveRaw(String key) {
        FileConfiguration bundle = primaryBundle();
        String value = bundle.getString(key);
        if (value == null) {
            FileConfiguration fallback = bundle(fallbackLocale);
            value = fallback == null ? key : fallback.getString(key, key);
        }
        return value;
    }

    private FileConfiguration primaryBundle() {
        FileConfiguration configured = bundle(locale);
        if (configured != null) {
            return configured;
        }
        FileConfiguration fallback = bundle(fallbackLocale);
        return fallback == null ? new YamlConfiguration() : fallback;
    }

    private FileConfiguration bundle(String code) {
        return bundles.get(code);
    }

    private void copyDefault(String fileName, File langFolder) {
        File target = new File(langFolder, fileName);
        if (target.exists()) {
            return;
        }
        try (InputStream stream = plugin.getResource("lang/" + fileName)) {
            if (stream == null) {
                return;
            }
            Files.copy(stream, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to copy leaderboard lang " + fileName + ": " + exception.getMessage());
        }
    }
}
