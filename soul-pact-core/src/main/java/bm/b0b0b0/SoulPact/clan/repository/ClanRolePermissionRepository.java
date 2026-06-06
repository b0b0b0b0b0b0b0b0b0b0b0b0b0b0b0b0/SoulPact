package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.ClanRolePermissionMap;
import java.util.concurrent.CompletableFuture;

public interface ClanRolePermissionRepository {

    CompletableFuture<ClanRolePermissionMap> findByClanId(long clanId);

    CompletableFuture<Void> upsert(long clanId, String role, String permission, boolean enabled);

    CompletableFuture<Void> seedDefaults(long clanId, ClanRolePermissionMap defaults);
}
