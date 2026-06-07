package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class SettingsGuiSlotsSettings {

    @Comment(@CommentValue("Назад в главное меню"))
    public int back = 49;

    @Comment(@CommentValue("Редактор баннера (2-я строка, последняя ячейка)"))
    public int banner = 17;

    @Comment(@CommentValue("Краткое описание клана"))
    public int description = 15;
}
