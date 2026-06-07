package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class WarPendingListGuiSettings {

    @Comment(@CommentValue("Строк инвентаря"))
    public int rows = 6;

    @Comment(@CommentValue("Слотов на странице (без навигации)"))
    public int pageSize = 45;

    @NewLine
    public WarPendingListSlotsSettings slots = new WarPendingListSlotsSettings();
}
