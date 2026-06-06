package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class EconomySettings {

    @Comment(@CommentValue("Отключить экономику кланов"))
    public boolean economyDisabled = false;

    @Comment(@CommentValue("Стоимость создания клана когда Vault подключён и economy-disabled: false"))
    public double createCostAmount = 1000.0;
}
