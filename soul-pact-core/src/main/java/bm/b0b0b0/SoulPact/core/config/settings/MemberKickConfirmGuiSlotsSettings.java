package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class MemberKickConfirmGuiSlotsSettings {

    @Comment(@CommentValue("Подтвердить выгон"))
    public int confirm = 11;

    @Comment(@CommentValue("Отмена"))
    public int deny = 15;
}
