package bm.b0b0b0.SoulPact.land.message;

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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class LandMessages {

    private final JavaPlugin plugin;
    private final String locale;
    private final String fallbackLocale;
    private final Map<String, FileConfiguration> bundles = new HashMap<>();

    public LandMessages(JavaPlugin plugin, String locale, String fallbackLocale) {
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
            plugin.getLogger().severe("Failed to load land lang: " + exception.getMessage());
        }
    }

    public String resolve(Player player, String key) {
        return resolve(player, key, Map.of());
    }

    public String resolve(Player player, String key, Map<String, String> placeholders) {
        return resolveRaw(player, key, placeholders);
    }

    public List<String> resolveList(Player player, String key) {
        return resolveList(player, key, Map.of());
    }

    public List<String> resolveList(Player player, String key, Map<String, String> placeholders) {
        FileConfiguration bundle = bundleFor(player);
        List<String> values = bundle.getStringList(key);
        if (values.isEmpty()) {
            FileConfiguration fallback = bundle(fallbackLocale);
            values = fallback == null ? List.of() : fallback.getStringList(key);
        }
        return applyPlaceholders(values, placeholders);
    }

    public List<String> resolveListDefault(String key, Map<String, String> placeholders) {
        FileConfiguration bundle = bundleForConsole();
        List<String> values = bundle.getStringList(key);
        if (values.isEmpty()) {
            FileConfiguration fallback = bundle(fallbackLocale);
            values = fallback == null ? List.of() : fallback.getStringList(key);
        }
        return applyPlaceholders(values, placeholders);
    }

    public Component component(Player player, String key) {
        return component(player, key, Map.of());
    }

    public Component component(Player player, String key, Map<String, String> placeholders) {
        return LandTextParser.parse(resolve(player, key, placeholders));
    }

    public void send(Player player, String key) {
        send(player, key, Map.of());
    }

    public void send(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(component(player, key, placeholders));
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        if (sender instanceof Player player) {
            send(player, key, placeholders);
            return;
        }
        sender.sendMessage(LandTextParser.parse(resolveDefault(key, placeholders)));
    }

    public String resolveDefault(String key) {
        return resolveDefault(key, Map.of());
    }

    public String resolveDefault(String key, Map<String, String> placeholders) {
        return resolveRaw(null, key, placeholders);
    }

    private String resolveRaw(Player player, String key, Map<String, String> placeholders) {
        FileConfiguration bundle = player == null ? bundleForConsole() : bundleFor(player);
        String value = bundle.getString(key);
        if (value == null) {
            FileConfiguration fallback = bundle(fallbackLocale);
            value = fallback == null ? key : fallback.getString(key, key);
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    private List<String> applyPlaceholders(List<String> lines, Map<String, String> placeholders) {
        return lines.stream().map(line -> {
            String resolved = line;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return resolved;
        }).toList();
    }

    private FileConfiguration bundleFor(Player player) {
        FileConfiguration configured = bundle(locale);
        if (configured != null) {
            return configured;
        }
        FileConfiguration fallback = bundle(fallbackLocale);
        return fallback == null ? new YamlConfiguration() : fallback;
    }

    private FileConfiguration bundleForConsole() {
        return bundleFor(null);
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
            plugin.getLogger().severe("Failed to copy land lang " + fileName + ": " + exception.getMessage());
        }
    }
}
