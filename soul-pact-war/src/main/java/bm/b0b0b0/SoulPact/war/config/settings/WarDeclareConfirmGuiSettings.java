package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class WarDeclareConfirmGuiSettings {

    @Comment(@CommentValue("Строк инвентаря"))
    public int rows = 3;

    @NewLine
    public WarDeclareConfirmSlotsSettings slots = new WarDeclareConfirmSlotsSettings();
}
