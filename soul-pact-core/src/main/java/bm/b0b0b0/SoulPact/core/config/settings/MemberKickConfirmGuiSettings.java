package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class MemberKickConfirmGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6)"))
    public int rows = 3;

    @NewLine
    @Comment(@CommentValue("Номера слотов (0–53)"))
    public MemberKickConfirmGuiSlotsSettings slots = new MemberKickConfirmGuiSlotsSettings();

    @NewLine
    @Comment(@CommentValue("Material для иконок (имена Bukkit)"))
    public MemberKickConfirmGuiMaterialsSettings materials = new MemberKickConfirmGuiMaterialsSettings();
}
