package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class WarHubGuiSettings {

    @Comment(@CommentValue("Строк инвентаря (1–6)"))
    public int rows = 3;

    @Comment(@CommentValue("Номера слотов (0–53)"))
    public WarHubGuiSlotsSettings slots = new WarHubGuiSlotsSettings();
}
