package bm.b0b0b0.SoulPact.core.module;

import bm.b0b0b0.SoulPact.api.SoulPactExtension;
import bm.b0b0b0.SoulPact.api.land.ClanLandProvider;
import java.util.UUID;

public final class ClanExtensionMembershipNotifier {

    private final ExtensionRegistryImpl extensionRegistry;

    public ClanExtensionMembershipNotifier(ExtensionRegistryImpl extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    public void memberJoined(long clanId, UUID playerId) {
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (extension instanceof ClanLandProvider landProvider) {
                landProvider.onMemberJoined(clanId, playerId);
            }
        }
    }

    public void memberLeft(long clanId, UUID playerId) {
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (extension instanceof ClanLandProvider landProvider) {
                landProvider.onMemberLeft(clanId, playerId);
            }
        }
    }

    public void leadershipTransferred(long clanId, UUID previousLeaderId, UUID newLeaderId) {
        for (SoulPactExtension extension : extensionRegistry.all()) {
            if (extension instanceof ClanLandProvider landProvider) {
                landProvider.onLeadershipTransferred(clanId, previousLeaderId, newLeaderId);
            }
        }
    }
}
