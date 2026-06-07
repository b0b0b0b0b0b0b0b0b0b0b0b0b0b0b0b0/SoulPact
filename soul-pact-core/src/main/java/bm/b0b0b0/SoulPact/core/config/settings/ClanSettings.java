package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ClanSettings {

    @Comment(@CommentValue("Слотов в клане по умолчанию"))
    public int maxMembersDefault = 10;

    @Comment(@CommentValue("Минимальная длина тега клана"))
    public int tagMinLength = 2;

    @Comment(@CommentValue("Максимальная длина тега клана"))
    public int tagMaxLength = 6;

    @Comment(@CommentValue("Максимальная длина названия клана"))
    public int nameMaxLength = 32;

    @Comment(@CommentValue("Максимум кланов в списке /clan list (чат)"))
    public int listChatLimit = 50;

    @Comment(@CommentValue("Максимальная длина описания клана"))
    public int descriptionMaxLength = 128;

    @Comment(@CommentValue("Тема названий ролей: файл lang/roles/{theme}.{locale}.yml (military, anime, …)"))
    public String roleTheme = "military";

    @Comment(@CommentValue("Права ролей по умолчанию при создании клана"))
    public ClanRolePermissionDefaultsSettings rolePermissionDefaults = new ClanRolePermissionDefaultsSettings();

    @Comment(@CommentValue("Установленное знамя клана в мире"))
    public ClanStandardSettings standard = new ClanStandardSettings();
}
