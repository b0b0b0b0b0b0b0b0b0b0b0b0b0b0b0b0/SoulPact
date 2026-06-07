package bm.b0b0b0.SoulPact.bank.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.api.treasury.TreasuryOperationResult;
import bm.b0b0b0.SoulPact.bank.config.BankConfig;
import bm.b0b0b0.SoulPact.bank.economy.VaultGateway;
import bm.b0b0b0.SoulPact.bank.message.BankMessages;
import bm.b0b0b0.SoulPact.bank.service.ClanTreasuryService;
import bm.b0b0b0.SoulPact.bank.util.MoneyFormat;
import java.util.Map;
import org.bukkit.entity.Player;

public final class BankGuiService {

    private final SoulPactApi api;
    private final BankConfig config;
    private final BankMessages messages;
    private final ClanTreasuryService treasuryService;
    private final VaultGateway vaultGateway;
    private final BankMenuPopulator populator;
    private final BankClickHandler clickHandler;

    public BankGuiService(
            SoulPactApi api,
            BankConfig config,
            BankMessages messages,
            ClanTreasuryService treasuryService,
            VaultGateway vaultGateway
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.treasuryService = treasuryService;
        this.vaultGateway = vaultGateway;
        this.populator = new BankMenuPopulator(config, messages);
        BankClanNavigation clanNavigation = new BankClanNavigation(api);
        this.clickHandler = new BankClickHandler(this, clanNavigation, config);
    }

    public BankClickHandler clickHandler() {
        return clickHandler;
    }

    public void open(Player player) {
        treasuryService.findPlayerClan(player).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (clanOptional.isEmpty()) {
                messages.send(player, "bank.not-in-clan");
                return;
            }
            openLoaded(player, clanOptional.get());
        }));
    }

    public void openLoaded(Player player, ClanSnapshot clan) {
        treasuryService.loadView(clan).thenCompose(view ->
                treasuryService.canDeposit(player, clan.id()).thenCombine(
                        treasuryService.canWithdraw(player, clan.id()),
                        (canDeposit, canWithdraw) -> new BankMenuSnapshot(view, canDeposit, canWithdraw)
                )
        ).thenAccept(snapshot -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            BankMenu menu = new BankMenu(config, populator, messages, player, snapshot);
            player.openInventory(menu.getInventory());
        }));
    }

    public void refresh(Player player, ClanSnapshot clan) {
        openLoaded(player, clan);
    }

    public void deposit(Player player, ClanSnapshot clan, double amount) {
        treasuryService.deposit(player, clan.id(), amount).thenAccept(result -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (result == TreasuryOperationResult.SUCCESS) {
                refresh(player, clan);
                return;
            }
            sendFailure(player, clan, amount, result, true);
        }));
    }

    public void withdraw(Player player, ClanSnapshot clan, double amount) {
        treasuryService.withdraw(player, clan.id(), amount).thenAccept(result -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (result == TreasuryOperationResult.SUCCESS) {
                refresh(player, clan);
                return;
            }
            sendFailure(player, clan, amount, result, false);
        }));
    }

    public void depositAll(Player player, ClanSnapshot clan) {
        if (!vaultGateway.available()) {
            messages.send(player, "bank.economy-unavailable");
            return;
        }
        deposit(player, clan, Math.min(vaultGateway.balance(player), config.maxDeposit()));
    }

    public void withdrawAll(Player player, ClanSnapshot clan, double treasuryBalance) {
        withdraw(player, clan, Math.min(treasuryBalance, config.maxWithdraw()));
    }

    private void sendFailure(
            Player player,
            ClanSnapshot clan,
            double amount,
            TreasuryOperationResult result,
            boolean deposit
    ) {
        String key = switch (result) {
            case NO_PERMISSION -> deposit ? "bank.no-permission-deposit" : "bank.no-permission-withdraw";
            case ECONOMY_UNAVAILABLE -> "bank.economy-unavailable";
            case INSUFFICIENT_PERSONAL_FUNDS -> "bank.insufficient-personal";
            case INSUFFICIENT_TREASURY_FUNDS -> "bank.insufficient-treasury";
            case INVALID_AMOUNT -> "bank.invalid-amount";
            case TREASURY_LOCKED -> "bank.locked";
            case NOT_IN_CLAN -> "bank.not-in-clan";
            case SUCCESS, FAILED -> "bank.failed";
        };
        messages.send(player, key, Map.of(
                "amount", MoneyFormat.format(amount),
                "tag", clan.tag(),
                "name", clan.name()
        ));
    }
}
