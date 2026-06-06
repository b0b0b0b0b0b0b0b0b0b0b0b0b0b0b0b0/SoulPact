package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ProfileGuiSlotsSettings {

    @Comment(@CommentValue("Карточка клана"))
    public int clanInfo = 11;

    @Comment(@CommentValue("Состав и роли"))
    public int members = 15;

    @Comment(@CommentValue("Заявки (только лидер)"))
    public int requests = 13;

    @Comment(@CommentValue("Покинуть клан (не лидер)"))
    public int leave = 13;

    @Comment(@CommentValue("Сообщение «нет клана»"))
    public int emptyMessage = 13;

    @Comment(@CommentValue("Создать клан (верхний ряд, 4-я ячейка)"))
    public int emptyCreate = 3;

    @Comment(@CommentValue("Список кланов (верхний ряд, 6-я ячейка)"))
    public int emptyList = 5;

    @Comment(@CommentValue("Назад в главное меню"))
    public int back = 22;

    @Comment(@CommentValue("Знамя клана (верх, центр)"))
    public int banner = 4;
}
