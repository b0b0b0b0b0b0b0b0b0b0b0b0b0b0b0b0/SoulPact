package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class WarPendingDetailSlotsSettings {

    @Comment(@CommentValue("Принять войну"))
    public int accept = 11;

    @Comment(@CommentValue("Выкуп"))
    public int ransom = 13;

    @Comment(@CommentValue("Назад"))
    public int back = 22;
}
