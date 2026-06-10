package bm.b0b0b0.SoulPact.core.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPactPlaceholderExpansion extends PlaceholderExpansion {

    private final JavaPlugin plugin;
    private final SoulPactPlaceholderService placeholderService;

    public SoulPactPlaceholderExpansion(JavaPlugin plugin, SoulPactPlaceholderService placeholderService) {
        this.plugin = plugin;
        this.placeholderService = placeholderService;
    }

    @Override
    public String getIdentifier() {
        return "spact";
    }

    @Override
    public String getAuthor() {
        return "b0b0b0";
    }

    @Override
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        String value = placeholderService.resolve(player, params);
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (value.indexOf('<') >= 0) {
            return PlaceholderTextUtil.toLegacyDisplay(value);
        }
        return value;
    }
}
