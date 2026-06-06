package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class PermissionSettings {

    @Comment(@CommentValue("Permission для /clan"))
    public String clanUse = "soulpact.clan.use";

    @Comment(@CommentValue("Permission для /sclan"))
    public String clanAdmin = "soulpact.admin";
}
