package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class MysqlSettings {

    public String host = "localhost";
    public int port = 3306;
    public String database = "soulpact";
    public String username = "root";

    @Comment(@CommentValue("Пароль MySQL (оставьте пустым если без пароля)"))
    public String password = "";
}
