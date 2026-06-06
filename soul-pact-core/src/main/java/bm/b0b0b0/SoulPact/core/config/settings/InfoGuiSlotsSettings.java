package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class InfoGuiSlotsSettings {

    @Comment(@CommentValue("Карточка клана"))
    public int clanCard = 11;

    @Comment(@CommentValue("Вступить / покинуть клан"))
    public int action = 15;

    @Comment(@CommentValue("Состав клана"))
    public int members = 13;

    @Comment(@CommentValue("Назад к списку"))
    public int back = 22;
}
