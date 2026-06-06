package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class MembersGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Первый ряд — заголовок, последний — навигация"))
    public int rows = 6;

    @NewLine
    @Comment(@CommentValue("Номера слотов (0–53)"))
    public MembersGuiSlotsSettings slots = new MembersGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public MembersGuiMaterialsSettings materials = new MembersGuiMaterialsSettings();
}
