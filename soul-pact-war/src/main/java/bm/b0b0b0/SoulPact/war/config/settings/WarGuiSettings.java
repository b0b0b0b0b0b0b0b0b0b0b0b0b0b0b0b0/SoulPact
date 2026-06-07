package bm.b0b0b0.SoulPact.war.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;

public final class WarGuiSettings {

    @Comment(@CommentValue("Подтверждение объявления войны"))
    public WarDeclareConfirmGuiSettings declareConfirm = new WarDeclareConfirmGuiSettings();

    @Comment(@CommentValue("Список входящих войн"))
    public WarPendingListGuiSettings pendingList = new WarPendingListGuiSettings();

    @Comment(@CommentValue("Карточка входящей войны"))
    public WarPendingDetailGuiSettings pendingDetail = new WarPendingDetailGuiSettings();

    @Comment(@CommentValue("Меню войны из профиля клана"))
    public WarHubGuiSettings hub = new WarHubGuiSettings();
}
