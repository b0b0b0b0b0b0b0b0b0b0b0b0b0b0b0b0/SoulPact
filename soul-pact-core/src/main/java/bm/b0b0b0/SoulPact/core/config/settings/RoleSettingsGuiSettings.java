package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class RoleSettingsGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6)"))
    public int rows = 4;

    @NewLine
    @Comment(@CommentValue("Номера слотов (0–53)"))
    public RoleSettingsGuiSlotsSettings slots = new RoleSettingsGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public RoleSettingsGuiMaterialsSettings materials = new RoleSettingsGuiMaterialsSettings();
}
