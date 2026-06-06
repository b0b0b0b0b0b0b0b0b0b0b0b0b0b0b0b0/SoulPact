package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class ExtensionsGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Последний ряд — навигация"))
    public int rows = 6;

    @NewLine
    @Comment(@CommentValue("Слоты навигации"))
    public ExtensionsGuiSlotsSettings slots = new ExtensionsGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок"))
    public ExtensionsGuiMaterialsSettings materials = new ExtensionsGuiMaterialsSettings();
}
