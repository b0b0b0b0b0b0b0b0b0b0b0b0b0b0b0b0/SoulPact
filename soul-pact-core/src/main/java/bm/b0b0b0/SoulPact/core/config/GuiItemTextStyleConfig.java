package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.api.message.GuiItemTextStyle;
import bm.b0b0b0.SoulPact.core.config.settings.GuiTextStyleSettings;

public final class GuiItemTextStyleConfig {

    private final GuiItemTextStyle style;

    public GuiItemTextStyleConfig(GuiTextStyleSettings settings) {
        this.style = new GuiItemTextStyle(
                settings.italic,
                settings.bold,
                settings.underlined,
                settings.strikethrough,
                settings.obfuscated
        );
    }

    public GuiItemTextStyle style() {
        return style;
    }
}
