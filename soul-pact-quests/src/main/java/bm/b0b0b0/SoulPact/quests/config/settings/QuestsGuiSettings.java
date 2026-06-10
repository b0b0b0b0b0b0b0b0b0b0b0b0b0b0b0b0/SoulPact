package bm.b0b0b0.SoulPact.quests.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class QuestsGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 6;

    @NewLine
    @Comment(@CommentValue("Номера слотов (0–53)"))
    public QuestsGuiSlotsSettings slots = new QuestsGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public QuestsGuiMaterialsSettings materials = new QuestsGuiMaterialsSettings();
}
