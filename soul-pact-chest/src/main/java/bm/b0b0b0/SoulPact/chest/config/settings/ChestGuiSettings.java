package bm.b0b0b0.SoulPact.chest.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class ChestGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 6;

    @NewLine
    public ChestGuiSlotsSettings slots = new ChestGuiSlotsSettings();

    @NewLine
    public ChestGuiMaterialsSettings materials = new ChestGuiMaterialsSettings();
}
