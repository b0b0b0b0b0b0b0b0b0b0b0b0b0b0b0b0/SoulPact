package bm.b0b0b0.SoulPact.chest.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Chest — клановый сундук. Тексты — в lang/"))
public final class ChestSettings extends YamlSerializable {

    public ChestSettings() {
        super(ChestSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Число страниц сундука"))
    public int pages = 3;

    @Comment(@CommentValue("Ячеек на странице"))
    public int cellsPerPage = 36;

    @NewLine
    public ChestPricingSettingsYaml pricing = new ChestPricingSettingsYaml();

    @NewLine
    public ChestGuiSettings gui = new ChestGuiSettings();
}
