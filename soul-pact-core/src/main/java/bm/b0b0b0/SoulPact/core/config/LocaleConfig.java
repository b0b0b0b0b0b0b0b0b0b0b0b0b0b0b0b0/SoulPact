package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.LocaleSettings;

public final class LocaleConfig {

    private final String defaultLocale;
    private final String fallbackLocale;

    public LocaleConfig(LocaleSettings settings) {
        this.defaultLocale = settings.defaultLocale;
        this.fallbackLocale = settings.fallbackLocale;
    }

    public String defaultLocale() {
        return defaultLocale;
    }

    public String fallbackLocale() {
        return fallbackLocale;
    }
}
