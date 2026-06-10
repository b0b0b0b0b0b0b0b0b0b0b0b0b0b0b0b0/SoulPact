package bm.b0b0b0.SoulPact.quests.config.settings;

import java.util.ArrayList;
import java.util.List;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Quests — клановые квесты. Тексты — в lang/"))
public final class QuestsSettings extends YamlSerializable {

    public QuestsSettings() {
        super(QuestsSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Время на ежедневный квест (часов); по истечении квест проваливается"))
    public int dailyDurationHours = 24;

    @Comment(@CommentValue("Перезарядка ежедневного квеста после выполнения (часов)"))
    public int dailyCooldownHours = 20;

    @Comment(@CommentValue("Сброс прогресса в БД (секунд)"))
    public int progressFlushSeconds = 30;

    @Comment(@CommentValue("Кэш «игрок → клан» для счётчиков прогресса (секунд)"))
    public int playerClanCacheSeconds = 60;

    @Comment(@CommentValue("Только лидер может запускать и отменять квесты"))
    public boolean leaderOnlyManage = true;

    @NewLine
    @Comment({
            @CommentValue("Квесты. Формат сегментов через |:"),
            @CommentValue("id | DAILY/ONCE | миссия | фильтр | количество | очки | казна | команды"),
            @CommentValue("Миссии: KILL_PLAYERS, KILL_MOBS, BREAK_BLOCKS, PLACE_BLOCKS, FISH"),
            @CommentValue("Фильтр: * — любые; иначе имена EntityType/Material через запятую"),
            @CommentValue("Команды: от консоли, через &&; плейсхолдеры {player}, {tag}"),
            @CommentValue("Название и описание квеста — lang: quests.quest.<id>.name / .description")
    })
    public List<String> quests = new ArrayList<>(List.of(
            "zombie_hunt | DAILY | KILL_MOBS | ZOMBIE,ZOMBIE_VILLAGER,HUSK | 50 | 10 | 250 |",
            "miner | DAILY | BREAK_BLOCKS | DIAMOND_ORE,DEEPSLATE_DIAMOND_ORE | 16 | 15 | 400 |",
            "builder | DAILY | PLACE_BLOCKS | * | 500 | 8 | 200 |",
            "fisherman | DAILY | FISH | * | 30 | 8 | 200 |",
            "first_blood | ONCE | KILL_PLAYERS | * | 10 | 50 | 1500 |",
            "dragon_slayer | ONCE | KILL_MOBS | ENDER_DRAGON | 1 | 100 | 5000 |"
    ));

    @NewLine
    @Comment(@CommentValue("Инвентарь квестов"))
    public QuestsGuiSettings gui = new QuestsGuiSettings();
}
