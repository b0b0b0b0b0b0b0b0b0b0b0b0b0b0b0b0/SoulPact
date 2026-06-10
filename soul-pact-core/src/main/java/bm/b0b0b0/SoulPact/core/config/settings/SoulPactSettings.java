package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact — основной конфиг (числа, БД, лимиты, permissions)"))
public final class SoulPactSettings extends YamlSerializable {

    public SoulPactSettings() {
        super(SoulPactSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Локализация (тексты — в папке lang/)"))
    public LocaleSettings locale = new LocaleSettings();

    @NewLine
    @Comment(@CommentValue("База данных"))
    public DatabaseSettings database = new DatabaseSettings();

    @NewLine
    @Comment(@CommentValue("Лимиты и правила кланов"))
    public ClanSettings clan = new ClanSettings();

    @NewLine
    @Comment(@CommentValue("Экономика (опционально, через Vault)"))
    public EconomySettings economy = new EconomySettings();

    @NewLine
    @Comment(@CommentValue("PlaceholderAPI (%spact_...%)"))
    public PlaceholderSettings placeholders = new PlaceholderSettings();

    @NewLine
    @Comment(@CommentValue("Permission nodes"))
    public PermissionSettings permissions = new PermissionSettings();
}
