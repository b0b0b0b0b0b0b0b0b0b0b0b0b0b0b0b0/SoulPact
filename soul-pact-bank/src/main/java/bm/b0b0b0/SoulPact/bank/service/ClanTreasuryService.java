package bm.b0b0b0.SoulPact.bank.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanPermissionKeys;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryApi;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryContributorSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.ClanTreasuryEntrySnapshot;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.bank.config.BankConfig;
import bm.b0b0b0.SoulPact.bank.economy.VaultGateway;
import bm.b0b0b0.SoulPact.bank.message.BankMessages;
import bm.b0b0b0.SoulPact.bank.repository.ClanTreasuryRepository;
import bm.b0b0b0.SoulPact.bank.util.MoneyFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanTreasuryService implements ClanTreasuryApi {

    public static final String ENTRY_DEPOSIT = "DEPOSIT";
    public static final String ENTRY_WITHDRAW = "WITHDRAW";
    public static final String ENTRY_TRANSFER_IN = "TRANSFER_IN";
    public static final String ENTRY_TRANSFER_OUT = "TRANSFER_OUT";
    public static final String ENTRY_CHARGE = "CHARGE";

    private final SoulPactApi api;
    private final ClanTreasuryRepository repository;
    private final VaultGateway vaultGateway;
    private final BankConfig config;
    private final BankMessages messages;

    public ClanTreasuryService(
            SoulPactApi api,
            ClanTreasuryRepository repository,
            VaultGateway vaultGateway,
            BankConfig config,
            BankMessages messages
    ) {
        this.api = api;
        this.repository = repository;
        this.vaultGateway = vaultGateway;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public CompletableFuture<Double> balance(long clanId) {
        return api.scheduler().supplyAsync(() -> repository.ensureAccount(clanId));
    }

    @Override
    public CompletableFuture<Boolean> isLocked(long clanId) {
        return api.scheduler().supplyAsync(() -> repository.isLocked(clanId));
    }

    @Override
    public CompletableFuture<Boolean> setLocked(long clanId, boolean locked) {
        return api.scheduler().supplyAsync(() -> repository.setLocked(clanId, locked));
    }

    @Override
    public CompletableFuture<TreasuryOperationResult> deposit(Player player, long clanId, double amount) {
        return validateOperation(player, clanId, amount, ClanPermissionKeys.BANK_DEPOSIT, true).thenCompose(result -> {
            if (result != TreasuryOperationResult.SUCCESS) {
                return CompletableFuture.completedFuture(result);
            }
            return api.scheduler().supplyAsync(() -> executeDeposit(player, clanId, amount));
        });
    }

    @Override
    public CompletableFuture<TreasuryOperationResult> withdraw(Player player, long clanId, double amount) {
        return validateOperation(player, clanId, amount, ClanPermissionKeys.BANK_WITHDRAW, true).thenCompose(result -> {
            if (result != TreasuryOperationResult.SUCCESS) {
                return CompletableFuture.completedFuture(result);
            }
            return api.scheduler().supplyAsync(() -> executeWithdraw(player, clanId, amount));
        });
    }

    @Override
    public CompletableFuture<TreasuryOperationResult> charge(long clanId, UUID actorId, double amount, String note) {
        if (!isValidAmount(amount)) {
            return CompletableFuture.completedFuture(TreasuryOperationResult.INVALID_AMOUNT);
        }
        return isLocked(clanId).thenCompose(locked -> {
            if (locked) {
                return CompletableFuture.completedFuture(TreasuryOperationResult.TREASURY_LOCKED);
            }
            return api.scheduler().supplyAsync(() -> executeCharge(clanId, actorId, amount, note));
        });
    }

    @Override
    public CompletableFuture<TreasuryOperationResult> transferAll(long fromClanId, long toClanId, String note) {
        return api.scheduler().supplyAsync(() -> executeTransferAll(fromClanId, toClanId, note));
    }

    @Override
    public CompletableFuture<TreasuryOperationResult> seize(long fromClanId, long toClanId, double amount, String note) {
        if (!isValidAmount(amount)) {
            return CompletableFuture.completedFuture(TreasuryOperationResult.INVALID_AMOUNT);
        }
        return api.scheduler().supplyAsync(() -> executeSeize(fromClanId, toClanId, amount, note));
    }

    @Override
    public CompletableFuture<List<ClanTreasuryEntrySnapshot>> recentEntries(long clanId, int limit) {
        return api.scheduler().supplyAsync(() -> repository.recentEntries(clanId, limit));
    }

    @Override
    public CompletableFuture<List<ClanTreasuryContributorSnapshot>> topContributors(long clanId, int limit) {
        return api.scheduler().supplyAsync(() -> repository.topContributors(clanId, limit));
    }

    public CompletableFuture<Optional<ClanSnapshot>> findPlayerClan(Player player) {
        return api.findClanByPlayer(player.getUniqueId());
    }

    public CompletableFuture<TreasuryView> loadView(ClanSnapshot clan) {
        return api.scheduler().supplyAsync(() -> {
            double balance = repository.ensureAccount(clan.id());
            boolean locked = repository.isLocked(clan.id());
            List<ClanTreasuryEntrySnapshot> entries = repository.recentEntries(clan.id(), config.ledgerPreviewSize());
            List<ClanTreasuryContributorSnapshot> contributors = repository.topContributors(clan.id(), config.contributorTopSize());
            return new TreasuryView(clan, balance, locked, entries, contributors);
        });
    }

    public CompletableFuture<Boolean> canDeposit(Player player, long clanId) {
        return api.clanAccess().hasPermission(clanId, player.getUniqueId(), ClanPermissionKeys.BANK_DEPOSIT);
    }

    public CompletableFuture<Boolean> canWithdraw(Player player, long clanId) {
        return api.clanAccess().hasPermission(clanId, player.getUniqueId(), ClanPermissionKeys.BANK_WITHDRAW);
    }

    public void sendResult(Player player, TreasuryOperationResult result, Map<String, String> placeholders) {
        api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            String key = switch (result) {
                case SUCCESS -> placeholders.containsKey("success_key")
                        ? placeholders.get("success_key")
                        : "bank.failed";
                case NOT_IN_CLAN -> "bank.not-in-clan";
                case NO_PERMISSION -> placeholders.containsKey("permission_key")
                        ? placeholders.get("permission_key")
                        : "bank.failed";
                case ECONOMY_UNAVAILABLE -> "bank.economy-unavailable";
                case INSUFFICIENT_PERSONAL_FUNDS -> "bank.insufficient-personal";
                case INSUFFICIENT_TREASURY_FUNDS -> "bank.insufficient-treasury";
                case INVALID_AMOUNT -> "bank.invalid-amount";
                case TREASURY_LOCKED -> "bank.locked";
                case FAILED -> "bank.failed";
            };
            Map<String, String> messagePlaceholders = new java.util.HashMap<>(placeholders);
            messagePlaceholders.remove("success_key");
            messagePlaceholders.remove("permission_key");
            messages.send(player, key, messagePlaceholders);
        });
    }

    private CompletableFuture<TreasuryOperationResult> validateOperation(
            Player player,
            long clanId,
            double amount,
            String permissionKey,
            boolean checkLock
    ) {
        if (!vaultGateway.available()) {
            return CompletableFuture.completedFuture(TreasuryOperationResult.ECONOMY_UNAVAILABLE);
        }
        if (!isValidAmount(amount)) {
            return CompletableFuture.completedFuture(TreasuryOperationResult.INVALID_AMOUNT);
        }
        return api.findClanByPlayer(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty() || clanOptional.get().id() != clanId) {
                return CompletableFuture.completedFuture(TreasuryOperationResult.NOT_IN_CLAN);
            }
            CompletableFuture<Boolean> permissionFuture = api.clanAccess().hasPermission(clanId, player.getUniqueId(), permissionKey);
            CompletableFuture<Boolean> lockFuture = checkLock
                    ? isLocked(clanId)
                    : CompletableFuture.completedFuture(false);
            return permissionFuture.thenCombine(lockFuture, (allowed, locked) -> {
                if (!allowed) {
                    return TreasuryOperationResult.NO_PERMISSION;
                }
                if (locked) {
                    return TreasuryOperationResult.TREASURY_LOCKED;
                }
                return TreasuryOperationResult.SUCCESS;
            });
        });
    }

    private TreasuryOperationResult executeDeposit(Player player, long clanId, double amount) {
        double cappedAmount = capAmount(amount, config.maxDeposit());
        if (!vaultGateway.has(player, cappedAmount)) {
            return TreasuryOperationResult.INSUFFICIENT_PERSONAL_FUNDS;
        }
        if (!vaultGateway.withdraw(player, cappedAmount)) {
            return TreasuryOperationResult.FAILED;
        }
        ClanTreasuryRepository.TreasuryMutationResult mutationResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        clanId,
                        player.getUniqueId(),
                        ENTRY_DEPOSIT,
                        cappedAmount,
                        null,
                        true,
                        System.currentTimeMillis()
                )
        );
        if (!mutationResult.success()) {
            vaultGateway.deposit(player, cappedAmount);
            return TreasuryOperationResult.FAILED;
        }
        notifyDeposit(player, clanId, cappedAmount, mutationResult.balanceAfter());
        return TreasuryOperationResult.SUCCESS;
    }

    private TreasuryOperationResult executeWithdraw(Player player, long clanId, double amount) {
        double cappedAmount = capAmount(amount, config.maxWithdraw());
        ClanTreasuryRepository.TreasuryMutationResult mutationResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        clanId,
                        player.getUniqueId(),
                        ENTRY_WITHDRAW,
                        -cappedAmount,
                        null,
                        false,
                        System.currentTimeMillis()
                )
        );
        if (!mutationResult.success()) {
            return TreasuryOperationResult.INSUFFICIENT_TREASURY_FUNDS;
        }
        if (!vaultGateway.deposit(player, cappedAmount)) {
            repository.applyMutation(new ClanTreasuryRepository.TreasuryMutation(
                    clanId,
                    player.getUniqueId(),
                    ENTRY_DEPOSIT,
                    cappedAmount,
                    "rollback",
                    false,
                    System.currentTimeMillis()
            ));
            return TreasuryOperationResult.FAILED;
        }
        notifyWithdraw(player, clanId, cappedAmount, mutationResult.balanceAfter());
        return TreasuryOperationResult.SUCCESS;
    }

    private TreasuryOperationResult executeCharge(long clanId, UUID actorId, double amount, String note) {
        ClanTreasuryRepository.TreasuryMutationResult mutationResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        clanId,
                        actorId,
                        ENTRY_CHARGE,
                        -amount,
                        note,
                        false,
                        System.currentTimeMillis()
                )
        );
        if (!mutationResult.success()) {
            return TreasuryOperationResult.INSUFFICIENT_TREASURY_FUNDS;
        }
        return TreasuryOperationResult.SUCCESS;
    }

    private TreasuryOperationResult executeTransferAll(long fromClanId, long toClanId, String note) {
        if (fromClanId == toClanId) {
            return TreasuryOperationResult.FAILED;
        }
        repository.ensureAccount(fromClanId);
        repository.ensureAccount(toClanId);
        Optional<ClanTreasuryRepository.TreasuryState> sourceState = repository.findState(fromClanId);
        if (sourceState.isEmpty() || sourceState.get().balance() <= 0D) {
            return TreasuryOperationResult.SUCCESS;
        }
        double amount = sourceState.get().balance();
        UUID systemActor = UUID.fromString("00000000-0000-0000-0000-000000000000");
        long timestamp = System.currentTimeMillis();
        ClanTreasuryRepository.TreasuryMutationResult withdrawResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        fromClanId,
                        systemActor,
                        ENTRY_TRANSFER_OUT,
                        -amount,
                        note,
                        false,
                        timestamp
                )
        );
        if (!withdrawResult.success()) {
            return TreasuryOperationResult.FAILED;
        }
        ClanTreasuryRepository.TreasuryMutationResult depositResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        toClanId,
                        systemActor,
                        ENTRY_TRANSFER_IN,
                        amount,
                        note,
                        false,
                        timestamp
                )
        );
        if (!depositResult.success()) {
            repository.applyMutation(new ClanTreasuryRepository.TreasuryMutation(
                    fromClanId,
                    systemActor,
                    ENTRY_TRANSFER_IN,
                    amount,
                    "rollback",
                    false,
                    timestamp
            ));
            return TreasuryOperationResult.FAILED;
        }
        return TreasuryOperationResult.SUCCESS;
    }

    private TreasuryOperationResult executeSeize(long fromClanId, long toClanId, double amount, String note) {
        if (fromClanId == toClanId) {
            return TreasuryOperationResult.FAILED;
        }
        repository.ensureAccount(fromClanId);
        repository.ensureAccount(toClanId);
        UUID systemActor = UUID.fromString("00000000-0000-0000-0000-000000000000");
        long timestamp = System.currentTimeMillis();
        ClanTreasuryRepository.TreasuryMutationResult withdrawResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        fromClanId,
                        systemActor,
                        ENTRY_TRANSFER_OUT,
                        -amount,
                        note,
                        false,
                        timestamp
                )
        );
        if (!withdrawResult.success()) {
            return TreasuryOperationResult.INSUFFICIENT_TREASURY_FUNDS;
        }
        ClanTreasuryRepository.TreasuryMutationResult depositResult = repository.applyMutation(
                new ClanTreasuryRepository.TreasuryMutation(
                        toClanId,
                        systemActor,
                        ENTRY_TRANSFER_IN,
                        amount,
                        note,
                        false,
                        timestamp
                )
        );
        if (!depositResult.success()) {
            repository.applyMutation(new ClanTreasuryRepository.TreasuryMutation(
                    fromClanId,
                    systemActor,
                    ENTRY_TRANSFER_IN,
                    amount,
                    "seize-rollback",
                    false,
                    timestamp
            ));
            return TreasuryOperationResult.FAILED;
        }
        return TreasuryOperationResult.SUCCESS;
    }

    private void notifyDeposit(Player player, long clanId, double amount, double balanceAfter) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (clanOptional.isEmpty() || !player.isOnline()) {
                return;
            }
            ClanSnapshot clan = clanOptional.get();
            Map<String, String> placeholders = Map.of(
                    "amount", MoneyFormat.format(amount),
                    "balance", MoneyFormat.format(balanceAfter),
                    "tag", clan.tag(),
                    "player", player.getName()
            );
            messages.send(player, "bank.deposit-success", placeholders);
            if (amount < config.notifyDepositAbove()) {
                return;
            }
            for (Player online : Bukkit.getOnlinePlayers()) {
                api.findClanByPlayer(online.getUniqueId()).thenAccept(viewerClan -> {
                    if (viewerClan.isEmpty() || viewerClan.get().id() != clanId) {
                        return;
                    }
                    api.scheduler().runSync(() -> messages.send(online, "bank.deposit-broadcast", placeholders));
                });
            }
        }));
    }

    private void notifyWithdraw(Player player, long clanId, double amount, double balanceAfter) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (clanOptional.isEmpty() || !player.isOnline()) {
                return;
            }
            ClanSnapshot clan = clanOptional.get();
            Map<String, String> placeholders = Map.of(
                    "amount", MoneyFormat.format(amount),
                    "balance", MoneyFormat.format(balanceAfter),
                    "tag", clan.tag(),
                    "player", player.getName()
            );
            messages.send(player, "bank.withdraw-success", placeholders);
            for (Player online : Bukkit.getOnlinePlayers()) {
                api.findClanByPlayer(online.getUniqueId()).thenAccept(viewerClan -> {
                    if (viewerClan.isEmpty() || viewerClan.get().id() != clanId) {
                        return;
                    }
                    api.scheduler().runSync(() -> messages.send(online, "bank.withdraw-broadcast", placeholders));
                });
            }
        }));
    }

    private boolean isValidAmount(double amount) {
        return amount >= config.minAmount() && Double.isFinite(amount);
    }

    private double capAmount(double amount, double maxAmount) {
        return Math.min(amount, maxAmount);
    }

    public record TreasuryView(
            ClanSnapshot clan,
            double balance,
            boolean locked,
            List<ClanTreasuryEntrySnapshot> recentEntries,
            List<ClanTreasuryContributorSnapshot> topContributors
    ) {
    }
}
