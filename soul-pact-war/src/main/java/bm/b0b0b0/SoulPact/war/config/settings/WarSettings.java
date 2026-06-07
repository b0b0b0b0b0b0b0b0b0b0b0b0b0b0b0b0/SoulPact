package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-War — войны кланов. Тексты — в lang/"))
public final class WarSettings extends YamlSerializable {

    public WarSettings() {
        super(WarSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Доля казны за выкуп (0.0–1.0)"))
    public double ransomPercent = 0.8D;

    @Comment(@CommentValue("Секунд на захват флага"))
    public int captureSeconds = 60;

    @NewLine
    public WarGuiSettings gui = new WarGuiSettings();

    @NewLine
    public WarBossbarSettings bossbar = new WarBossbarSettings();
}
