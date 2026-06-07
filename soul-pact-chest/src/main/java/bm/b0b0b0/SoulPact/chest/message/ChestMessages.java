package bm.b0b0b0.SoulPact.chest.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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

public final class ChestMessages {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private final JavaPlugin plugin;
    private final String locale;
    private final String fallbackLocale;
    private final Map<String, FileConfiguration> bundles = new HashMap<>();

    public ChestMessages(JavaPlugin plugin, String locale, String fallbackLocale) {
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
            plugin.getLogger().severe("Failed to load chest lang: " + exception.getMessage());
        }
    }

    public String resolve(Player player, String key) {
        return resolve(player, key, Map.of());
    }

    public String resolve(Player player, String key, Map<String, String> placeholders) {
        String resolved = resolveRaw(player, key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return resolved;
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
        return values.stream().map(line -> {
            String resolved = line;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                resolved = resolved.replace("{" + entry.getKey() + "}", entry.getValue());
            }
            return resolved;
        }).toList();
    }

    public Component component(Player player, String key, Map<String, String> placeholders) {
        return ChestGuiItems.parse(resolve(player, key, placeholders));
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
        sender.sendMessage(parseLegacy(resolveDefault(key, placeholders)));
    }

    public String resolveDefault(String key, Map<String, String> placeholders) {
        String value = resolveRaw(null, key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    private String resolveRaw(Player player, String key) {
        FileConfiguration bundle = player == null ? bundleForConsole() : bundleFor(player);
        String value = bundle.getString(key);
        if (value == null) {
            FileConfiguration fallback = bundle(fallbackLocale);
            value = fallback == null ? key : fallback.getString(key, key);
        }
        return value;
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
        FileConfiguration configured = bundle(locale);
        if (configured != null) {
            return configured;
        }
        return bundle(fallbackLocale);
    }

    private FileConfiguration bundle(String code) {
        return bundles.get(code);
    }

    private Component parseLegacy(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (input.indexOf('<') >= 0 && input.indexOf('>') > input.indexOf('<')) {
            return MINI_MESSAGE.deserialize(input);
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
            plugin.getLogger().severe("Failed to copy chest lang " + fileName + ": " + exception.getMessage());
        }
    }
}
