package bm.b0b0b0.SoulPact.api.coalition;

import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public interface CoalitionDisplayBridge {

    CompletableFuture<String> coalitionLineForList(long clanId);

    CompletableFuture<CoalitionDisplayExtras> enrichInfoView(Player viewer, long targetClanId);

    void handleInfoInviteClick(Player player, long targetClanId, int listPage);
}
