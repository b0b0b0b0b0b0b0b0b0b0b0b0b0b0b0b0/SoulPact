package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.List;

public record ClanPlaceholderExtras(
        int bannedCount,
        List<String> bannedNames,
        int mailTotal,
        String lastMailSender,
        String lastMailMessage,
        List<String> homeNames
) {

    private static final ClanPlaceholderExtras EMPTY = new ClanPlaceholderExtras(
            0,
            List.of(),
            0,
            "",
            "",
            List.of()
    );

    public static ClanPlaceholderExtras empty() {
        return EMPTY;
    }
}
