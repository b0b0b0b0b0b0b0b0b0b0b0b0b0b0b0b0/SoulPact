package bm.b0b0b0.SoulPact.core.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class AdventureTextParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private AdventureTextParser() {
    }

    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (containsMiniMessageTag(input)) {
            return MINI_MESSAGE.deserialize(input);
        }
        return LEGACY.deserialize(input);
    }

    public static String toLegacyString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }
        return LEGACY.serialize(parse(input));
    }

    private static boolean containsMiniMessageTag(String input) {
        int open = input.indexOf('<');
        if (open < 0) {
            return false;
        }
        int close = input.indexOf('>', open);
        return close > open;
    }
}
