package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import bm.b0b0b0.SoulPact.api.message.SoulPactMessages;
import bm.b0b0b0.SoulPact.api.platform.SoulPactClanAccess;
import bm.b0b0b0.SoulPact.api.platform.SoulPactClanGui;
import bm.b0b0b0.SoulPact.api.platform.SoulPactScheduler;
import bm.b0b0b0.SoulPact.clan.service.ClanQueryService;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import org.bukkit.plugin.Plugin;

public final class SoulPactApiImpl implements SoulPactApi {

    private final Plugin plugin;
    private final SoulPactMessages messages;
    private final ExtensionRegistry extensionRegistry;
    private final SoulPactScheduler scheduler;
    private final SoulPactClanAccess clanAccess;
    private final SoulPactClanGui clanGui;
    private final DataSourceProvider dataSourceProvider;
    private final ClanQueryService clanQueryService;

    public SoulPactApiImpl(
            Plugin plugin,
            SoulPactMessages messages,
            ExtensionRegistry extensionRegistry,
            SoulPactScheduler scheduler,
            SoulPactClanAccess clanAccess,
            SoulPactClanGui clanGui,
            DataSourceProvider dataSourceProvider,
            ClanQueryService clanQueryService
    ) {
        this.plugin = plugin;
        this.messages = messages;
        this.extensionRegistry = extensionRegistry;
        this.scheduler = scheduler;
        this.clanAccess = clanAccess;
        this.clanGui = clanGui;
        this.dataSourceProvider = dataSourceProvider;
        this.clanQueryService = clanQueryService;
    }

    @Override
    public Plugin plugin() {
        return plugin;
    }

    @Override
    public SoulPactMessages messages() {
        return messages;
    }

    @Override
    public ExtensionRegistry extensions() {
        return extensionRegistry;
    }

    @Override
    public SoulPactScheduler scheduler() {
        return scheduler;
    }

    @Override
    public SoulPactClanAccess clanAccess() {
        return clanAccess;
    }

    @Override
    public SoulPactClanGui clanGui() {
        return clanGui;
    }

    @Override
    public DataSource dataSource() {
        return dataSourceProvider.dataSource();
    }

    @Override
    public boolean isDatabaseReady() {
        return dataSourceProvider.isReady();
    }

    @Override
    public CompletableFuture<Optional<ClanSnapshot>> findClanByTag(String tag) {
        return clanQueryService.findByTag(tag);
    }

    @Override
    public CompletableFuture<Optional<ClanSnapshot>> findClanByPlayer(UUID playerId) {
        return clanQueryService.findByPlayerId(playerId);
    }
}
