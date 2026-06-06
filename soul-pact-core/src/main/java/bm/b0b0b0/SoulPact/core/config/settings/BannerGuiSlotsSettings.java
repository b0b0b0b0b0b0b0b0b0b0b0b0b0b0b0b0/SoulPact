package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class BannerGuiSlotsSettings {

    @Comment(@CommentValue("Превью баннера"))
    public int preview = 4;

    @Comment(@CommentValue("Узоры (9 слотов подряд)"))
    public int patternStart = 11;

    @Comment(@CommentValue("Первый ряд базовых цветов (8 слотов)"))
    public int baseColorRowOneStart = 19;

    @Comment(@CommentValue("Второй ряд базовых цветов (8 слотов)"))
    public int baseColorRowTwoStart = 28;

    @Comment(@CommentValue("Сброс узоров"))
    public int clearPatterns = 38;

    @Comment(@CommentValue("Цвет узора"))
    public int patternColor = 40;

    @Comment(@CommentValue("Скопировать из руки"))
    public int fromHand = 42;

    @Comment(@CommentValue("Снять последний узор"))
    public int undoPattern = 43;

    @Comment(@CommentValue("Сохранить"))
    public int save = 44;

    @Comment(@CommentValue("Взять знамя"))
    public int takeStandard = 36;

    @Comment(@CommentValue("Назад"))
    public int back = 49;
}
