package bm.b0b0b0.SoulPact.clan.service;

import java.util.Map;

public record ClanHubSnapshot(
        Map<String, String> placeholders,
        boolean inClan,
        boolean clanLeader
) {
}
