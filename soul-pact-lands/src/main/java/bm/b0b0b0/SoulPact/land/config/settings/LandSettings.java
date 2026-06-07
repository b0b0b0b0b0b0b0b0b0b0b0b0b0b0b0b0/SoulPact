package bm.b0b0b0.SoulPact.land.config.settings;

import java.util.ArrayList;
import java.util.List;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Lands — базы кланов. Тексты — в lang/"))
public final class LandSettings extends YamlSerializable {

    private static final List<String> DEFAULT_BORDER_COLORS = List.of(
            "RED_WOOL",
            "ORANGE_WOOL",
            "YELLOW_WOOL",
            "LIME_WOOL",
            "GREEN_WOOL",
            "CYAN_WOOL",
            "LIGHT_BLUE_WOOL",
            "BLUE_WOOL",
            "PURPLE_WOOL",
            "MAGENTA_WOOL",
            "PINK_WOOL",
            "WHITE_WOOL",
            "LIGHT_GRAY_WOOL",
            "GRAY_WOOL",
            "BLACK_WOOL",
            "BROWN_WOOL"
    );

    public LandSettings() {
        super(LandSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("Буфер между регионами кланов"))
    public int regionBuffer = 5;

    @Comment(@CommentValue("Начальный радиус базы"))
    public int baseRadius = 8;

    @Comment(@CommentValue("Material границы по умолчанию (_CONCRETE или _WOOL — в мире ставится бетон)"))
    public String borderMaterial = "RED_CONCRETE";

    @Comment(@CommentValue("Палитра цветов границы в меню (_WOOL), в мире — соответствующий _CONCRETE"))
    public List<String> borderColors = new ArrayList<>(DEFAULT_BORDER_COLORS);

    @NewLine
    public LandExpansionSettingsYaml expansion = new LandExpansionSettingsYaml();

    @NewLine
    public LandGuiSettings gui = new LandGuiSettings();
}
