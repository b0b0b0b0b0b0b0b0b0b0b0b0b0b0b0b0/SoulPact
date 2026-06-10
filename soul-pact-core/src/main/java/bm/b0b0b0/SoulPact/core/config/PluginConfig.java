package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.GuiGeneralSettings;
import bm.b0b0b0.SoulPact.core.config.settings.SoulPactSettings;

public final class PluginConfig {

    private final LocaleConfig localeConfig;
    private final DatabaseConfig databaseConfig;
    private final ClanConfig clanConfig;
    private final GuiHubConfig guiHubConfig;
    private final GuiProfileConfig guiProfileConfig;
    private final GuiListConfig guiListConfig;
    private final GuiInfoConfig guiInfoConfig;
    private final GuiExtensionsConfig guiExtensionsConfig;
    private final GuiRequestsConfig guiRequestsConfig;
    private final GuiRequestDetailConfig guiRequestDetailConfig;
    private final GuiMembersConfig guiMembersConfig;
    private final GuiMemberDetailConfig guiMemberDetailConfig;
    private final GuiMemberKickConfirmConfig guiMemberKickConfirmConfig;
    private final GuiClanSettingsConfig guiClanSettingsConfig;
    private final GuiClanRoleSettingsConfig guiClanRoleSettingsConfig;
    private final GuiClanBannerConfig guiClanBannerConfig;
    private final PermissionsConfig permissionsConfig;
    private final EconomyConfig economyConfig;
    private final PlaceholderConfig placeholderConfig;

    public PluginConfig(SoulPactSettings mainSettings, GuiGeneralSettings guiSettings) {
        this.localeConfig = new LocaleConfig(mainSettings.locale);
        this.databaseConfig = new DatabaseConfig(mainSettings.database);
        this.clanConfig = new ClanConfig(mainSettings.clan);
        this.guiHubConfig = new GuiHubConfig(guiSettings.hub);
        this.guiProfileConfig = new GuiProfileConfig(guiSettings.profile);
        this.guiListConfig = new GuiListConfig(guiSettings.list);
        this.guiInfoConfig = new GuiInfoConfig(guiSettings.info);
        this.guiExtensionsConfig = new GuiExtensionsConfig(guiSettings.extensions);
        this.guiRequestsConfig = new GuiRequestsConfig(guiSettings.requests);
        this.guiRequestDetailConfig = new GuiRequestDetailConfig(guiSettings.requestDetail);
        this.guiMembersConfig = new GuiMembersConfig(guiSettings.members);
        this.guiMemberDetailConfig = new GuiMemberDetailConfig(guiSettings.memberDetail);
        this.guiMemberKickConfirmConfig = new GuiMemberKickConfirmConfig(guiSettings.memberKickConfirm);
        this.guiClanSettingsConfig = new GuiClanSettingsConfig(guiSettings.settings);
        this.guiClanRoleSettingsConfig = new GuiClanRoleSettingsConfig(guiSettings.roleSettings);
        this.guiClanBannerConfig = new GuiClanBannerConfig(guiSettings.banner);
        this.permissionsConfig = new PermissionsConfig(mainSettings.permissions);
        this.economyConfig = new EconomyConfig(mainSettings.economy);
        this.placeholderConfig = new PlaceholderConfig(mainSettings.placeholders);
    }

    public LocaleConfig locale() {
        return localeConfig;
    }

    public DatabaseConfig database() {
        return databaseConfig;
    }

    public ClanConfig clan() {
        return clanConfig;
    }

    public GuiHubConfig guiHub() {
        return guiHubConfig;
    }

    public GuiProfileConfig guiProfile() {
        return guiProfileConfig;
    }

    public GuiListConfig guiList() {
        return guiListConfig;
    }

    public GuiInfoConfig guiInfo() {
        return guiInfoConfig;
    }

    public GuiExtensionsConfig guiExtensions() {
        return guiExtensionsConfig;
    }

    public GuiRequestsConfig guiRequests() {
        return guiRequestsConfig;
    }

    public GuiRequestDetailConfig guiRequestDetail() {
        return guiRequestDetailConfig;
    }

    public GuiMembersConfig guiMembers() {
        return guiMembersConfig;
    }

    public GuiMemberDetailConfig guiMemberDetail() {
        return guiMemberDetailConfig;
    }

    public GuiMemberKickConfirmConfig guiMemberKickConfirm() {
        return guiMemberKickConfirmConfig;
    }

    public GuiClanSettingsConfig guiClanSettings() {
        return guiClanSettingsConfig;
    }

    public GuiClanRoleSettingsConfig guiClanRoleSettings() {
        return guiClanRoleSettingsConfig;
    }

    public GuiClanBannerConfig guiClanBanner() {
        return guiClanBannerConfig;
    }

    public PermissionsConfig permissions() {
        return permissionsConfig;
    }

    public EconomyConfig economy() {
        return economyConfig;
    }

    public PlaceholderConfig placeholders() {
        return placeholderConfig;
    }
}
