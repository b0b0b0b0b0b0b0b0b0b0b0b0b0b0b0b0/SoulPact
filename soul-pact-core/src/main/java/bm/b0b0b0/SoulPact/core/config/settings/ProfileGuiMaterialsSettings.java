package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ProfileGuiMaterialsSettings {

    @Comment(@CommentValue("Карточка клана"))
    public String clanInfo = "LEATHER";

    @Comment(@CommentValue("Состав клана"))
    public String members = "PLAYER_HEAD";

    @Comment(@CommentValue("Заявки в клан"))
    public String requests = "WRITABLE_BOOK";

    @Comment(@CommentValue("Покинуть клан"))
    public String leave = "RED_DYE";

    @Comment(@CommentValue("Нет клана"))
    public String empty = "BARRIER";

    @Comment(@CommentValue("Создать клан (экран без клана)"))
    public String create = "EMERALD";

    @Comment(@CommentValue("Список кланов (экран без клана)"))
    public String emptyList = "NETHER_STAR";

    @Comment(@CommentValue("Назад"))
    public String back = "ARROW";

    @Comment(@CommentValue("Заполнитель"))
    public String filler = "GRAY_STAINED_GLASS_PANE";

    @Comment(@CommentValue("Война (модуль SoulPact-War)"))
    public String war = "IRON_SWORD";
}
