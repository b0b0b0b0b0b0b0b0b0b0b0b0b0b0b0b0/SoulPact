package bm.b0b0b0.SoulPact.core.placeholder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import bm.b0b0b0.SoulPact.core.message.AdventureTextParser;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class PlaceholderTextUtil {

    private static final String TEXTURES_PROPERTY = "textures";
    private static final Pattern LEGACY_COLOR = Pattern.compile("§[0-9a-fk-orA-FK-OR]");
    private static final Pattern HEX_COLOR = Pattern.compile("<#[0-9A-Fa-f]{6}>");
    private static final Pattern MINI_TAG = Pattern.compile("</?[a-zA-Z#0-9]+>");
    private static final DecimalFormat MONEY = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private PlaceholderTextUtil() {
    }

    public static String stripColors(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        String stripped = HEX_COLOR.matcher(input).replaceAll("");
        stripped = MINI_TAG.matcher(stripped).replaceAll("");
        stripped = LEGACY_COLOR.matcher(stripped).replaceAll("");
        return stripped;
    }

    public static String formatMoney(double amount) {
        return MONEY.format(amount);
    }

    public static String formatDate(long epochMillis) {
        if (epochMillis <= 0L) {
            return "-";
        }
        return DATE.format(Instant.ofEpochMilli(epochMillis));
    }

    public static String applyTemplate(String template, Map<String, String> values) {
        String result = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    public static String toLegacyDisplay(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return AdventureTextParser.toLegacyString(input);
    }

    public static String formatKdr(int kills, int deaths) {
        if (deaths <= 0) {
            return kills <= 0 ? "0" : String.valueOf(kills);
        }
        double ratio = (double) kills / (double) deaths;
        return String.format(Locale.US, "%.2f", ratio);
    }

    public static int clanLevel(int points, int pointsPerLevel, int maxLevel) {
        if (pointsPerLevel <= 0) {
            return 1;
        }
        int level = Math.max(1, points / pointsPerLevel + 1);
        return Math.min(maxLevel, level);
    }

    public static int pointsToNextLevel(int points, int pointsPerLevel, int maxLevel) {
        int level = clanLevel(points, pointsPerLevel, maxLevel);
        if (level >= maxLevel) {
            return 0;
        }
        int nextThreshold = level * pointsPerLevel;
        return Math.max(0, nextThreshold - points);
    }

    public static String headTextureBase64(Player player) {
        if (player == null) {
            return "";
        }
        PlayerProfile profile = player.getPlayerProfile();
        if (!profile.hasProperty(TEXTURES_PROPERTY)) {
            return "";
        }
        for (ProfileProperty property : profile.getProperties()) {
            if (TEXTURES_PROPERTY.equals(property.getName())) {
                return property.getValue();
            }
        }
        return "";
    }

    public static String resolvePlayerName(UUID playerId) {
        if (playerId == null) {
            return "";
        }
        Player online = Bukkit.getPlayer(playerId);
        if (online != null) {
            return profileName(online.getPlayerProfile(), playerId);
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerId);
        return profileName(offline.getPlayerProfile(), playerId);
    }

    private static String profileName(PlayerProfile profile, UUID fallbackId) {
        if (profile == null) {
            return fallbackId.toString().substring(0, 8);
        }
        String name = profile.getName();
        if (name == null || name.isBlank()) {
            return fallbackId.toString().substring(0, 8);
        }
        return name;
    }
}
