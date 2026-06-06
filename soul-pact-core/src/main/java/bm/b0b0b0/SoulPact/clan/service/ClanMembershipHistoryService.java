package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanMembershipHistoryRepository;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClanMembershipHistoryService {

    private final ClanMembershipHistoryRepository historyRepository;

    public ClanMembershipHistoryService(ClanMembershipHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public CompletableFuture<Void> recordLeave(Clan clan, ClanMember member, long leftAt) {
        return historyRepository.record(
                member.playerId(),
                clan.id(),
                clan.tag(),
                clan.name(),
                member.role(),
                member.joinedAt(),
                leftAt,
                "leave"
        );
    }

    public CompletableFuture<Void> recordKick(Clan clan, ClanMember member, long leftAt) {
        return historyRepository.record(
                member.playerId(),
                clan.id(),
                clan.tag(),
                clan.name(),
                member.role(),
                member.joinedAt(),
                leftAt,
                "kick"
        );
    }

    public CompletableFuture<Void> recordDisband(Clan clan, List<ClanMember> members, long leftAt) {
        CompletableFuture<Void> chain = CompletableFuture.completedFuture(null);
        for (ClanMember member : members) {
            chain = chain.thenCompose(ignored -> historyRepository.record(
                    member.playerId(),
                    clan.id(),
                    clan.tag(),
                    clan.name(),
                    member.role(),
                    member.joinedAt(),
                    leftAt,
                    "disband"
            ));
        }
        return chain;
    }
}
