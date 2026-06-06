package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ListGuiMaterialsSettings {

    @Comment(@CommentValue("Не используется — в списке голова лидера клана"))
    public String entry = "PLAYER_HEAD";

    @Comment(@CommentValue("Пустой сервер"))
    public String empty = "BARRIER";

    @Comment(@CommentValue("Предыдущая / следующая страница"))
    public String pageArrow = "ARROW";

    @Comment(@CommentValue("Стрелка неактивна"))
    public String pageArrowDisabled = "GRAY_DYE";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
