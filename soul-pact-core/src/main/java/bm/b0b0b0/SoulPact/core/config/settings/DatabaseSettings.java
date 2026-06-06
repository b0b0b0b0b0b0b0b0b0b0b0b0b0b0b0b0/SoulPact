package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class DatabaseSettings {

    @Comment(@CommentValue("Тип хранилища: sqlite или mysql"))
    public String type = "sqlite";

    @Comment(@CommentValue("Отключить плагин при ошибке подключения к БД"))
    public boolean failOnConnect = true;

    @Comment(@CommentValue("Папка для SQLite и других файлов данных (относительно plugins/SoulPact/)"))
    public String storageDirectory = "storage";

    @NewLine
    public SqliteSettings sqlite = new SqliteSettings();

    @NewLine
    public MysqlSettings mysql = new MysqlSettings();

    @NewLine
    @Comment(@CommentValue("HikariCP pool"))
    public DatabasePoolSettings pool = new DatabasePoolSettings();
}
