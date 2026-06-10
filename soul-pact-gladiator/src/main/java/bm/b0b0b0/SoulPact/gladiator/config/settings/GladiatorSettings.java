package bm.b0b0b0.SoulPact.gladiator.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Gladiator — PvP-ивент для кланов. Тексты — в lang/"))
public final class GladiatorSettings extends YamlSerializable {

    public GladiatorSettings() {
        super(GladiatorSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Permission-нода админ-команды /clanglad"))
    public String adminPermission = "soulpact.gladiator.admin";

    @NewLine
    @Comment(@CommentValue("Отсчёт в лобби перед боем (секунд)"))
    public int lobbyCountdownSeconds = 60;

    @Comment(@CommentValue("Минимум кланов для старта боя"))
    public int minClans = 2;

    @Comment(@CommentValue("Проверка границ арены (секунд); 0 — отключить"))
    public int boundsCheckSeconds = 2;

    @Comment(@CommentValue("Проверка расписаний (секунд)"))
    public int scheduleCheckSeconds = 30;

    @Comment(@CommentValue("Кэш «игрок → клан» для плейсхолдеров (секунд)"))
    public int playerClanCacheSeconds = 60;

    @NewLine
    @Comment(@CommentValue("Предмет-жезл для выделения региона арены"))
    public String wandMaterial = "BLAZE_ROD";

    @NewLine
    @Comment(@CommentValue("Звуки ивента"))
    public GladiatorSoundSettings sounds = new GladiatorSoundSettings();

    @NewLine
    @Comment(@CommentValue("Инвентарь списка арен"))
    public GladiatorGuiSettings gui = new GladiatorGuiSettings();
}
