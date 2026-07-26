package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;

public final class GuiTextStyleSettings {

    @Comment({
            @CommentValue("Курсив у имён и lore предметов в GUI."),
            @CommentValue("Minecraft по умолчанию рисует item-meta курсивом, если стиль не задан."),
            @CommentValue("false = обычный текст; true = как ваниль. Явные <italic>/&o в lang не перебиваются.")
    })
    public boolean italic = false;

    @NewLine
    @Comment(@CommentValue("Жирный по умолчанию (если в тексте нет явного <bold>/&l)"))
    public boolean bold = false;

    @NewLine
    @Comment(@CommentValue("Подчёркнутый по умолчанию (если нет <underlined>/&n)"))
    public boolean underlined = false;

    @NewLine
    @Comment(@CommentValue("Зачёркнутый по умолчанию (если нет <strikethrough>/&m)"))
    public boolean strikethrough = false;

    @NewLine
    @Comment(@CommentValue("Обфускация по умолчанию (если нет <obfuscated>/&k)"))
    public boolean obfuscated = false;
}
