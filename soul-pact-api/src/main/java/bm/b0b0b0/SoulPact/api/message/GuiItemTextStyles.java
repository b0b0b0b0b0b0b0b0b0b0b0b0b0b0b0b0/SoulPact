package bm.b0b0b0.SoulPact.api.message;

import java.util.List;
import net.kyori.adventure.text.Component;

public final class GuiItemTextStyles {

    private static volatile GuiItemTextStyle current = GuiItemTextStyle.defaults();

    private GuiItemTextStyles() {
    }

    public static GuiItemTextStyle current() {
        return current;
    }

    public static void setCurrent(GuiItemTextStyle style) {
        current = style == null ? GuiItemTextStyle.defaults() : style;
    }

    public static Component apply(Component component) {
        return current.apply(component);
    }

    public static List<Component> applyAll(List<Component> components) {
        if (components == null || components.isEmpty()) {
            return List.of();
        }
        return components.stream().map(GuiItemTextStyles::apply).toList();
    }
}
