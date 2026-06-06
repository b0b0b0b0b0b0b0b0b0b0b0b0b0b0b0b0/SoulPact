package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class RequestsGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Первый ряд — действия, последний — навигация"))
    public int rows = 6;

    @NewLine
    @Comment(@CommentValue("Номера слотов (0–53)"))
    public RequestsGuiSlotsSettings slots = new RequestsGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public RequestsGuiMaterialsSettings materials = new RequestsGuiMaterialsSettings();
}
