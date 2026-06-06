package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class MembersGuiSlotsSettings {

    @Comment(@CommentValue("Предыдущая страница"))
    public int previous = 45;

    @Comment(@CommentValue("Назад"))
    public int back = 49;

    @Comment(@CommentValue("Следующая страница"))
    public int next = 53;
}
