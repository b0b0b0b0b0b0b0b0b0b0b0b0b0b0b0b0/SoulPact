package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class MemberDetailGuiMaterialsSettings {

    @Comment(@CommentValue("Назначить роль"))
    public String assignRole = "WRITABLE_BOOK";

    @Comment(@CommentValue("Передать клан"))
    public String transfer = "GOLD_INGOT";

    @Comment(@CommentValue("Выгнать из клана"))
    public String kick = "BARRIER";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
