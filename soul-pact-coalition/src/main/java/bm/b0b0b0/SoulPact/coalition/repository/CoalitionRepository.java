package bm.b0b0b0.SoulPact.coalition.repository;

import bm.b0b0b0.SoulPact.coalition.model.CoalitionInviteRecord;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoalitionRepository {

    List<Long> listAllMemberClanIds();

    Optional<Long> findCoalitionIdByClan(long clanId);

    List<Long> listMemberClanIds(long coalitionId);

    long createCoalition(long createdAt);

    void addMember(long coalitionId, long clanId, long joinedAt);

    void removeMember(long clanId);

    int countMembers(long coalitionId);

    Optional<CoalitionInviteRecord> findPendingInvite(long inviteId);

    Optional<CoalitionInviteRecord> findPendingInviteForTarget(long targetClanId, long inviteId);

    List<CoalitionInviteRecord> listPendingForTarget(long targetClanId);

    long createInvite(
            long coalitionId,
            long inviterClanId,
            long targetClanId,
            UUID invitedBy,
            long createdAt,
            String status
    );

    void updateInviteStatus(long inviteId, String status);

    void deleteInvitesForClan(long clanId);

    boolean isInviteBlocked(long targetClanId, long inviterClanId);

    void blockInviter(long targetClanId, long inviterClanId, long blockedAt);
}
