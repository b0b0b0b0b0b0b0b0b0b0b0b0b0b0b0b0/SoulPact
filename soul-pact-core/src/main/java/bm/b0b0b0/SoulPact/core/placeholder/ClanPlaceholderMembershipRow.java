package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.UUID;

public record ClanPlaceholderMembershipRow(long clanId, String role, int kills, int deaths) {

    public static ClanPlaceholderMembershipRow empty() {
        return new ClanPlaceholderMembershipRow(0L, "", 0, 0);
    }

    public boolean present() {
        return clanId > 0L;
    }
}
