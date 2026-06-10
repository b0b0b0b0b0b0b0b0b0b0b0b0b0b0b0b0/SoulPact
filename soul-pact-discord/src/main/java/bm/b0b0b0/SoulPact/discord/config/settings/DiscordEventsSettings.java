package bm.b0b0b0.SoulPact.discord.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class DiscordEventsSettings {

    @Comment(@CommentValue("Запуск сервера/модуля"))
    public boolean serverStart = true;

    @Comment(@CommentValue("Остановка сервера/модуля"))
    public boolean serverStop = true;

    @Comment(@CommentValue("Создание клана"))
    public boolean clanCreate = true;

    @Comment(@CommentValue("Удаление клана"))
    public boolean clanDelete = true;

    @Comment(@CommentValue("Смена тега клана"))
    public boolean tagChange = true;

    @Comment(@CommentValue("Смена описания клана"))
    public boolean descChange = true;

    @Comment(@CommentValue("Смена роли участника"))
    public boolean roleChange = true;

    @Comment(@CommentValue("Игрок вступил в клан"))
    public boolean memberJoin = true;

    @Comment(@CommentValue("Игрок покинул клан"))
    public boolean memberLeave = true;

    @Comment(@CommentValue("Игрока кикнули из клана"))
    public boolean memberKick = true;

    @Comment(@CommentValue("Передача лидерства"))
    public boolean leaderChange = true;

    @Comment(@CommentValue("Начало войны кланов"))
    public boolean warStart = true;

    @Comment(@CommentValue("Конец войны без победителя"))
    public boolean warEnd = true;

    @Comment(@CommentValue("Победа в войне кланов"))
    public boolean warWin = true;

    @Comment(@CommentValue("Клан завершил квест"))
    public boolean questComplete = true;

    @Comment(@CommentValue("Старт гладиаторского ивента"))
    public boolean gladStart = true;

    @Comment(@CommentValue("Победа в гладиаторском ивенте"))
    public boolean gladWin = true;
}
