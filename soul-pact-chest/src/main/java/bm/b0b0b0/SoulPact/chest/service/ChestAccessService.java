package bm.b0b0b0.SoulPact.chest.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ChestAccessService {

    private final SoulPactApi api;

    public ChestAccessService(SoulPactApi api) {
        this.api = api;
    }

    public CompletableFuture<Boolean> canDeposit(UUID playerId, long clanId) {
        return api.clanAccess().hasPermission(clanId, playerId, ClanPermissionKeys.CHEST_DEPOSIT);
    }

    public CompletableFuture<Boolean> canWithdraw(UUID playerId, long clanId) {
        return api.clanAccess().hasPermission(clanId, playerId, ClanPermissionKeys.CHEST_WITHDRAW);
    }
}
