package bm.b0b0b0.SoulPact.chest.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.FallbackNodeNames;

public final class ChestGuiSlotsSettings {

    public int buyCell = 1;
    public int bankLink = 3;

    @FallbackNodeNames({"page-1"})
    public int page1 = 5;

    @FallbackNodeNames({"page-2"})
    public int page2 = 6;

    @FallbackNodeNames({"page-3"})
    public int page3 = 7;

    public int back = 45;
    public int prevPage = 48;
    public int nextPage = 50;
}
