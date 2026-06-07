package bm.b0b0b0.SoulPact.coalition.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Coalition — коалиции кланов. Тексты — в lang/"))
public final class CoalitionSettings extends YamlSerializable {

    public CoalitionSettings() {
        super(CoalitionSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Максимум кланов в одной коалиции"))
    public int maxMembers = 3;

    @NewLine
    public CoalitionTreasurySettings treasury = new CoalitionTreasurySettings();

    @NewLine
    public CoalitionGuiSettings gui = new CoalitionGuiSettings();

    @NewLine
    public CoalitionBossbarSettings bossbar = new CoalitionBossbarSettings();
}
