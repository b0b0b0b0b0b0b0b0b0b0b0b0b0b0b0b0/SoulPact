package bm.b0b0b0.SoulPact.leaderboard.config.settings;

import java.util.LinkedHashMap;
import java.util.Map;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Leaderboard — топы кланов: таблички, стойки с головами, голограммы. Тексты — в lang/"))
public final class LeaderboardSettings extends YamlSerializable {

    public LeaderboardSettings() {
        super(LeaderboardSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Период обновления всех бордов (секунд); минимум 30"))
    public int updateIntervalSeconds = 300;

    @Comment(@CommentValue("Обновлять борды по событиям кланов (создание, войны, вступления)"))
    public boolean eventUpdates = true;

    @Comment(@CommentValue("Не чаще, чем раз в N секунд при событиях"))
    public int eventDebounceSeconds = 15;

    @Comment(@CommentValue("Сколько мест держать в кэше топа"))
    public int topSize = 10;

    @NewLine
    @Comment(@CommentValue("Голова лидера клана на стойке"))
    public boolean leaderHead = true;

    @Comment(@CommentValue("Высота голограммы над точкой установки (блоков)"))
    public double hologramYOffset = 2.2;

    @Comment(@CommentValue("Масштаб текста голограммы"))
    public double hologramScale = 1.0;

    @NewLine
    @Comment(@CommentValue("Броня стойки по месту в топе: chestplate,leggings,boots"))
    public Map<String, String> standEquipment = defaultEquipment();

    @Comment(@CommentValue("Броня стойки для остальных мест"))
    public String standEquipmentDefault = "LEATHER_CHESTPLATE,LEATHER_LEGGINGS,LEATHER_BOOTS";

    @NewLine
    @Comment(@CommentValue("Permission-нода админ-команды /clanboard"))
    public String adminPermission = "soulpact.leaderboard.admin";

    private static Map<String, String> defaultEquipment() {
        Map<String, String> equipment = new LinkedHashMap<>();
        equipment.put("1", "DIAMOND_CHESTPLATE,DIAMOND_LEGGINGS,DIAMOND_BOOTS");
        equipment.put("2", "GOLDEN_CHESTPLATE,GOLDEN_LEGGINGS,GOLDEN_BOOTS");
        equipment.put("3", "IRON_CHESTPLATE,IRON_LEGGINGS,IRON_BOOTS");
        return equipment;
    }
}
