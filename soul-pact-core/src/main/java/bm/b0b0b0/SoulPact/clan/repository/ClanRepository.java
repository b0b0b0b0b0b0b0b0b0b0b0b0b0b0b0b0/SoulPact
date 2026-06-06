package bm.b0b0b0.SoulPact.clan.repository;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanListEntry;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ClanRepository {

    CompletableFuture<Optional<Clan>> findByTag(String tag);

    CompletableFuture<Optional<Clan>> findById(long clanId);

    CompletableFuture<Optional<Clan>> findByPlayerId(UUID playerId);

    CompletableFuture<Integer> countClans();

    CompletableFuture<List<Clan>> findAll(int limit);

    CompletableFuture<List<ClanListEntry>> findPageEntries(int offset, int limit);

    CompletableFuture<Clan> create(CreateClanRecord record);

    CompletableFuture<Integer> countMembers(long clanId);

    CompletableFuture<Boolean> removeMember(long clanId, UUID playerId);

    CompletableFuture<Boolean> addMember(long clanId, UUID playerId, String role, long joinedAt);

    CompletableFuture<Boolean> deleteClan(long clanId);

    CompletableFuture<List<ClanMember>> findMembersByClanId(long clanId);

    CompletableFuture<Boolean> updateJoinRequestsOpen(long clanId, boolean open);

    CompletableFuture<Boolean> updateMemberRole(long clanId, UUID playerId, String role);

    CompletableFuture<Boolean> transferLeadership(
            long clanId,
            UUID currentLeaderId,
            UUID newLeaderId,
            String formerLeaderRole
    );
}
