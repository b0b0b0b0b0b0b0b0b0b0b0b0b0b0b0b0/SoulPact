package bm.b0b0b0.SoulPact.quests.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class QuestsGuiSlotsSettings {

    @Comment(@CommentValue("Первый слот списка квестов"))
    public int listStart = 10;

    @Comment(@CommentValue("Последний слот списка квестов (включительно)"))
    public int listEnd = 34;

    @Comment(@CommentValue("Кнопка «назад в меню клана»"))
    public int back = 49;
}
