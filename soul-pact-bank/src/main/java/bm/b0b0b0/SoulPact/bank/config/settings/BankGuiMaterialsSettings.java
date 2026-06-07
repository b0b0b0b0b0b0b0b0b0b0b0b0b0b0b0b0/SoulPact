package bm.b0b0b0.SoulPact.bank.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class BankGuiMaterialsSettings {

    @Comment(@CommentValue("Заполнитель пустых слотов"))
    public String filler = "GRAY_STAINED_GLASS_PANE";

    @Comment(@CommentValue("Баланс казны"))
    public String balance = "GOLD_BLOCK";

    @Comment(@CommentValue("Кнопки депозита"))
    public String deposit = "LIME_DYE";

    @Comment(@CommentValue("Кнопки снятия"))
    public String withdraw = "RED_DYE";

    @Comment(@CommentValue("Внести всё"))
    public String depositAll = "EMERALD";

    @Comment(@CommentValue("Снять всё"))
    public String withdrawAll = "GOLD_INGOT";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";
}
