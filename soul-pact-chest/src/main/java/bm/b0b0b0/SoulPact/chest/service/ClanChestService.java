package bm.b0b0b0.SoulPact.chest.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryProvider;
import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import bm.b0b0b0.SoulPact.chest.gui.ChestMenuSnapshot;
import bm.b0b0b0.SoulPact.chest.message.ChestMessages;
import bm.b0b0b0.SoulPact.chest.repository.ClanChestRepository;
import bm.b0b0b0.SoulPact.chest.util.MoneyFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClanChestService {

    private final SoulPactApi api;
    private final ChestConfig config;
    private final ChestMessages messages;
    private final ClanChestRepository repository;
    private final ChestAccessService accessService;
    private final ChestPaymentService paymentService;

    public ClanChestService(
            SoulPactApi api,
            ChestConfig config,
            ChestMessages messages,
            ClanChestRepository repository,
            ChestAccessService accessService,
            ChestPaymentService paymentService
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.repository = repository;
        this.accessService = accessService;
        this.paymentService = paymentService;
    }

    public CompletableFuture<ChestMenuSnapshot> loadSnapshot(Player player, ClanSnapshot clan, int page) {
        int normalizedPage = normalizePage(page);
        return api.scheduler().supplyAsync(() -> {
            int unlocked = repository.unlockedCells(clan.id());
            Map<Integer, ItemStack> items = repository.loadItems(clan.id());
            return new LoadedChestData(unlocked, items);
        }).thenCompose(data -> accessService.canDeposit(player.getUniqueId(), clan.id())
                .thenCombine(
                        accessService.canWithdraw(player.getUniqueId(), clan.id()),
                        (canDeposit, canWithdraw) -> buildSnapshot(
                                clan,
                                normalizedPage,
                                data,
                                clan.leaderId().equals(player.getUniqueId()),
                                canDeposit,
                                canWithdraw
                        )
                ));
    }

    public CompletableFuture<Boolean> purchaseNextCell(Player player, ChestMenuSnapshot snapshot) {
        if (!snapshot.leader()) {
            messages.send(player, "chest.error.not-leader");
            return CompletableFuture.completedFuture(false);
        }
        if (snapshot.unlockedCells() >= snapshot.maxCells()) {
            messages.send(player, "chest.error.max-cells");
            return CompletableFuture.completedFuture(false);
        }
        double cost = config.pricing().costForCell(snapshot.unlockedCells());
        int purchasedCell = snapshot.unlockedCells() + 1;
        return paymentService.charge(player, snapshot.clan().id(), cost).thenCompose(result ->
                api.scheduler().supplyAsync(() -> {
                    if (result != ChestPaymentResult.SUCCESS) {
                        return PurchaseResult.failure(result);
                    }
                    repository.setUnlockedCells(snapshot.clan().id(), purchasedCell);
                    return PurchaseResult.success(purchasedCell, cost);
                }).thenApply(purchaseResult -> {
                    api.scheduler().runSync(() -> {
                        if (!player.isOnline()) {
                            return;
                        }
                        if (!purchaseResult.success()) {
                            notifyPaymentFailure(player, purchaseResult.paymentResult());
                            return;
                        }
                        messages.send(player, "chest.cell.purchased", Map.of(
                                "cell", String.valueOf(purchaseResult.purchasedCell()),
                                "cost", MoneyFormat.format(purchaseResult.cost()),
                                "source", messages.resolve(
                                        player,
                                        paymentService.usesTreasury()
                                                ? "chest.payment.source.treasury"
                                                : "chest.payment.source.leader"
                                )
                        ));
                    });
                    return purchaseResult.success();
                })
        );
    }

    public void saveItems(long clanId, Map<Integer, ItemStack> items) {
        api.scheduler().runAsync(() -> repository.saveItems(clanId, cloneItems(items)));
    }

    public boolean bankAvailable() {
        return api.extensions().find("bank").filter(ClanTreasuryProvider.class::isInstance).isPresent();
    }

    private ChestMenuSnapshot buildSnapshot(
            ClanSnapshot clan,
            int page,
            LoadedChestData data,
            boolean leader,
            boolean canDeposit,
            boolean canWithdraw
    ) {
        double nextCost = data.unlocked >= config.maxCells()
                ? 0.0D
                : config.pricing().costForCell(data.unlocked);
        return new ChestMenuSnapshot(
                clan,
                page,
                data.unlocked,
                config.maxCells(),
                data.items,
                leader,
                canDeposit,
                canWithdraw,
                bankAvailable(),
                nextCost
        );
    }

    private int normalizePage(int page) {
        if (page < 0) {
            return 0;
        }
        if (page >= config.pages()) {
            return config.pages() - 1;
        }
        return page;
    }

    private record PurchaseResult(boolean success, int purchasedCell, double cost, ChestPaymentResult paymentResult) {

        static PurchaseResult success(int purchasedCell, double cost) {
            return new PurchaseResult(true, purchasedCell, cost, ChestPaymentResult.SUCCESS);
        }

        static PurchaseResult failure(ChestPaymentResult paymentResult) {
            return new PurchaseResult(false, 0, 0.0D, paymentResult);
        }
    }

    private void notifyPaymentFailure(Player player, ChestPaymentResult result) {
        String key = switch (result) {
            case TREASURY_INSUFFICIENT -> "chest.payment.insufficient-treasury";
            case LEADER_INSUFFICIENT -> "chest.payment.insufficient-leader";
            case TREASURY_LOCKED -> "chest.payment.treasury-locked";
            case ECONOMY_UNAVAILABLE -> "chest.payment.economy-unavailable";
            case FAILED -> "chest.error.failed";
            case SUCCESS -> "chest.error.failed";
        };
        messages.send(player, key);
    }

    private static Map<Integer, ItemStack> cloneItems(Map<Integer, ItemStack> source) {
        Map<Integer, ItemStack> copy = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : source.entrySet()) {
            ItemStack value = entry.getValue();
            if (value != null && !value.getType().isAir()) {
                copy.put(entry.getKey(), value.clone());
            }
        }
        return copy;
    }

    private record LoadedChestData(int unlocked, Map<Integer, ItemStack> items) {
    }
}
