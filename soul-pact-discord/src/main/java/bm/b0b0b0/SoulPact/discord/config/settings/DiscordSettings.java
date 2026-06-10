package bm.b0b0b0.SoulPact.discord.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact-Discord — события кланов в Discord через WebHook. Тексты embed — в lang/"))
public final class DiscordSettings extends YamlSerializable {

    public DiscordSettings() {
        super(DiscordSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Язык по умолчанию (имя файла без .yml из папки lang/)"))
    public String locale = "ru";

    @Comment(@CommentValue("Запасной язык, если ключ не найден в основном"))
    public String fallbackLocale = "en";

    @NewLine
    @Comment(@CommentValue("URL вебхука Discord-канала (Integrations → Webhooks → Copy URL)"))
    public String webhookUrl = "";

    @Comment(@CommentValue("Имя бота в Discord"))
    public String botName = "SoulPact";

    @Comment(@CommentValue("URL аватарки бота (пусто — аватар вебхука)"))
    public String avatarUrl = "";

    @Comment(@CommentValue("Название сервера в footer embed'ов"))
    public String serverName = "Minecraft Server";

    @NewLine
    @Comment(@CommentValue("Минимальный интервал между отправками (мс) — защита от rate-limit Discord"))
    public long sendIntervalMillis = 1500;

    @Comment(@CommentValue("Таймаут HTTP-запроса (секунд)"))
    public int requestTimeoutSeconds = 10;

    @Comment(@CommentValue("Максимум сообщений в очереди (старые отбрасываются)"))
    public int maxQueueSize = 200;

    @NewLine
    @Comment(@CommentValue("Какие события отправлять"))
    public DiscordEventsSettings events = new DiscordEventsSettings();

    @NewLine
    @Comment(@CommentValue("Цвета embed по событиям"))
    public DiscordColorsSettings colors = new DiscordColorsSettings();
}
