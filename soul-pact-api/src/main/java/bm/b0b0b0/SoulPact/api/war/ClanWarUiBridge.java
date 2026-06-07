package bm.b0b0b0.SoulPact.api.war;

import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public interface ClanWarUiBridge {

    CompletableFuture<String> treasuryLineForList(long clanId);

    CompletableFuture<ClanWarInfoExtras> enrichInfoView(Player viewer, long targetClanId);

    void openDeclareConfirm(Player player, long targetClanId, int listPage);

    void handleInfoDeclareClick(Player player, long targetClanId, int listPage);

    CompletableFuture<Integer> pendingCountForLeader(long defenderClanId);

    void openPendingWars(Player player);

    void openWarHub(Player player);

    boolean available();
}
