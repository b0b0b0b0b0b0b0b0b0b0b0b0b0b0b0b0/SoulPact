package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class DatabasePoolSettings {

    @Comment(@CommentValue("Максимальный размер пула соединений"))
    public int maximumPoolSize = 10;

    @Comment(@CommentValue("Минимум idle-соединений в пуле"))
    public int minimumIdle = 2;

    @Comment(@CommentValue("Таймаут получения соединения (мс)"))
    public long connectionTimeoutMs = 30000L;

    @Comment(@CommentValue("Таймаут простоя соединения (мс)"))
    public long idleTimeoutMs = 600000L;

    @Comment(@CommentValue("Максимальное время жизни соединения (мс)"))
    public long maxLifetimeMs = 1800000L;
}
