package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class MemberDetailGuiSlotsSettings {

    @Comment(@CommentValue("Карточка участника"))
    public int player = 13;

    @Comment(@CommentValue("Назад к составу"))
    public int back = 49;
}
