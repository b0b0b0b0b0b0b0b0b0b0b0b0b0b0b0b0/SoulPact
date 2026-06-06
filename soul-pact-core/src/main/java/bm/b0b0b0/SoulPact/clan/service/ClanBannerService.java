package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.banner.ClanBannerCodec;
import bm.b0b0b0.SoulPact.clan.banner.ClanBannerEditor;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanBannerRepository;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerService {

    private final ClanBannerRepository clanBannerRepository;

    public ClanBannerService(ClanBannerRepository clanBannerRepository) {
        this.clanBannerRepository = clanBannerRepository;
    }

    public CompletableFuture<ItemStack> loadBanner(long clanId) {
        return clanBannerRepository.findDataByClanId(clanId)
                .thenApply(dataOptional -> ClanBannerEditor.copy(
                        ClanBannerCodec.decode(dataOptional.orElse(null))
                ));
    }

    public CompletableFuture<Boolean> saveBanner(long clanId, ItemStack banner) {
        String encoded = ClanBannerCodec.encode(banner);
        return clanBannerRepository.updateData(clanId, encoded);
    }

    public boolean canEdit(Player player, Clan clan) {
        return clan.leaderId().equals(player.getUniqueId());
    }
}
