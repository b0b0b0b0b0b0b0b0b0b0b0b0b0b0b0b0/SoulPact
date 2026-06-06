package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import bm.b0b0b0.SoulPact.api.message.SoulPactMessages;
import bm.b0b0b0.SoulPact.clan.service.ClanQueryService;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.plugin.Plugin;

public final class SoulPactApiImpl implements SoulPactApi {

    private final Plugin plugin;
    private final SoulPactMessages messages;
    private final ExtensionRegistry extensionRegistry;
    private final DataSourceProvider dataSourceProvider;
    private final ClanQueryService clanQueryService;

    public SoulPactApiImpl(
            Plugin plugin,
            SoulPactMessages messages,
            ExtensionRegistry extensionRegistry,
            DataSourceProvider dataSourceProvider,
            ClanQueryService clanQueryService
    ) {
        this.plugin = plugin;
        this.messages = messages;
        this.extensionRegistry = extensionRegistry;
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
