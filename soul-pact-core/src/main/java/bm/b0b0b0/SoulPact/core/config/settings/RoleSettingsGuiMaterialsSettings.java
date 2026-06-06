package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RoleSettingsGuiMaterialsSettings {

    @Comment(@CommentValue("Переключатель включён"))
    public String toggleOn = "LIME_DYE";

    @Comment(@CommentValue("Переключатель выключен"))
    public String toggleOff = "GRAY_DYE";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
