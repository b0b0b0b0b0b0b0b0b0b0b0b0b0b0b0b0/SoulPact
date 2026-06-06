package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class RequestsGuiMaterialsSettings {

    @Comment(@CommentValue("Принять всех"))
    public String acceptAll = "LIME_CONCRETE";

    @Comment(@CommentValue("Отклонить всех"))
    public String denyAll = "RED_CONCRETE";

    @Comment(@CommentValue("Заблокировать всех"))
    public String blockAll = "PURPLE_CONCRETE";

    @Comment(@CommentValue("Заявки открыты"))
    public String toggleOpen = "OAK_DOOR";

    @Comment(@CommentValue("Заявки закрыты"))
    public String toggleClosed = "IRON_DOOR";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";

    @Comment(@CommentValue("Пустой список"))
    public String empty = "BARRIER";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
