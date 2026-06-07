package bm.b0b0b0.SoulPact.coalition.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public final class CoalitionMessages {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final JavaPlugin plugin;
    private final String locale;
    private final String fallbackLocale;
    private final Map<String, FileConfiguration> bundles = new HashMap<>();

    public CoalitionMessages(JavaPlugin plugin, String locale, String fallbackLocale) {
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
                    .forEach(path -> bundles.put(
                            path.getFileName().toString().replace(".yml", ""),
                            YamlConfiguration.loadConfiguration(path.toFile())
                    ));
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to load coalition lang: " + exception.getMessage());
        }
    }

    public void send(Player player, String key) {
        send(player, key, Map.of());
    }

    public void send(Player player, String key, Map<String, String> placeholders) {
        player.sendMessage(component(player, key, placeholders));
    }

    public Component component(Player player, String key, Map<String, String> placeholders) {
        return parse(resolve(player, key, placeholders));
    }

    public Component component(Player player, String key) {
        return component(player, key, Map.of());
    }

    public List<String> resolveList(Player player, String key, Map<String, String> placeholders) {
        FileConfiguration bundle = player == null ? bundle(fallbackLocale) : bundleFor(player);
        List<String> lines = bundle.getStringList(key);
        if (lines.isEmpty()) {
            FileConfiguration fallback = bundle(fallbackLocale);
            lines = fallback == null ? List.of() : fallback.getStringList(key);
        }
        return lines.stream().map(line -> {
            String resolved = line;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return resolved;
        }).toList();
    }

    public String resolve(Player player, String key, Map<String, String> placeholders) {
        String value = resolveRaw(player, key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    public String resolveDefault(String key, Map<String, String> placeholders) {
        String value = resolveRaw(null, key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    private String resolveRaw(Player player, String key) {
        FileConfiguration bundle = player == null ? bundle(fallbackLocale) : bundleFor(player);
        String value = bundle.getString(key);
        if (value == null) {
            FileConfiguration fallback = bundle(fallbackLocale);
            value = fallback == null ? key : fallback.getString(key, key);
        }
        return value;
    }

    private FileConfiguration bundleFor(Player player) {
        FileConfiguration configured = bundle(locale);
        return configured == null ? bundle(fallbackLocale) : configured;
    }

    private FileConfiguration bundle(String code) {
        return bundles.get(code);
    }

    private Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (input.indexOf('<') >= 0 && input.indexOf('>') > input.indexOf('<')) {
            return MINI.deserialize(input);
        }
        return LEGACY.deserialize(input);
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
            plugin.getLogger().severe("Failed to copy coalition lang " + fileName + ": " + exception.getMessage());
        }
    }
}
