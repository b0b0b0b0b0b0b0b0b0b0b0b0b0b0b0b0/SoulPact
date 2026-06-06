package bm.b0b0b0.SoulPact.core.message;

import bm.b0b0b0.SoulPact.api.message.SoulPactMessages;
import bm.b0b0b0.SoulPact.core.config.LocaleConfig;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MessageService implements SoulPactMessages {

    private final JavaPlugin plugin;
    private final LocaleConfig localeConfig;
    private final Map<String, LangBundle> bundles = new HashMap<>();

    public MessageService(JavaPlugin plugin, LocaleConfig localeConfig) {
        this.plugin = plugin;
        this.localeConfig = localeConfig;
    }

    public void load() {
        bundles.clear();
        File langFolder = langFolder();
        ensureLangFolder(langFolder);
        copyDefaultLangFiles(langFolder);
        try (Stream<java.nio.file.Path> paths = Files.list(langFolder.toPath())) {
            paths.filter(path -> path.toString().endsWith(".yml"))
                    .forEach(path -> loadBundle(langFolder, path.getFileName().toString()));
        } catch (IOException exception) {
            plugin.getLogger().severe("Failed to load lang folder: " + exception.getMessage());
        }
    }

    public void reload() {
        load();
    }

    public List<String> resolveList(Player player, String key) {
        LangBundle bundle = bundleFor(player);
        List<String> values = bundle.list(key);
        if (values != null) {
            return values;
        }
        LangBundle fallback = bundle(localeConfig.fallbackLocale());
        return fallback == null ? List.of() : fallback.list(key);
    }

    public List<String> resolveList(Player player, String key, Map<String, String> placeholders) {
        List<String> lines = resolveList(player, key);
        if (lines.isEmpty()) {
            return lines;
        }
        return lines.stream()
                .map(line -> PlaceholderResolver.apply(line, placeholders))
                .toList();
    }

    @Override
    public String resolve(Player player, String key) {
        return resolve(player, key, Map.of());
    }

    @Override
    public String resolve(Player player, String key, Map<String, String> placeholders) {
        String template = resolveRaw(player, key);
        return PlaceholderResolver.apply(template, placeholders);
    }

    @Override
    public String resolveDefault(String key) {
        return resolveDefault(key, Map.of());
    }

    @Override
    public String resolveDefault(String key, Map<String, String> placeholders) {
        LangBundle bundle = bundle(localeConfig.defaultLocale());
        String template = bundle == null ? key : valueOrKey(bundle, key);
        return PlaceholderResolver.apply(template, placeholders);
    }

    @Override
    public void send(CommandSender sender, String key) {
        send(sender, key, Map.of());
    }

    @Override
    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        if (sender instanceof Player player) {
            send(player, key, placeholders);
            return;
        }
        Component component = parse(resolveDefault(key, placeholders));
        sender.sendMessage(component);
    }

    @Override
    public void send(Player player, String key) {
        send(player, key, Map.of());
    }

    @Override
    public void send(Player player, String key, Map<String, String> placeholders) {
        Component component = parse(resolve(player, key, placeholders));
        player.sendMessage(component);
    }

    public Component component(Player player, String key) {
        return parse(resolve(player, key));
    }

    public Component component(Player player, String key, Map<String, String> placeholders) {
        return parse(resolve(player, key, placeholders));
    }

    public void sendCreatePrompt(Player player, String suggestCommand, String reopenCommand) {
        Component suggest = parse(resolve(player, "clan.gui.hub.create.prompt-suggest"))
                .clickEvent(ClickEvent.suggestCommand(suggestCommand));
        Component separator = parse(resolve(player, "clan.gui.hub.create.prompt-separator"));
        Component cancel = parse(resolve(player, "clan.gui.hub.create.prompt-cancel"))
                .clickEvent(ClickEvent.runCommand(reopenCommand));
        player.sendMessage(suggest.append(separator).append(cancel));
    }

    public void sendSuggestLine(Player player, String labelKey, String command) {
        sendSuggestLine(player, labelKey, Map.of(), command);
    }

    public void sendSuggestLine(Player player, String labelKey, Map<String, String> placeholders, String command) {
        Component line = parse(resolve(player, labelKey, placeholders))
                .clickEvent(ClickEvent.suggestCommand(command));
        player.sendMessage(line);
    }

    public void sendRunLine(Player player, String labelKey, String command) {
        Component line = parse(resolve(player, labelKey)).clickEvent(ClickEvent.runCommand(command));
        player.sendMessage(line);
    }

    public void sendRawLine(Player player, String line) {
        player.sendMessage(parse(line));
    }

    private static Component parse(String input) {
        return AdventureTextParser.parse(input);
    }

    private String resolveRaw(Player player, String key) {
        LangBundle primary = bundleFor(player);
        String value = valueOrNull(primary, key);
        if (value != null) {
            return value;
        }
        LangBundle fallback = bundle(localeConfig.fallbackLocale());
        if (fallback != null) {
            value = valueOrNull(fallback, key);
            if (value != null) {
                return value;
            }
        }
        return key;
    }

    private LangBundle bundleFor(Player player) {
        LangBundle bundle = bundle(localeConfig.defaultLocale());
        return bundle == null ? LangBundle.empty() : bundle;
    }

    private LangBundle bundle(String locale) {
        return bundles.get(normalizeLocale(locale));
    }

    private static String valueOrNull(LangBundle bundle, String key) {
        String value = bundle.text(key);
        return value == null || value.isEmpty() ? null : value;
    }

    private static String valueOrKey(LangBundle bundle, String key) {
        String value = bundle.text(key);
        return value == null ? key : value;
    }

    private File langFolder() {
        return new File(plugin.getDataFolder(), "lang");
    }

    private void ensureLangFolder(File langFolder) {
        if (!langFolder.exists() && !langFolder.mkdirs()) {
            plugin.getLogger().severe("Failed to create lang folder.");
        }
    }

    private void copyDefaultLangFiles(File langFolder) {
        copyDefaultLangFile(langFolder, "en.yml");
        copyDefaultLangFile(langFolder, "ru.yml");
    }

    private void copyDefaultLangFile(File langFolder, String fileName) {
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
            plugin.getLogger().severe("Failed to copy lang file " + fileName + ": " + exception.getMessage());
        }
    }

    private void loadBundle(File langFolder, String fileName) {
        File file = new File(langFolder, fileName);
        if (!file.exists()) {
            return;
        }
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        String locale = fileName.substring(0, fileName.length() - 4);
        bundles.put(normalizeLocale(locale), LangBundle.fromConfiguration(configuration));
    }

    private static String normalizeLocale(String locale) {
        return locale.toLowerCase();
    }
}
