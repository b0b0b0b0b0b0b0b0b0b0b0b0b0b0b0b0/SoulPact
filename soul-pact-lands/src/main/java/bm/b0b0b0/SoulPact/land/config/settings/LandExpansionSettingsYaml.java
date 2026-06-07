package bm.b0b0b0.SoulPact.land.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class LandExpansionSettingsYaml {

    @Comment(@CommentValue("Шаг расширения базы (блоков)"))
    public int step = 8;

    @Comment(@CommentValue("Максимальный радиус/экстент"))
    public int maxExtent = 32;

    @Comment(@CommentValue("Базовая цена расширения"))
    public double baseCost = 1000D;

    @Comment(@CommentValue("Доплата за каждый тир расширения"))
    public double costPerBlock = 250D;
}
