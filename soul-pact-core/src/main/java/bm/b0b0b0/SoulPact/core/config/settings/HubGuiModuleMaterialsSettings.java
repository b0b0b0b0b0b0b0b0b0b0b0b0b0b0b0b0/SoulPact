package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class HubGuiModuleMaterialsSettings {

    @Comment(@CommentValue("Банк клана"))
    public String bank = "GOLD_BLOCK";

    @Comment(@CommentValue("База клана"))
    public String land = "WHITE_BANNER";

    @Comment(@CommentValue("Клановый сундук"))
    public String chest = "CHEST";

    @Comment(@CommentValue("Война кланов"))
    public String war = "IRON_SWORD";

    @Comment(@CommentValue("Коалиция"))
    public String coalition = "BLUE_BANNER";

    @Comment(@CommentValue("Квесты"))
    public String quests = "WRITABLE_BOOK";

    @Comment(@CommentValue("Гладиаторские ивенты"))
    public String gladiator = "DIAMOND_SWORD";
}
