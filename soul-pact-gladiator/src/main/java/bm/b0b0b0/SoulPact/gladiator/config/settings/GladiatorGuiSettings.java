package bm.b0b0b0.SoulPact.gladiator.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class GladiatorGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 3;

    @NewLine
    @Comment(@CommentValue("Номера слотов"))
    public GladiatorGuiSlotsSettings slots = new GladiatorGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public GladiatorGuiMaterialsSettings materials = new GladiatorGuiMaterialsSettings();
}
