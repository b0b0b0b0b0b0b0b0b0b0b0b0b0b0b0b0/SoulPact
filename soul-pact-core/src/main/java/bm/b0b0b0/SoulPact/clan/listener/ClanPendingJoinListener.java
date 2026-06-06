package bm.b0b0b0.SoulPact.clan.listener;

import bm.b0b0b0.SoulPact.clan.runtime.ClanRuntimeHolder;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class ClanPendingJoinListener implements Listener {

    private final ClanRuntimeHolder runtimeHolder;
    private final DataSourceProvider dataSourceProvider;

    public ClanPendingJoinListener(ClanRuntimeHolder runtimeHolder, DataSourceProvider dataSourceProvider) {
        this.runtimeHolder = runtimeHolder;
        this.dataSourceProvider = dataSourceProvider;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            return;
        }
        runtimeHolder.services().membershipService().deliverPending(event.getPlayer());
    }
}
