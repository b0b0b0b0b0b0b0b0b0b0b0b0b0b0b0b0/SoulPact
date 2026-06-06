package bm.b0b0b0.SoulPact.core.config.settings;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;

@Comment(@CommentValue("SoulPact — общие настройки GUI (слоты, материалы). Тексты — в lang/"))
public final class GuiGeneralSettings extends YamlSerializable {

    public GuiGeneralSettings() {
        super(SoulPactSerializerConfig.INSTANCE);
    }

    @Comment(@CommentValue("Главное меню кланов (/clan help)"))
    public HubGuiSettings hub = new HubGuiSettings();

    @Comment(@CommentValue("Профиль клана (карточка + состав)"))
    public ProfileGuiSettings profile = new ProfileGuiSettings();

    @Comment(@CommentValue("Список кланов с пагинацией"))
    public ListGuiSettings list = new ListGuiSettings();

    @Comment(@CommentValue("Карточка клана из списка"))
    public InfoGuiSettings info = new InfoGuiSettings();

    @Comment(@CommentValue("Модули SoulPact (extension registry)"))
    public ExtensionsGuiSettings extensions = new ExtensionsGuiSettings();

    @Comment(@CommentValue("Заявки в клан (лидер)"))
    public RequestsGuiSettings requests = new RequestsGuiSettings();

    @Comment(@CommentValue("Карточка заявителя"))
    public RequestDetailGuiSettings requestDetail = new RequestDetailGuiSettings();

    @Comment(@CommentValue("Состав клана"))
    public MembersGuiSettings members = new MembersGuiSettings();

    @Comment(@CommentValue("Профиль участника"))
    public MemberDetailGuiSettings memberDetail = new MemberDetailGuiSettings();

    @Comment(@CommentValue("Подтверждение выгона из клана"))
    public MemberKickConfirmGuiSettings memberKickConfirm = new MemberKickConfirmGuiSettings();

    @Comment(@CommentValue("Настройки клана (лидер)"))
    public SettingsGuiSettings settings = new SettingsGuiSettings();

    @Comment(@CommentValue("Права роли в настройках клана"))
    public RoleSettingsGuiSettings roleSettings = new RoleSettingsGuiSettings();
}
