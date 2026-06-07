package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.role.RoleThemeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanSettingsDataService {

    private final ClanRepository clanRepository;
    private final RoleThemeService roleThemeService;
    private final ClanBannerService clanBannerService;

    public ClanSettingsDataService(
            ClanRepository clanRepository,
            RoleThemeService roleThemeService,
            ClanBannerService clanBannerService
    ) {
        this.clanRepository = clanRepository;
        this.roleThemeService = roleThemeService;
        this.clanBannerService = clanBannerService;
    }

    public CompletableFuture<Optional<ClanSettingsSnapshot>> load(Player player) {
        return clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            var clan = clanOptional.get();
            if (!ClanStaffPermissions.isLeader(clan, player.getUniqueId())) {
                return CompletableFuture.completedFuture(Optional.empty());
            }
            List<String> roleKeys = new ArrayList<>();
            for (String roleKey : roleThemeService.theme().order()) {
                if (ClanStaffPermissions.LEADER_ROLE.equals(roleKey)) {
                    continue;
                }
                roleKeys.add(roleKey);
            }
            return clanBannerService.loadBanner(clan.id()).thenApply(banner ->
                    Optional.of(new ClanSettingsSnapshot(clan, roleKeys, banner))
            );
        });
    }
}
