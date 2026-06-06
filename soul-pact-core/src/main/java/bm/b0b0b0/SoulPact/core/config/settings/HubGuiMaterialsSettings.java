package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class HubGuiMaterialsSettings {

    public String overview = "NETHER_STAR";
    @Comment(@CommentValue("Не используется — голова игрока"))
    public String profile = "PLAYER_HEAD";
    public String settings = "COMPARATOR";
    public String banner = "WHITE_BANNER";
    public String create = "EMERALD";
    public String help = "BOOK";
    public String filler = "GRAY_STAINED_GLASS_PANE";
}
