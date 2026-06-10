package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.api.placeholder.SoulPactPlaceholderBridge;
import bm.b0b0b0.SoulPact.core.config.LocaleConfig;
import bm.b0b0b0.SoulPact.core.config.PlaceholderConfig;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SoulPactPlaceholderResolver {

    private final PlaceholderConfig placeholderConfig;
    private final LocaleConfig localeConfig;
    private final VaultIntegration vaultIntegration;
    private final SoulPactPlaceholderService placeholderService;
    private final ClanPlaceholderExtrasService extrasService;
    private final List<SoulPactPlaceholderBridge> moduleBridges;

    public SoulPactPlaceholderResolver(
            PlaceholderConfig placeholderConfig,
            LocaleConfig localeConfig,
            VaultIntegration vaultIntegration,
            SoulPactPlaceholderService placeholderService,
            ClanPlaceholderExtrasService extrasService,
            List<SoulPactPlaceholderBridge> moduleBridges
    ) {
        this.placeholderConfig = placeholderConfig;
        this.localeConfig = localeConfig;
        this.vaultIntegration = vaultIntegration;
        this.placeholderService = placeholderService;
        this.extrasService = extrasService;
        this.moduleBridges = List.copyOf(moduleBridges);
    }

    public String resolve(Player player, String params) {
        if (params == null || params.isBlank()) {
            return "";
        }
        for (SoulPactPlaceholderBridge bridge : moduleBridges) {
            String moduleValue = bridge.resolve(player, params);
            if (moduleValue != null) {
                return moduleValue;
            }
        }
        String normalized = params.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("money_") || normalized.startsWith("current_lang")) {
            double balance = playerBalance(player);
            return resolveEconomy(normalized, balance);
        }
        ClanPlaceholderSnapshot snapshot = placeholderService.load(player);
        if (normalized.startsWith("mail")) {
            return resolveMail(player, snapshot, normalized);
        }
        if (normalized.startsWith("setting")) {
            return resolveSetting(snapshot, normalized);
        }
        if (normalized.startsWith("home:")) {
            return resolveHome(snapshot, normalized);
        }
        return resolveClan(player, snapshot, normalized);
    }

    private String resolveEconomy(String params, double balance) {
        return switch (params) {
            case "money_current" -> String.valueOf(balance);
            case "money_current_formated" -> PlaceholderTextUtil.formatMoney(balance);
            case "money_currency" -> currencyName();
            case "current_lang" -> localeConfig.defaultLocale();
            case "current_lang_head" -> Base64.getEncoder().encodeToString(
                    localeConfig.defaultLocale().getBytes(StandardCharsets.UTF_8)
            );
            default -> "";
        };
    }

    private String resolveMail(Player player, ClanPlaceholderSnapshot snapshot, String params) {
        if (!snapshot.hasClan()) {
            return params.startsWith("mail:") ? "" : "0";
        }
        if (params.equals("mail_amount")) {
            return String.valueOf(extrasService.load(snapshot.clanId()).mailTotal());
        }
        if (params.equals("mail_amount_unread")) {
            return String.valueOf(extrasService.unreadMail(snapshot.clanId(), player.getUniqueId()));
        }
        if (params.startsWith("mail:")) {
            ClanPlaceholderExtras extras = extrasService.load(snapshot.clanId());
            return switch (params.substring("mail:".length())) {
                case "last" -> extras.lastMailMessage();
                case "last_sender" -> extras.lastMailSender();
                default -> "";
            };
        }
        return "0";
    }

    private String resolveSetting(ClanPlaceholderSnapshot snapshot, String params) {
        if (params.startsWith("setting_state:")) {
            Boolean value = settingValue(snapshot, params.substring("setting_state:".length()));
            if (value == null) {
                return placeholderConfig.booleanNo();
            }
            return value ? placeholderConfig.booleanYes() : placeholderConfig.booleanNo();
        }
        if (params.startsWith("setting:")) {
            Boolean value = settingValue(snapshot, params.substring("setting:".length()));
            return value == null ? "false" : booleanText(value);
        }
        return "";
    }

    private Boolean settingValue(ClanPlaceholderSnapshot snapshot, String key) {
        if (!snapshot.hasClan()) {
            return null;
        }
        return switch (key) {
            case "ff", "friendly_fire", "friendlyfire" -> snapshot.friendlyFire();
            case "open", "join", "join_requests" -> snapshot.joinOpen();
            case "verified" -> snapshot.verified();
            default -> null;
        };
    }

    private String resolveHome(ClanPlaceholderSnapshot snapshot, String params) {
        if (!snapshot.hasClan()) {
            return params.equals("home:count") ? "0" : "";
        }
        ClanPlaceholderExtras extras = extrasService.load(snapshot.clanId());
        String key = params.substring("home:".length());
        return switch (key) {
            case "count" -> String.valueOf(extras.homeNames().size());
            case "list" -> String.join(placeholderConfig.membersSeparator(), extras.homeNames());
            default -> booleanText(extras.homeNames().stream().anyMatch(name -> name.equalsIgnoreCase(key)));
        };
    }

    private String resolveClan(Player player, ClanPlaceholderSnapshot snapshot, String params) {
        ClanPlaceholderComputed computed = snapshot.computed();
        return switch (params) {
            case "hasclan" -> booleanText(snapshot.hasClan());
            case "hasclan_formated" -> snapshot.hasClan()
                    ? computed.hasClanFormatted()
                    : placeholderConfig.hasClanFormatedNone();
            case "hasclan_formated_levelsup" -> snapshot.hasClan()
                    ? computed.hasClanFormattedLevelUp()
                    : placeholderConfig.hasClanFormatedNone();
            case "hasclan_formated_levelsub" -> snapshot.hasClan()
                    ? computed.hasClanFormattedLevelDown()
                    : placeholderConfig.hasClanFormatedNone();
            case "global_ff", "is_ff" -> booleanText(snapshot.hasClan() && snapshot.friendlyFire());
            case "global_ff_formated", "is_ff_formated" -> snapshot.hasClan()
                    ? computed.friendlyFireFormatted()
                    : placeholderConfig.booleanNo();
            case "isopen" -> booleanText(snapshot.hasClan() && snapshot.joinOpen());
            case "isopen_formated" -> snapshot.hasClan()
                    ? computed.joinOpenFormatted()
                    : placeholderConfig.booleanNo();
            case "tag_nocolor" -> computed.tagNoColor();
            case "tag_color" -> snapshot.tag();
            case "tag_color_levelsup" -> withLevelSymbol(snapshot.tag(), placeholderConfig.levelUpSymbol());
            case "tag_color_levelsub" -> withLevelSymbol(snapshot.tag(), placeholderConfig.levelDownSymbol());
            case "tag_formated" -> snapshot.hasClan() ? computed.tagFormatted() : "";
            case "tag_formated_nocolor" -> snapshot.hasClan() ? computed.tagFormattedNoColor() : "";
            case "tag_formated_levelsup" -> snapshot.hasClan() ? computed.tagFormattedLevelUp() : "";
            case "tag_formated_levelsub" -> snapshot.hasClan() ? computed.tagFormattedLevelDown() : "";
            case "desc_nocolor" -> computed.descNoColor();
            case "desc_color" -> snapshot.description();
            case "leader" -> snapshot.leaderName();
            case "leader_formated" -> snapshot.hasClan() ? computed.leaderFormatted() : "";
            case "creation_date" -> snapshot.hasClan() ? computed.creationDate() : "";
            case "creation_date_formated" -> snapshot.hasClan() ? computed.creationDateFormatted() : "";
            case "clanid" -> snapshot.hasClan() ? String.valueOf(snapshot.clanId()) : "";
            case "clan_kills" -> snapshot.hasClan() ? String.valueOf(snapshot.clanKills()) : "0";
            case "clan_deaths" -> snapshot.hasClan() ? String.valueOf(snapshot.clanDeaths()) : "0";
            case "clan_souls" -> "0";
            case "clan_kdr" -> snapshot.hasClan() ? computed.clanKdr() : "0";
            case "clan_members" -> snapshot.hasClan() ? computed.membersLine() : "";
            case "clan_onlinemembers" -> snapshot.hasClan() ? computed.onlineMembersLine() : "";
            case "count_onlinemembers" -> snapshot.hasClan() ? String.valueOf(snapshot.onlineCount()) : "0";
            case "clan_ally" -> snapshot.hasClan() ? computed.alliesLine() : "";
            case "clan_count_ally" -> snapshot.hasClan() ? String.valueOf(snapshot.allyTags().size()) : "0";
            case "clan_rival" -> placeholderConfig.rivalUnavailable();
            case "clan_count_rival" -> "0";
            case "count_members" -> snapshot.hasClan() ? String.valueOf(snapshot.memberCount()) : "0";
            case "verified" -> booleanText(snapshot.hasClan() && snapshot.verified());
            case "only_verified_tag" -> snapshot.hasClan() && snapshot.verified() ? snapshot.tag() : "";
            case "only_verified_tag_formated" -> snapshot.hasClan() && snapshot.verified()
                    ? computed.verifiedTagFormatted()
                    : "";
            case "count_banned" -> snapshot.hasClan()
                    ? String.valueOf(extrasService.load(snapshot.clanId()).bannedCount())
                    : "0";
            case "banned" -> snapshot.hasClan()
                    ? String.join(placeholderConfig.membersSeparator(), extrasService.load(snapshot.clanId()).bannedNames())
                    : "";
            case "bank_balance" -> snapshot.hasClan() ? String.valueOf(snapshot.bankBalance()) : "0";
            case "bank_balance_formated" -> snapshot.hasClan() ? computed.bankBalanceFormatted() : "0";
            case "level" -> snapshot.hasClan() ? computed.level() : "0";
            case "max_level_reached" -> snapshot.hasClan() ? computed.maxLevelReached() : "false";
            case "levelsup" -> placeholderConfig.levelUpSymbol();
            case "levelsub" -> placeholderConfig.levelDownSymbol();
            case "points" -> snapshot.hasClan() ? PlaceholderTextUtil.formatMoney(snapshot.points()) : "0";
            case "points_unformated" -> snapshot.hasClan() ? String.valueOf(snapshot.points()) : "0";
            case "points_to_nextlevel" -> snapshot.hasClan() ? computed.pointsToNextLevel() : "0";
            case "slots" -> snapshot.hasClan() ? String.valueOf(snapshot.maxSlots()) : "0";
            case "slots_extra" -> String.valueOf(placeholderConfig.extraSlots());
            case "slots_total" -> snapshot.hasClan()
                    ? String.valueOf(snapshot.maxSlots() + placeholderConfig.extraSlots())
                    : String.valueOf(placeholderConfig.extraSlots());
            case "banner" -> snapshot.bannerData();
            case "player_head" -> PlaceholderTextUtil.headTextureBase64(player);
            case "leader_head" -> snapshot.hasClan() && snapshot.leaderId() != null
                    ? PlaceholderTextUtil.headTextureBase64(Bukkit.getPlayer(snapshot.leaderId()))
                    : "";
            case "patent_formated" -> snapshot.hasClan() ? computed.patentFormatted() : "";
            case "patent_name" -> snapshot.hasClan() ? computed.patentName() : "";
            case "clan_member_joined" -> snapshot.hasClan() ? String.valueOf(snapshot.statsJoined()) : "0";
            case "clan_member_leave" -> snapshot.hasClan() ? String.valueOf(snapshot.statsLeave()) : "0";
            case "clan_member_kicked" -> snapshot.hasClan() ? String.valueOf(snapshot.statsKick()) : "0";
            case "clan_war_win" -> snapshot.hasClan() ? String.valueOf(snapshot.warsWon()) : "0";
            case "clan_war_lose" -> snapshot.hasClan() ? String.valueOf(snapshot.warsLost()) : "0";
            default -> "";
        };
    }

    private double playerBalance(Player player) {
        Economy economy = vaultIntegration.economy();
        if (economy == null) {
            return 0.0D;
        }
        return economy.getBalance(player);
    }

    private String currencyName() {
        Economy economy = vaultIntegration.economy();
        if (economy == null) {
            return "";
        }
        return economy.currencyNamePlural();
    }

    private String booleanText(boolean value) {
        return value ? "true" : "false";
    }

    private static String withLevelSymbol(String value, String symbol) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return symbol + value;
    }
}
