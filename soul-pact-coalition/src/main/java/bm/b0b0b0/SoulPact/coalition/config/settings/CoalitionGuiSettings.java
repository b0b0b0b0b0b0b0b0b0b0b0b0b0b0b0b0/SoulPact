package bm.b0b0b0.SoulPact.coalition.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class CoalitionGuiSettings {

    @Comment(@CommentValue("Меню коалиции"))
    public CoalitionHubGuiSettings hub = new CoalitionHubGuiSettings();
}
