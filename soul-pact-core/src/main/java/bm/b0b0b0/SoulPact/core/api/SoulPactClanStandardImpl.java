package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.clan.SoulPactClanStandard;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardService;
import java.util.UUID;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class SoulPactClanStandardImpl implements SoulPactClanStandard {

    private final ClanStandardService clanStandardService;

    public SoulPactClanStandardImpl(ClanStandardService clanStandardService) {
        this.clanStandardService = clanStandardService;
    }

    @Override
    public boolean isStandard(ItemStack itemStack) {
        return clanStandardService.items().isStandard(itemStack);
    }

    @Override
    public Long readClanId(ItemStack itemStack) {
        return clanStandardService.items().readClanIdFromItem(itemStack);
    }

    @Override
    public String readClanTag(ItemStack itemStack) {
        return clanStandardService.items().readClanTagFromItem(itemStack);
    }

    @Override
    public Long readClanIdFromBlock(BlockState blockState) {
        return clanStandardService.items().readClanIdFromBlock(blockState);
    }

    @Override
    public String readClanTagFromBlock(BlockState blockState) {
        return clanStandardService.items().readClanTagFromBlock(blockState);
    }

    @Override
    public void trackInventory(long clanId, UUID playerId) {
        clanStandardService.presence().trackInventory(clanId, playerId);
    }

    @Override
    public void restoreToPlayer(Player player, long clanId, String clanTag) {
        clanStandardService.restoreStandardToPlayer(player, clanId, clanTag);
    }
}
