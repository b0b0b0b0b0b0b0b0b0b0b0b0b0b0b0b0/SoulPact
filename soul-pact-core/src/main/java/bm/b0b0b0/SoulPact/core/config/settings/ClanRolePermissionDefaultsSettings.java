package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class ClanRolePermissionDefaultsSettings {

    @NewLine
    @Comment(@CommentValue("Заместитель (deputy)"))
    public ClanRolePermissionFlagsSettings deputy = deputyDefaults();

    @NewLine
    @Comment(@CommentValue("Капитан (officer)"))
    public ClanRolePermissionFlagsSettings officer = officerDefaults();

    @NewLine
    @Comment(@CommentValue("Рядовой (member)"))
    public ClanRolePermissionFlagsSettings member = memberDefaults();

    private static ClanRolePermissionFlagsSettings deputyDefaults() {
        ClanRolePermissionFlagsSettings settings = new ClanRolePermissionFlagsSettings();
        settings.kick = true;
        settings.accept = true;
        settings.recruitLower = true;
        return settings;
    }

    private static ClanRolePermissionFlagsSettings officerDefaults() {
        ClanRolePermissionFlagsSettings settings = new ClanRolePermissionFlagsSettings();
        settings.accept = true;
        return settings;
    }

    private static ClanRolePermissionFlagsSettings memberDefaults() {
        return new ClanRolePermissionFlagsSettings();
    }
}
