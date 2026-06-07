package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ClanStandardSettings {

    @Comment(@CommentValue("ПКМ по установленному знамени открывает меню хаба клана"))
    public boolean openHubOnInteract = true;

    @Comment(@CommentValue("Только участники клана этого знамени могут открыть хаб"))
    public boolean requireClanMember = true;
}
