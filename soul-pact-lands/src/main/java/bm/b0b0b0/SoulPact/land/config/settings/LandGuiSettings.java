package bm.b0b0b0.SoulPact.land.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class LandGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6). Размер = rows × 9"))
    public int rows = 5;

    @NewLine
    public LandGuiSlotsSettings slots = new LandGuiSlotsSettings();
}
