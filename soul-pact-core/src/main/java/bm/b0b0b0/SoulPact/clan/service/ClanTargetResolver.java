package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class ClanTargetResolver {

    private final ClanRepository clanRepository;

    public ClanTargetResolver(ClanRepository clanRepository) {
        this.clanRepository = clanRepository;
    }

    public Optional<Long> parseClanId(String rawTarget) {
        if (rawTarget == null) {
            return Optional.empty();
        }
        String normalized = rawTarget.trim();
        if (normalized.startsWith("#")) {
            normalized = normalized.substring(1).trim();
        }
        if (normalized.isEmpty()) {
            return Optional.empty();
        }
        try {
            long clanId = Long.parseLong(normalized);
            return clanId > 0 ? Optional.of(clanId) : Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    public CompletableFuture<Optional<Clan>> resolveClan(String rawTarget) {
        if (rawTarget == null || rawTarget.isBlank()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        Optional<Long> clanId = parseClanId(rawTarget);
        if (clanId.isPresent()) {
            return clanRepository.findById(clanId.get());
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(rawTarget.trim());
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        return clanRepository.findByPlayerId(offlinePlayer.getUniqueId())
                .thenApply(clanOptional -> clanOptional.filter(clan -> clan.leaderId().equals(offlinePlayer.getUniqueId())));
    }

    public Optional<java.util.UUID> resolvePlayerId(String playerName) {
        if (playerName == null || playerName.isBlank()) {
            return Optional.empty();
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName.trim());
        if (!offlinePlayer.hasPlayedBefore() && !offlinePlayer.isOnline()) {
            return Optional.empty();
        }
        return Optional.of(offlinePlayer.getUniqueId());
    }
}
