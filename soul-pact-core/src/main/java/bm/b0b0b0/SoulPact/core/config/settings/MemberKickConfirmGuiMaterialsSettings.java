package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class MemberKickConfirmGuiMaterialsSettings {

    @Comment(@CommentValue("Подтвердить"))
    public String confirm = "LIME_CONCRETE";

    @Comment(@CommentValue("Отмена"))
    public String deny = "RED_CONCRETE";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
