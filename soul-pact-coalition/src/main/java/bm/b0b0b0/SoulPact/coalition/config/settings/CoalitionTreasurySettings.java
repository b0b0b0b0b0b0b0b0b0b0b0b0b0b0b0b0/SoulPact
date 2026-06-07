package bm.b0b0b0.SoulPact.coalition.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class CoalitionTreasurySettings {

    @Comment(@CommentValue("Доля проигравшей казны — участнику войны"))
    public double warSharePercent = 0.25D;

    @Comment(@CommentValue("Доля — захватившему флаг"))
    public double captureSharePercent = 0.25D;

    @Comment(@CommentValue("Доля — остальным союзникам победителя"))
    public double poolSharePercent = 0.50D;
}
