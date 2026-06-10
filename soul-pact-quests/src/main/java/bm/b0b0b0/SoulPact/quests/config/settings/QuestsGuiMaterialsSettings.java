package bm.b0b0b0.SoulPact.quests.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class QuestsGuiMaterialsSettings {

    @Comment(@CommentValue("Фон"))
    public String filler = "GRAY_STAINED_GLASS_PANE";

    @Comment(@CommentValue("Доступный квест"))
    public String available = "BOOK";

    @Comment(@CommentValue("Активный квест"))
    public String active = "WRITABLE_BOOK";

    @Comment(@CommentValue("Выполненный (once) или на перезарядке (daily)"))
    public String completed = "ENCHANTED_BOOK";

    @Comment(@CommentValue("Кнопка назад"))
    public String back = "ARROW";
}
