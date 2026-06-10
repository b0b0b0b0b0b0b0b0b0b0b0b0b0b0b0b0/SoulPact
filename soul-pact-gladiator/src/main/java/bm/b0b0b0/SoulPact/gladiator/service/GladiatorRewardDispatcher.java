package bm.b0b0b0.SoulPact.gladiator.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class GladiatorRewardDispatcher {

    private final ArenaCatalog catalog;

    public GladiatorRewardDispatcher(ArenaCatalog catalog) {
        this.catalog = catalog;
    }

    public void dispatch(String arenaName, ClanRef winner, Collection<UUID> winnerParticipants) {
        List<String> commands = catalog.rewardsOf(arenaName);
        if (commands.isEmpty()) {
            return;
        }
        for (UUID participantId : winnerParticipants) {
            Player participant = Bukkit.getPlayer(participantId);
            if (participant == null) {
                continue;
            }
            for (String command : commands) {
                String prepared = command
                        .replace("{player}", participant.getName())
                        .replace("{tag}", winner.tag())
                        .replace("{arena}", arenaName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), prepared);
            }
        }
    }
}
