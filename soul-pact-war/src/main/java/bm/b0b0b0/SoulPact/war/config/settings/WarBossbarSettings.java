package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class WarBossbarSettings {

    @Comment(@CommentValue("Объявленная война"))
    public String pendingColor = "YELLOW";

    @Comment(@CommentValue("Активная война"))
    public String activeColor = "RED";

    @Comment(@CommentValue("Захват флага — защитники"))
    public String captureDefendingColor = "RED";

    @Comment(@CommentValue("Захват флага — атакующие"))
    public String captureAttackingColor = "GREEN";
}
