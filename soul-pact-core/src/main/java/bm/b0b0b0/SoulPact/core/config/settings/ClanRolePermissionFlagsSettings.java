package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ClanRolePermissionFlagsSettings {

    @Comment(@CommentValue("Выгонять участников"))
    public boolean kick = false;

    @Comment(@CommentValue("Принимать заявки в клан"))
    public boolean accept = false;

    @Comment(@CommentValue("Нанимать участников ниже по рангу"))
    public boolean recruitLower = false;

    @Comment(@CommentValue("Вносить в казну"))
    public boolean bankDeposit = true;

    @Comment(@CommentValue("Снимать из казны"))
    public boolean bankWithdraw = false;

    @Comment(@CommentValue("Класть в клановый сундук"))
    public boolean chestDeposit = true;

    @Comment(@CommentValue("Брать из кланового сундука"))
    public boolean chestWithdraw = false;
}
