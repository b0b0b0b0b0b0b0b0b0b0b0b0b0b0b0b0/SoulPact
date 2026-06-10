package bm.b0b0b0.SoulPact.core.placeholder;

import bm.b0b0b0.SoulPact.api.placeholder.SoulPactPlaceholderBridge;
import bm.b0b0b0.SoulPact.core.config.LocaleConfig;
import bm.b0b0b0.SoulPact.core.config.PlaceholderConfig;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

public final class SoulPactPlaceholderResolver {

    private final PlaceholderConfig placeholderConfig;
    private final LocaleConfig localeConfig;
    private final VaultIntegration vaultIntegration;
    private final ClanPlaceholderSnapshotLoader snapshotLoader;
    private final List<SoulPactPlaceholderBridge> moduleBridges;

    public SoulPactPlaceholderResolver(
            PlaceholderConfig placeholderConfig,
            LocaleConfig localeConfig,
            VaultIntegration vaultIntegration,
            ClanPlaceholderSnapshotLoader snapshotLoader,
            List<SoulPactPlaceholderBridge> moduleBridges
    ) {
        this.placeholderConfig = placeholderConfig;
        this.localeConfig = localeConfig;
        this.vaultIntegration = vaultIntegration;
        this.snapshotLoader = snapshotLoader;
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
            return resolveEconomy(player, normalized);
        }
        if (normalized.startsWith("mail")) {
            return resolveMail(normalized);
        }
        if (normalized.startsWith("setting")) {
            return resolveSetting(normalized);
        }
        if (normalized.startsWith("home:")) {
            return placeholderConfig.homeUnavailable();
        }
        ClanPlaceholderSnapshot snapshot = snapshotLoader.load(player);
        return resolveClan(player, snapshot, normalized);
    }

    private String resolveEconomy(Player player, String params) {
        return switch (params) {
            case "money_current" -> String.valueOf(playerBalance(player));
            case "money_current_formated" -> PlaceholderTextUtil.formatMoney(playerBalance(player));
            case "money_currency" -> currencyName();
            case "current_lang" -> localeConfig.defaultLocale();
            case "current_lang_head" -> Base64.getEncoder().encodeToString(
                    localeConfig.defaultLocale().getBytes(StandardCharsets.UTF_8)
            );
            default -> "";
        };
    }

    private String resolveMail(String params) {
        if (params.equals("mail_amount") || params.equals("mail_amount_unread")) {
            return placeholderConfig.mailUnavailable();
        }
        if (params.startsWith("mail:")) {
            return "";
        }
        return placeholderConfig.mailUnavailable();
    }

    private String resolveSetting(String params) {
        if (params.startsWith("setting_state:")) {
            return placeholderConfig.booleanNo();
        }
        if (params.startsWith("setting:")) {
            return "false";
        }
        return "";
    }

    private String resolveClan(Player player, ClanPlaceholderSnapshot snapshot, String params) {
        return switch (params) {
            case "hasclan" -> booleanText(snapshot.hasClan());
            case "hasclan_formated" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.applyTemplate(
                    placeholderConfig.hasClanFormated(),
                    Map.of("tag", snapshot.tag(), "name", snapshot.name())
            )
                    : placeholderConfig.hasClanFormatedNone();
            case "hasclan_formated_levelsup" -> withLevelSymbol(
                    resolveClan(player, snapshot, "hasclan_formated"),
                    placeholderConfig.levelUpSymbol()
            );
            case "hasclan_formated_levelsub" -> withLevelSymbol(
                    resolveClan(player, snapshot, "hasclan_formated"),
                    placeholderConfig.levelDownSymbol()
            );
            case "global_ff", "is_ff" -> booleanText(snapshot.hasClan() && snapshot.friendlyFire());
            case "global_ff_formated", "is_ff_formated" -> booleanFormated(snapshot.hasClan() && snapshot.friendlyFire());
            case "isopen" -> booleanText(snapshot.hasClan() && snapshot.joinOpen());
            case "isopen_formated" -> booleanFormated(snapshot.hasClan() && snapshot.joinOpen());
            case "tag_nocolor" -> PlaceholderTextUtil.stripColors(snapshot.tag());
            case "tag_color" -> snapshot.tag();
            case "tag_color_levelsup" -> withLevelSymbol(snapshot.tag(), placeholderConfig.levelUpSymbol());
            case "tag_color_levelsub" -> withLevelSymbol(snapshot.tag(), placeholderConfig.levelDownSymbol());
            case "tag_formated" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.applyTemplate(placeholderConfig.tagFormated(), Map.of("tag", snapshot.tag()))
                    : "";
            case "tag_formated_nocolor" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.applyTemplate(
                    placeholderConfig.tagFormatedNocolor(),
                    Map.of("tag", PlaceholderTextUtil.stripColors(snapshot.tag()))
            )
                    : "";
            case "tag_formated_levelsup" -> withLevelSymbol(resolveClan(player, snapshot, "tag_formated"), placeholderConfig.levelUpSymbol());
            case "tag_formated_levelsub" -> withLevelSymbol(resolveClan(player, snapshot, "tag_formated"), placeholderConfig.levelDownSymbol());
            case "desc_nocolor" -> PlaceholderTextUtil.stripColors(snapshot.description());
            case "desc_color" -> snapshot.description();
            case "leader" -> snapshot.leaderName();
            case "leader_formated" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.applyTemplate(
                    placeholderConfig.leaderFormated(),
                    Map.of("leader", snapshot.leaderName())
            )
                    : "";
            case "creation_date" -> snapshot.hasClan() ? PlaceholderTextUtil.formatDate(snapshot.createdAt()) : "";
            case "creation_date_formated" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.applyTemplate(
                    placeholderConfig.creationDateFormated(),
                    Map.of("date", PlaceholderTextUtil.formatDate(snapshot.createdAt()))
            )
                    : "";
            case "clanid" -> snapshot.hasClan() ? String.valueOf(snapshot.clanId()) : "";
            case "clan_kills" -> snapshot.hasClan() ? String.valueOf(snapshot.clanKills()) : "0";
            case "clan_deaths" -> snapshot.hasClan() ? String.valueOf(snapshot.clanDeaths()) : "0";
            case "clan_souls" -> "0";
            case "clan_kdr" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.formatKdr(snapshot.clanKills(), snapshot.clanDeaths())
                    : "0";
            case "clan_members" -> snapshot.hasClan() ? String.join(placeholderConfig.membersSeparator(), snapshot.memberNames()) : "";
            case "clan_onlinemembers" -> snapshot.hasClan()
                    ? String.join(placeholderConfig.membersSeparator(), snapshot.onlineMemberNames())
                    : "";
            case "count_onlinemembers" -> snapshot.hasClan() ? String.valueOf(snapshot.onlineCount()) : "0";
            case "clan_ally" -> snapshot.hasClan() ? String.join(placeholderConfig.alliesSeparator(), snapshot.allyTags()) : "";
            case "clan_count_ally" -> snapshot.hasClan() ? String.valueOf(snapshot.allyTags().size()) : "0";
            case "clan_rival" -> placeholderConfig.rivalUnavailable();
            case "clan_count_rival" -> "0";
            case "count_members" -> snapshot.hasClan() ? String.valueOf(snapshot.memberCount()) : "0";
            case "verified" -> booleanText(snapshot.hasClan() && snapshot.verified());
            case "only_verified_tag" -> snapshot.hasClan() && snapshot.verified() ? snapshot.tag() : "";
            case "only_verified_tag_formated" -> snapshot.hasClan() && snapshot.verified()
                    ? PlaceholderTextUtil.applyTemplate(
                    placeholderConfig.verifiedTagFormated(),
                    Map.of("tag", snapshot.tag())
            )
                    : "";
            case "count_banned" -> "0";
            case "banned" -> "";
            case "bank_balance" -> snapshot.hasClan() ? String.valueOf(snapshot.bankBalance()) : "0";
            case "bank_balance_formated" -> snapshot.hasClan()
                    ? PlaceholderTextUtil.formatMoney(snapshot.bankBalance())
                    : "0";
            case "level" -> snapshot.hasClan()
                    ? String.valueOf(PlaceholderTextUtil.clanLevel(
                    snapshot.points(),
                    placeholderConfig.pointsPerLevel(),
                    placeholderConfig.maxClanLevel()
            ))
                    : "0";
            case "max_level_reached" -> snapshot.hasClan() && PlaceholderTextUtil.clanLevel(
                    snapshot.points(),
                    placeholderConfig.pointsPerLevel(),
                    placeholderConfig.maxClanLevel()
            ) >= placeholderConfig.maxClanLevel() ? "true" : "false";
            case "levelsup" -> placeholderConfig.levelUpSymbol();
            case "levelsub" -> placeholderConfig.levelDownSymbol();
            case "points" -> snapshot.hasClan() ? PlaceholderTextUtil.formatMoney(snapshot.points()) : "0";
            case "points_unformated" -> snapshot.hasClan() ? String.valueOf(snapshot.points()) : "0";
            case "points_to_nextlevel" -> snapshot.hasClan()
                    ? String.valueOf(PlaceholderTextUtil.pointsToNextLevel(
                    snapshot.points(),
                    placeholderConfig.pointsPerLevel(),
                    placeholderConfig.maxClanLevel()
            ))
                    : "0";
            case "slots" -> snapshot.hasClan() ? String.valueOf(snapshot.maxSlots()) : "0";
            case "slots_extra" -> String.valueOf(placeholderConfig.extraSlots());
            case "slots_total" -> snapshot.hasClan()
                    ? String.valueOf(snapshot.maxSlots() + placeholderConfig.extraSlots())
                    : String.valueOf(placeholderConfig.extraSlots());
            case "banner" -> snapshot.bannerData();
            case "player_head" -> PlaceholderTextUtil.headTextureBase64(player);
            case "leader_head" -> snapshot.hasClan() && snapshot.leaderId() != null
                    ? PlaceholderTextUtil.headTextureBase64(
                    org.bukkit.Bukkit.getPlayer(snapshot.leaderId())
            )
                    : "";
            case "patent_formated" -> snapshot.hasClan()
                    ? snapshotLoader.patentFormatted(snapshot.memberRole())
                    : "";
            case "patent_name" -> snapshot.hasClan()
                    ? snapshotLoader.patentName(snapshot.memberRole())
                    : "";
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

    private String booleanFormated(boolean value) {
        return value ? placeholderConfig.booleanYes() : placeholderConfig.booleanNo();
    }

    private static String withLevelSymbol(String value, String symbol) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return symbol + value;
    }
}
