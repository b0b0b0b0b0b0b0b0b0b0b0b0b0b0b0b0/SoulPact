package bm.b0b0b0.SoulPact.bank.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class BankGuiSlotsSettings {

    @Comment(@CommentValue("Баланс казны"))
    public int balance = 4;

    @Comment(@CommentValue("Первый слот пресетов депозита"))
    public int depositStart = 20;

    @Comment(@CommentValue("Первый слот пресетов снятия"))
    public int withdrawStart = 29;

    @Comment(@CommentValue("Внести всё"))
    public int depositAll = 24;

    @Comment(@CommentValue("Снять всё"))
    public int withdrawAll = 33;

    @Comment(@CommentValue("Назад"))
    public int back = 49;
}
