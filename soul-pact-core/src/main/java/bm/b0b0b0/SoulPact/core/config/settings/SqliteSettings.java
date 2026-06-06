package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class SqliteSettings {

    @Comment(@CommentValue("Имя файла SQLite внутри storage-directory"))
    public String file = "data.db";
}
