package bm.b0b0b0.SoulPact.bank.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class BankGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 6;

    @NewLine
    @Comment(@CommentValue("Номера слотов кнопок (0–53)"))
    public BankGuiSlotsSettings slots = new BankGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public BankGuiMaterialsSettings materials = new BankGuiMaterialsSettings();
}
