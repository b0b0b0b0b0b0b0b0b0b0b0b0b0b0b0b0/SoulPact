package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.coalition.CoalitionDisplayExtras;
import bm.b0b0b0.SoulPact.api.war.ClanWarInfoExtras;
import bm.b0b0b0.SoulPact.api.war.ClanWarUiBridge;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanInfoViewDataService {

    private final ClanRepository clanRepository;
    private final ClanWarAccessService warAccessService;
    private final ClanTreasuryDisplayService treasuryDisplayService;
    private final ClanCoalitionDisplayService coalitionDisplayService;

    public ClanInfoViewDataService(
            ClanRepository clanRepository,
            ClanWarAccessService warAccessService,
            ClanTreasuryDisplayService treasuryDisplayService,
            ClanCoalitionDisplayService coalitionDisplayService
    ) {
        this.clanRepository = clanRepository;
        this.warAccessService = warAccessService;
        this.treasuryDisplayService = treasuryDisplayService;
        this.coalitionDisplayService = coalitionDisplayService;
    }

    public CompletableFuture<Optional<ClanInfoViewSnapshot>> load(Player viewer, long clanId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            Clan clan = clanOptional.get();
            return clanRepository.countMembers(clan.id()).thenCombine(
                    clanRepository.findByPlayerId(viewer.getUniqueId()),
                    (memberCount, viewerClanOptional) -> buildSnapshot(clan, memberCount, viewer, viewerClanOptional.orElse(null))
            ).thenCompose(baseSnapshot -> enrichSnapshot(viewer, clanId, baseSnapshot));
        });
    }

    private CompletableFuture<Optional<ClanInfoViewSnapshot>> enrichSnapshot(
            Player viewer,
            long clanId,
            ClanInfoViewSnapshot baseSnapshot
    ) {
        CompletableFuture<ClanInfoViewSnapshot> warFuture = enrichWar(viewer, clanId, baseSnapshot);
        return warFuture.thenCompose(warSnapshot -> coalitionDisplayService.alliesForInfo(clanId)
                .thenApply(extras -> Optional.of(mergeCoalitionExtras(warSnapshot, extras))));
    }

    private CompletableFuture<ClanInfoViewSnapshot> enrichWar(Player viewer, long clanId, ClanInfoViewSnapshot baseSnapshot) {
        Optional<ClanWarUiBridge> warUiOptional = warAccessService.resolveUi();
        if (warUiOptional.isPresent()) {
            return warUiOptional.get().enrichInfoView(viewer, clanId).thenApply(extras ->
                    mergeWarExtras(baseSnapshot, extras)
            );
        }
        return treasuryDisplayService.formatBalance(clanId).thenApply(treasury ->
                new ClanInfoViewSnapshot(
                        baseSnapshot.clan(),
                        baseSnapshot.memberCount(),
                        baseSnapshot.viewerRole(),
                        treasury,
                        false,
                        "",
                        List.of()
                )
        );
    }

    private ClanInfoViewSnapshot mergeWarExtras(ClanInfoViewSnapshot base, ClanWarInfoExtras extras) {
        return new ClanInfoViewSnapshot(
                base.clan(),
                base.memberCount(),
                base.viewerRole(),
                extras.treasuryLine(),
                extras.showDeclareWar(),
                "",
                List.of()
        );
    }

    private ClanInfoViewSnapshot mergeCoalitionExtras(ClanInfoViewSnapshot base, CoalitionDisplayExtras extras) {
        return new ClanInfoViewSnapshot(
                base.clan(),
                base.memberCount(),
                base.viewerRole(),
                base.treasuryLine(),
                base.showDeclareWar(),
                extras.coalitionLine(),
                extras.allies()
        );
    }

    private ClanInfoViewSnapshot buildSnapshot(
            Clan clan,
            int memberCount,
            Player viewer,
            Clan viewerClan
    ) {
        if (viewerClan == null) {
            return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.NONE);
        }
        if (viewerClan.id() != clan.id()) {
            return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.FOREIGN);
        }
        if (clan.leaderId().equals(viewer.getUniqueId())) {
            return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.LEADER);
        }
        return new ClanInfoViewSnapshot(clan, memberCount, ClanInfoViewSnapshot.ViewerRole.MEMBER);
    }
}
