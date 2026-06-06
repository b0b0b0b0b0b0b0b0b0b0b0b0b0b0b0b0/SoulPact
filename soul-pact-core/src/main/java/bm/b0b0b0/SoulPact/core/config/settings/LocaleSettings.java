package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.FallbackNodeNames;

public final class LocaleSettings {

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    @FallbackNodeNames({"default"})
    public String defaultLocale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    @FallbackNodeNames({"fallback"})
    public String fallbackLocale = "en";
}
