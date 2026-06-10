package bm.b0b0b0.SoulPact.discord.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class DiscordColorsSettings {

    @Comment(@CommentValue("Цвета embed в hex (#RRGGBB)"))
    public String serverStart = "#57F287";

    public String serverStop = "#ED4245";

    public String clanCreate = "#57F287";

    public String clanDelete = "#ED4245";

    public String tagChange = "#FEE75C";

    public String descChange = "#FEE75C";

    public String roleChange = "#5865F2";

    public String memberJoin = "#57F287";

    public String memberLeave = "#ED4245";

    public String memberKick = "#ED4245";

    public String leaderChange = "#EB459E";

    public String warStart = "#ED4245";

    public String warEnd = "#FEE75C";

    public String warWin = "#FFD700";

    public String questComplete = "#57F287";

    public String gladStart = "#EB459E";

    public String gladWin = "#FFD700";
}
