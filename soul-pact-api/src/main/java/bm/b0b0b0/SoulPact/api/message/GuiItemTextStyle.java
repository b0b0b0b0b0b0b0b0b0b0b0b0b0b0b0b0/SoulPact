package bm.b0b0b0.SoulPact.api.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public final class GuiItemTextStyle {

    private final boolean italic;
    private final boolean bold;
    private final boolean underlined;
    private final boolean strikethrough;
    private final boolean obfuscated;

    public GuiItemTextStyle(
            boolean italic,
            boolean bold,
            boolean underlined,
            boolean strikethrough,
            boolean obfuscated
    ) {
        this.italic = italic;
        this.bold = bold;
        this.underlined = underlined;
        this.strikethrough = strikethrough;
        this.obfuscated = obfuscated;
    }

    public static GuiItemTextStyle defaults() {
        return new GuiItemTextStyle(false, false, false, false, false);
    }

    public boolean italic() {
        return italic;
    }

    public boolean bold() {
        return bold;
    }

    public boolean underlined() {
        return underlined;
    }

    public boolean strikethrough() {
        return strikethrough;
    }

    public boolean obfuscated() {
        return obfuscated;
    }

    public Component apply(Component component) {
        if (component == null) {
            return Component.empty();
        }
        Component result = component;
        result = applyIfUnset(result, TextDecoration.ITALIC, italic);
        result = applyIfUnset(result, TextDecoration.BOLD, bold);
        result = applyIfUnset(result, TextDecoration.UNDERLINED, underlined);
        result = applyIfUnset(result, TextDecoration.STRIKETHROUGH, strikethrough);
        result = applyIfUnset(result, TextDecoration.OBFUSCATED, obfuscated);
        return result;
    }

    private static Component applyIfUnset(Component component, TextDecoration decoration, boolean value) {
        if (component.decoration(decoration) != TextDecoration.State.NOT_SET) {
            return component;
        }
        return component.decoration(decoration, value);
    }
}
