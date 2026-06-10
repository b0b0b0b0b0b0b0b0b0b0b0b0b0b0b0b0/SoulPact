package bm.b0b0b0.SoulPact.clanholo.config.settings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-ClanHolo — голограммы на базе клана. Тексты — в lang/"))
public final class ClanHoloSettings extends YamlSerializable {

    public ClanHoloSettings() {
        super(ClanHoloSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Локаль модуля (файл lang/{locale}.yml)"))
    public String locale = "ru";

    @Comment(@CommentValue("Fallback-локаль"))
    public String fallbackLocale = "en";

    @Comment(@CommentValue("Bukkit permission для /clanholo admin"))
    public String adminPermission = "soulpact.clanholo.admin";

    @Comment(@CommentValue("Клановое право для редактирования (ключ role sync)"))
    public String clanPermissionKey = "land_manage";

    @Comment(@CommentValue("Требовать установку только на базе клана (WorldGuard sp-base-*)"))
    public boolean requireClanBase = true;

    @Comment(@CommentValue("Максимум пользовательских строк (без авто-строки автора)"))
    public int maxLines = 8;

    @Comment(@CommentValue("Максимальная длина одной строки"))
    public int maxLineLength = 64;

    @Comment(@CommentValue("Максимум голограмм на клан по умолчанию"))
    public int maxHologramsDefault = 3;

    @NewLine
    @Comment(@CommentValue("Лимиты по Bukkit-permission (permission: лимит)"))
    public Map<String, Integer> maxHologramsByPermission = defaultLimits();

    @Comment(@CommentValue("Шаблон последней строки (%player% = создатель голограммы)"))
    public String ownerLine = "<gray>Автор: <white>%player%";

    @Comment(@CommentValue("Вертикальный отступ между строками"))
    public double lineSpacing = 0.28;

    @Comment(@CommentValue("Масштаб TextDisplay"))
    public double displayScale = 1.0;

    @Comment(@CommentValue("Радиус поиска ближайшей голограммы для edit/add"))
    public double selectRadius = 6.0;

    @NewLine
    @Comment(@CommentValue("Заблокированные слова и regex (префикс regex:)"))
    public List<String> blockedWords = defaultBlocked();

    @Comment(@CommentValue("Строки шаблона rules по умолчанию (если нет в lang)"))
    public List<String> defaultRulesLines = defaultRules();

    private static Map<String, Integer> defaultLimits() {
        Map<String, Integer> limits = new LinkedHashMap<>();
        limits.put("soulpact.clanholo.limit.5", 5);
        limits.put("soulpact.clanholo.limit.10", 10);
        return limits;
    }

    private static List<String> defaultBlocked() {
        List<String> blocked = new ArrayList<>();
        blocked.add("regex:(?i)https?://\\S+");
        return blocked;
    }

    private static List<String> defaultRules() {
        List<String> rules = new ArrayList<>();
        rules.add("<yellow><bold>Правила клана</bold>");
        rules.add("<gray>1. Уважайте союзников");
        rules.add("<gray>2. Не гриферьте базу");
        rules.add("<gray>3. Слушайтесь лидера");
        return rules;
    }
}
