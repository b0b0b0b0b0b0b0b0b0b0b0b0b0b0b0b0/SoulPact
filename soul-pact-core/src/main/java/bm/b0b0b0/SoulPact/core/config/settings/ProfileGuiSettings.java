package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class ProfileGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 3;

    @NewLine
    @Comment(@CommentValue("Номера слотов (0–53)"))
    public ProfileGuiSlotsSettings slots = new ProfileGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public ProfileGuiMaterialsSettings materials = new ProfileGuiMaterialsSettings();
}
