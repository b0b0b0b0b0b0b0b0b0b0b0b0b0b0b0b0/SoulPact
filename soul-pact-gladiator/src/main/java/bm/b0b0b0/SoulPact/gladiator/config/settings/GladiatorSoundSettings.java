package bm.b0b0b0.SoulPact.gladiator.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class GladiatorSoundSettings {

    @Comment(@CommentValue("Звук старта боя (имя Bukkit Sound, пусто — без звука)"))
    public String start = "ENTITY_ENDER_DRAGON_GROWL";

    @Comment(@CommentValue("Звук выбывания участника"))
    public String eliminate = "ENTITY_LIGHTNING_BOLT_THUNDER";

    @Comment(@CommentValue("Звук победы"))
    public String win = "UI_TOAST_CHALLENGE_COMPLETE";

    @Comment(@CommentValue("Громкость"))
    public double volume = 1.0D;

    @Comment(@CommentValue("Высота тона"))
    public double pitch = 1.0D;
}
