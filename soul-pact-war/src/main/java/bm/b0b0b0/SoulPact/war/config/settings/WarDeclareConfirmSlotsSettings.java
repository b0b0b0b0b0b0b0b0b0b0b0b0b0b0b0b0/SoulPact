package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class WarDeclareConfirmSlotsSettings {

    @Comment(@CommentValue("Подтвердить объявление"))
    public int confirm = 11;

    @Comment(@CommentValue("Отмена"))
    public int deny = 15;
}
