package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RequestDetailGuiSlotsSettings {

    @Comment(@CommentValue("Карточка заявителя"))
    public int player = 4;

    @Comment(@CommentValue("Принять"))
    public int accept = 11;

    @Comment(@CommentValue("Отклонить"))
    public int deny = 13;

    @Comment(@CommentValue("Заблокировать"))
    public int block = 15;

    @Comment(@CommentValue("Назад к списку"))
    public int back = 22;
}
