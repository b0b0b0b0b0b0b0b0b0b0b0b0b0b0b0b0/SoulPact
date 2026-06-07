package bm.b0b0b0.SoulPact.chest.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class ChestPricingSettingsYaml {

    @Comment(@CommentValue("Базовая цена первой ячейки"))
    public double baseCost = 1000D;

    @Comment(@CommentValue("Линейная прибавка за каждую уже открытую ячейку"))
    public double linearStep = 250D;

    @Comment(@CommentValue("Размер тира для множителя"))
    public int tierSize = 9;

    @Comment(@CommentValue("Множитель за тир"))
    public double tierMultiplier = 1.12D;

    @Comment(@CommentValue("Потолок цены (0 — без лимита)"))
    public double maxCost = 75000D;
}
