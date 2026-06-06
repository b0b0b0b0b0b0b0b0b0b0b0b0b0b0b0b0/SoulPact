package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RequestDetailGuiMaterialsSettings {

    @Comment(@CommentValue("Принять"))
    public String accept = "LIME_CONCRETE";

    @Comment(@CommentValue("Отклонить"))
    public String deny = "RED_CONCRETE";

    @Comment(@CommentValue("Заблокировать"))
    public String block = "PURPLE_CONCRETE";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
