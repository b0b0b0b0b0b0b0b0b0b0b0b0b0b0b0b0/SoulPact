package bm.b0b0b0.SoulPact.gladiator.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class GladiatorGuiMaterialsSettings {

    @Comment(@CommentValue("Фон"))
    public String filler = "GRAY_STAINED_GLASS_PANE";

    @Comment(@CommentValue("Иконка арены по умолчанию (если не задана seticon)"))
    public String arenaDefault = "IRON_SWORD";

    @Comment(@CommentValue("Кнопка назад"))
    public String back = "ARROW";
}
