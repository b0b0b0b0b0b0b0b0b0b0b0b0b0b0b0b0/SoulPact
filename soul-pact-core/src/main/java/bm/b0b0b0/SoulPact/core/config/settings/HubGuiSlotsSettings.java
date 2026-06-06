package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class HubGuiSlotsSettings {

    @Comment(@CommentValue("Звезда — статистика и список кланов"))
    public int overview = 4;

    @Comment(@CommentValue("Профиль игрока в клане"))
    public int profile = 22;

    @Comment(@CommentValue("Настройки клана"))
    public int settings = 24;

    @Comment(@CommentValue("Баннер клана (ряд с профилем)"))
    public int banner = 20;

    @Comment(@CommentValue("Создать клан (под головой игрока)"))
    public int create = 31;

    @Comment(@CommentValue("Справка (нижний ряд, справа)"))
    public int help = 49;
}
