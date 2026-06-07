package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class WarHubGuiSlotsSettings {

    @Comment(@CommentValue("Координаты флага противника"))
    public int enemyFlag = 13;

    @Comment(@CommentValue("Входящие объявления (только лидер)"))
    public int pending = 11;

    @Comment(@CommentValue("Назад в профиль клана"))
    public int back = 22;
}
