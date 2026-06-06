package bm.b0b0b0.SoulPact.clan.standard;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanBannerRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.service.ClanBannerService;
import bm.b0b0b0.SoulPact.clan.service.ClanDisbandService;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClanStandardService {

    private final ClanRepository clanRepository;
    private final ClanBannerService clanBannerService;
    private final ClanBannerRepository clanBannerRepository;
    private final ClanStandardItem clanStandardItem;
    private final ClanStandardPresence presence;
    private final ClanDisbandService clanDisbandService;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final Set<Long> disbandInProgress = ConcurrentHashMap.newKeySet();

    public ClanStandardService(
            ClanRepository clanRepository,
            ClanBannerService clanBannerService,
            ClanBannerRepository clanBannerRepository,
            ClanStandardItem clanStandardItem,
            ClanStandardPresence presence,
            ClanDisbandService clanDisbandService,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.clanBannerService = clanBannerService;
        this.clanBannerRepository = clanBannerRepository;
        this.clanStandardItem = clanStandardItem;
        this.presence = presence;
        this.clanDisbandService = clanDisbandService;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public ClanStandardItem items() {
        return clanStandardItem;
    }

    public ClanStandardPresence presence() {
        return presence;
    }

    public boolean isStandardOut(long clanId) {
        return presence.isTracked(clanId);
    }

    public boolean canDepositStandard(Player player, long clanId) {
        return containsStandard(player, clanId);
    }

    public CompletableFuture<TakeStandardResult> takeStandard(Player player, long clanId) {
        return clanRepository.findById(clanId).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                return CompletableFuture.completedFuture(TakeStandardResult.FAILED);
            }
            return takeStandard(player, clanOptional.get());
        });
    }

    public CompletableFuture<TakeStandardResult> takeStandard(Player player, Clan clan) {
        if (!clan.leaderId().equals(player.getUniqueId())) {
            return CompletableFuture.completedFuture(TakeStandardResult.NOT_LEADER);
        }
        if (presence.isTracked(clan.id()) || containsStandard(player, clan.id())) {
            return CompletableFuture.completedFuture(TakeStandardResult.ALREADY_EXISTS);
        }
        return clanBannerService.loadBanner(clan.id()).thenCompose(banner ->
                clanBannerRepository.markStandardIssued(clan.id()).thenApply(marked -> {
                    if (!marked) {
                        return TakeStandardResult.FAILED;
                    }
                    ItemStack standard = clanStandardItem.create(player, banner, clan.id(), clan.tag());
                    Map<Integer, ItemStack> leftovers = player.getInventory().addItem(standard);
                    if (!leftovers.isEmpty()) {
                        leftovers.values().forEach(leftover -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
                    }
                    presence.trackInventory(clan.id(), player.getUniqueId());
                    return TakeStandardResult.SUCCESS;
                })
        );
    }

    public void handleStandardDestroyed(long clanId) {
        clanBannerRepository.isStandardIssued(clanId).thenAccept(issued -> {
            if ((!issued && !presence.isTracked(clanId)) || !disbandInProgress.add(clanId)) {
                return;
            }
            presence.clear(clanId);
            clanDisbandService.disbandByStandardLoss(clanId);
        });
    }

    public void recoverOnlineStandards(Player player) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            Long clanId = clanStandardItem.readClanIdFromItem(itemStack);
            if (clanId == null) {
                continue;
            }
            presence.trackInventory(clanId, player.getUniqueId());
        }
    }

    public void refreshTrackedStandard(Player player, long clanId, String clanTag, ItemStack bannerDesign) {
        if (!presence.isTracked(clanId)) {
            return;
        }
        replaceInventoryStandard(player, clanId, clanTag, bannerDesign);
    }

    public DepositStandardResult depositStandard(Player player, long clanId) {
        if (!containsStandard(player, clanId)) {
            return DepositStandardResult.NOT_IN_INVENTORY;
        }
        if (!removeStandardFromInventory(player, clanId)) {
            return DepositStandardResult.FAILED;
        }
        presence.clear(clanId);
        return DepositStandardResult.SUCCESS;
    }

    public void sendDepositResult(Player player, DepositStandardResult result, Map<String, String> placeholders) {
        asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            String key = switch (result) {
                case SUCCESS -> "clan.standard.deposit.success";
                case NOT_IN_INVENTORY -> "clan.standard.deposit.not-in-inventory";
                case FAILED -> "clan.standard.deposit.failed";
            };
            messageService.send(player, key, placeholders == null ? Map.of() : placeholders);
        });
    }

    public void sendTakeResult(Player player, TakeStandardResult result, Map<String, String> placeholders) {
        asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            String key = switch (result) {
                case SUCCESS -> "clan.standard.take.success";
                case NOT_LEADER -> "clan.banner.not-leader";
                case ALREADY_EXISTS -> "clan.standard.take.already-exists";
                case FAILED -> "clan.standard.take.failed";
            };
            messageService.send(player, key, placeholders == null ? Map.of() : placeholders);
        });
    }

    private void replaceInventoryStandard(Player player, long clanId, String clanTag, ItemStack bannerDesign) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack itemStack = contents[slot];
            Long itemClanId = clanStandardItem.readClanIdFromItem(itemStack);
            if (itemClanId == null || itemClanId != clanId) {
                continue;
            }
            player.getInventory().setItem(slot, clanStandardItem.refreshAppearance(player, itemStack, bannerDesign, clanTag));
            presence.trackInventory(clanId, player.getUniqueId());
            return;
        }
    }

    private boolean containsStandard(Player player, long clanId) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            Long itemClanId = clanStandardItem.readClanIdFromItem(itemStack);
            if (itemClanId != null && itemClanId == clanId) {
                return true;
            }
        }
        return false;
    }

    private boolean removeStandardFromInventory(Player player, long clanId) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int slot = 0; slot < contents.length; slot++) {
            ItemStack itemStack = contents[slot];
            Long itemClanId = clanStandardItem.readClanIdFromItem(itemStack);
            if (itemClanId == null || itemClanId != clanId) {
                continue;
            }
            player.getInventory().setItem(slot, null);
            return true;
        }
        return false;
    }

    public enum TakeStandardResult {
        SUCCESS,
        NOT_LEADER,
        ALREADY_EXISTS,
        FAILED
    }

    public enum DepositStandardResult {
        SUCCESS,
        NOT_IN_INVENTORY,
        FAILED
    }
}
