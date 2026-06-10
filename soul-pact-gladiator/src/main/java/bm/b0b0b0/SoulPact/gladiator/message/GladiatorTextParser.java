package bm.b0b0b0.SoulPact.gladiator.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class GladiatorTextParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private GladiatorTextParser() {
    }

    public static Component parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.empty();
        }
        if (input.indexOf('<') >= 0 && input.indexOf('>') > input.indexOf('<')) {
            return MINI_MESSAGE.deserialize(input);
        }
        return LEGACY.deserialize(input);
    }
}
