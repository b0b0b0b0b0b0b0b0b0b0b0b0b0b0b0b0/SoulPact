package bm.b0b0b0.SoulPact.api.platform;

import bm.b0b0b0.SoulPact.api.clan.ClanMemberSnapshot;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SoulPactClanAccess {

    CompletableFuture<Optional<ClanMemberSnapshot>> findMember(long clanId, UUID playerId);

    CompletableFuture<Boolean> hasPermission(long clanId, UUID playerId, String permissionKey);

    boolean hasPermissionSync(long clanId, UUID playerId, String permissionKey);
}
