package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RoleSettingsGuiSlotsSettings {

    @Comment(@CommentValue("Выгонять участников"))
    public int kick = 11;

    @Comment(@CommentValue("Принимать участников"))
    public int accept = 13;

    @Comment(@CommentValue("Нанимать нижних чинов"))
    public int recruitLower = 15;

    @Comment(@CommentValue("Назад"))
    public int back = 31;
}
