package bm.b0b0b0.SoulPact.war.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionWarBridge;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class WarVictoryAnnouncer {

    private final WarMessages messages;
    private final WarClanLookup clanLookup;
    private final CoalitionWarBridgeLookup coalitionWarBridgeLookup;

    public WarVictoryAnnouncer(
            WarMessages messages,
            WarClanLookup clanLookup,
            CoalitionWarBridgeLookup coalitionWarBridgeLookup
    ) {
        this.messages = messages;
        this.clanLookup = clanLookup;
        this.coalitionWarBridgeLookup = coalitionWarBridgeLookup;
    }

    public void announce(long winnerClanId, long loserClanId, Map<Long, Integer> killsByClan) {
        Map<String, String> placeholders = buildPlaceholders(winnerClanId, loserClanId);
        for (Player online : Bukkit.getOnlinePlayers()) {
            messages.sendList(online, "war.victory.broadcast", placeholders);
            sendKillLines(online, killsByClan);
        }
        for (Player online : Bukkit.getOnlinePlayers()) {
            Optional<Long> clanIdOptional = clanLookup.findClanIdByPlayerSync(online.getUniqueId());
            if (clanIdOptional.isEmpty() || clanIdOptional.get() != loserClanId) {
                continue;
            }
            messages.sendList(online, "war.victory.loser-alert", placeholders);
            sendKillLines(online, killsByClan);
        }
    }

    private Map<String, String> buildPlaceholders(long winnerClanId, long loserClanId) {
        String winnerTag = clanLookup.findClanTagSync(winnerClanId).orElse("#" + winnerClanId);
        String winnerName = clanLookup.findClanNameSync(winnerClanId).orElse(String.valueOf(winnerClanId));
        String loserTag = clanLookup.findClanTagSync(loserClanId).orElse("#" + loserClanId);
        String loserName = clanLookup.findClanNameSync(loserClanId).orElse(String.valueOf(loserClanId));
        return Map.of(
                "winner_tag", winnerTag,
                "winner_name", winnerName,
                "loser_tag", loserTag,
                "loser_name", loserName,
                "winner_coalition", formatCoalition(winnerClanId),
                "loser_coalition", formatCoalition(loserClanId)
        );
    }

    private String formatCoalition(long rootClanId) {
        Set<Long> clanIds = coalitionClanIds(rootClanId);
        List<String> labels = new ArrayList<>();
        for (long clanId : clanIds) {
            labels.add(formatClanLabel(clanId));
        }
        labels.sort(String.CASE_INSENSITIVE_ORDER);
        return String.join(", ", labels);
    }

    private String formatClanLabel(long clanId) {
        String tag = clanLookup.findClanTagSync(clanId).orElse("#" + clanId);
        String name = clanLookup.findClanNameSync(clanId).orElse(String.valueOf(clanId));
        return "[" + tag + "] " + name;
    }

    private Set<Long> coalitionClanIds(long clanId) {
        return coalitionWarBridgeLookup.resolve()
                .map(bridge -> bridge.coalitionClanIds(clanId))
                .orElse(Set.of(clanId));
    }

    private void sendKillLines(Player player, Map<Long, Integer> killsByClan) {
        if (killsByClan.isEmpty()) {
            messages.send(player, "war.victory.kill-empty");
            return;
        }
        List<Map.Entry<Long, Integer>> sorted = killsByClan.entrySet().stream()
                .sorted(Comparator.<Map.Entry<Long, Integer>>comparingInt(Map.Entry::getValue).reversed()
                        .thenComparing(entry -> clanLookup.findClanTagSync(entry.getKey()).orElse("")))
                .toList();
        for (Map.Entry<Long, Integer> entry : sorted) {
            long clanId = entry.getKey();
            messages.send(player, "war.victory.kill-line", Map.of(
                    "tag", clanLookup.findClanTagSync(clanId).orElse("#" + clanId),
                    "name", clanLookup.findClanNameSync(clanId).orElse(String.valueOf(clanId)),
                    "kills", String.valueOf(entry.getValue())
            ));
        }
    }
}
