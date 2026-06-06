package bm.b0b0b0.SoulPact.api;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import bm.b0b0b0.SoulPact.api.message.SoulPactMessages;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.plugin.Plugin;

public interface SoulPactApi {

    Plugin plugin();

    SoulPactMessages messages();

    ExtensionRegistry extensions();

    boolean isDatabaseReady();

    CompletableFuture<Optional<ClanSnapshot>> findClanByTag(String tag);

    CompletableFuture<Optional<ClanSnapshot>> findClanByPlayer(UUID playerId);
}
