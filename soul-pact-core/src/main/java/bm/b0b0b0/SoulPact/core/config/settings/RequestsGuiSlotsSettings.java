package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RequestsGuiSlotsSettings {

    @Comment(@CommentValue("Принять всех"))
    public int acceptAll = 0;

    @Comment(@CommentValue("Отклонить всех"))
    public int denyAll = 2;

    @Comment(@CommentValue("Заблокировать всех"))
    public int blockAll = 4;

    @Comment(@CommentValue("Открыть/закрыть заявки (правый верхний угол)"))
    public int toggle = 8;

    @Comment(@CommentValue("Назад в профиль"))
    public int back = 49;
}
