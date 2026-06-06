package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class HubGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 6;

    @NewLine
    @Comment(@CommentValue("Номера слотов кнопок (0–53)"))
    public HubGuiSlotsSettings slots = new HubGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public HubGuiMaterialsSettings materials = new HubGuiMaterialsSettings();
}
