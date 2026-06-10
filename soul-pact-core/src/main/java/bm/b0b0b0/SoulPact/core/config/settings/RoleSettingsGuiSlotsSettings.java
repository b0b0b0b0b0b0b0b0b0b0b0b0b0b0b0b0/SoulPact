package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RoleSettingsGuiSlotsSettings {

    @Comment(@CommentValue("Выгонять участников"))
    public int kick = 11;

    @Comment(@CommentValue("Принимать участников"))
    public int accept = 13;

    @Comment(@CommentValue("Нанимать нижних чинов"))
    public int recruitLower = 15;

    @Comment(@CommentValue("Вносить в казну"))
    public int bankDeposit = 20;

    @Comment(@CommentValue("Снимать из казны"))
    public int bankWithdraw = 22;

    @Comment(@CommentValue("Класть в клановый сундук"))
    public int chestDeposit = 29;

    @Comment(@CommentValue("Брать из кланового сундука"))
    public int chestWithdraw = 33;

    @Comment(@CommentValue("Управлять базой"))
    public int landManage = 38;

    @Comment(@CommentValue("Объявлять войну"))
    public int warDeclare = 40;

    @Comment(@CommentValue("Принимать войну и выкуп"))
    public int warRespond = 42;

    @Comment(@CommentValue("Участвовать в войне"))
    public int warFight = 44;

    @Comment(@CommentValue("Управлять коалицией"))
    public int coalitionManage = 46;

    @Comment(@CommentValue("Назад"))
    public int back = 49;
}
