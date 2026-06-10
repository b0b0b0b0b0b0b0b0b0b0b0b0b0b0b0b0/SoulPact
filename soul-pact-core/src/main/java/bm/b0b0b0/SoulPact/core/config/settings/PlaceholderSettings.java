package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class PlaceholderSettings {

    @Comment(@CommentValue("Кэш PlaceholderAPI (мс). 0 — без кэша"))
    public int cacheMillis = 1000;

    @Comment(@CommentValue("Максимальный уровень клана (для level / points_to_nextlevel)"))
    public int maxClanLevel = 10;

    @Comment(@CommentValue("Очков на уровень (упрощённая формула уровня)"))
    public int pointsPerLevel = 100;

    @Comment(@CommentValue("Доп. слотов клана (поверх max_slots из БД)"))
    public int extraSlots = 0;

    @Comment(@CommentValue("Символ «повышение» в formated_levelsup"))
    public String levelUpSymbol = "▲";

    @Comment(@CommentValue("Символ «понижение» в formated_levelsub"))
    public String levelDownSymbol = "▼";

    @Comment(@CommentValue("Формат тега: {tag}"))
    public String tagFormated = "<white>[{tag}]</white>";

    @Comment(@CommentValue("Формат тега без цвета"))
    public String tagFormatedNocolor = "[{tag}]";

    @Comment(@CommentValue("Формат «в клане»"))
    public String hasClanFormated = "<#86EFAC>[{tag}]</#86EFAC> <white>{name}</white>";

    @Comment(@CommentValue("Формат «не в клане»"))
    public String hasClanFormatedNone = "<#6B7280>—";

    @Comment(@CommentValue("Формат лидера"))
    public String leaderFormated = "<white>{leader}</white>";

    @Comment(@CommentValue("Формат даты создания"))
    public String creationDateFormated = "<#9CA3AF>{date}";

    @Comment(@CommentValue("Да / нет для boolean"))
    public String booleanYes = "<#86EFAC>да";

    @Comment(@CommentValue("Нет"))
    public String booleanNo = "<#F87171>нет";

    @Comment(@CommentValue("Верифицированный тег"))
    public String verifiedTagFormated = "<#86EFAC>✔ <white>[{tag}]</white>";

    @Comment(@CommentValue("Список участников (разделитель)"))
    public String membersSeparator = ", ";

    @Comment(@CommentValue("Список союзников (разделитель)"))
    public String alliesSeparator = ", ";

    @Comment(@CommentValue("Значение, если модуль почты не подключён"))
    public String mailUnavailable = "0";

    @Comment(@CommentValue("Значение, если дома клана не реализованы"))
    public String homeUnavailable = "";

    @Comment(@CommentValue("Значение, если соперники не реализованы"))
    public String rivalUnavailable = "";
}
