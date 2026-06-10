package bm.b0b0b0.SoulPact.gladiator.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class GladiatorGuiSlotsSettings {

    @Comment(@CommentValue("Первый слот списка арен"))
    public int listStart = 10;

    @Comment(@CommentValue("Последний слот списка арен (включительно)"))
    public int listEnd = 16;

    @Comment(@CommentValue("Кнопка «назад в меню клана»"))
    public int back = 22;
}
