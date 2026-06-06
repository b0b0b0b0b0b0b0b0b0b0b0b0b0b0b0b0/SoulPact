package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.gui.ClanGuiOpenService;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.clan.repository.CreateClanRecord;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.config.EconomyConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class ClanCreateService {

    private final ClanRepository clanRepository;
    private final ClanConfig clanConfig;
    private final EconomyConfig economyConfig;
    private final ClanCreateValidator validator;
    private final ClanCreateEconomy createEconomy;
    private final ClanEconomyMessages clanEconomyMessages;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ClanGuiOpenService guiOpenService;
    private final ClanRolePermissionService rolePermissionService;

    public ClanCreateService(
            ClanRepository clanRepository,
            ClanConfig clanConfig,
            EconomyConfig economyConfig,
            ClanCreateValidator validator,
            ClanCreateEconomy createEconomy,
            ClanEconomyMessages clanEconomyMessages,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor,
            ClanGuiOpenService guiOpenService,
            ClanRolePermissionService rolePermissionService
    ) {
        this.clanRepository = clanRepository;
        this.clanConfig = clanConfig;
        this.economyConfig = economyConfig;
        this.validator = validator;
        this.createEconomy = createEconomy;
        this.clanEconomyMessages = clanEconomyMessages;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
        this.guiOpenService = guiOpenService;
        this.rolePermissionService = rolePermissionService;
    }

    public void sendUsageHint(Player player) {
        messageService.send(player, "clan.create.usage");
    }

    public void sendMissingNameHint(Player player, String rawTag) {
        String tag = rawTag == null || rawTag.isBlank() ? "TAG" : validator.normalizeTag(rawTag);
        messageService.send(player, "clan.create.missing-name", Map.of("tag", tag));
    }

    public void sendCreateHint(Player player) {
        sendUsageHint(player);
    }

    public void create(Player player, String rawTag, String rawName) {
        Optional<String> tagError = validator.validateTag(rawTag, clanConfig);
        if (tagError.isPresent()) {
            messageService.send(player, tagError.get());
            return;
        }
        Optional<String> nameError = validator.validateName(rawName, clanConfig);
        if (nameError.isPresent()) {
            messageService.send(player, nameError.get());
            return;
        }
        String tag = validator.normalizeTag(rawTag);
        String name = rawName.trim();
        clanRepository.findByPlayerId(player.getUniqueId())
                .thenCompose(existingClan -> {
                    if (existingClan.isPresent()) {
                        asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.create.already-in-clan"));
                        return CompletableFuture.completedFuture(null);
                    }
                    return clanRepository.findByTag(tag).thenCompose(tagClan -> {
                        if (tagClan.isPresent()) {
                            asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.create.tag-taken", Map.of("tag", tag)));
                            return CompletableFuture.completedFuture(null);
                        }
                        return chargeAndPersist(player, tag, name);
                    });
                })
                .exceptionally(throwable -> {
                    asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.create.failed"));
                    return null;
                });
    }

    private CompletableFuture<Void> chargeAndPersist(Player player, String tag, String name) {
        CompletableFuture<ClanCreateEconomy.ChargeResult> chargeFuture = new CompletableFuture<>();
        asyncDatabaseExecutor.runSync(() -> chargeFuture.complete(createEconomy.chargeCreate(player)));
        return chargeFuture.thenCompose(chargeResult -> {
            if (chargeResult == ClanCreateEconomy.ChargeResult.INSUFFICIENT_FUNDS) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.create.not-enough-money", Map.of(
                        "amount", String.valueOf(economyConfig.createCostAmount())
                )));
                return CompletableFuture.completedFuture(null);
            }
            if (chargeResult == ClanCreateEconomy.ChargeResult.FAILED) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.create.payment-failed"));
                return CompletableFuture.completedFuture(null);
            }
            long now = System.currentTimeMillis();
            CreateClanRecord record = new CreateClanRecord(
                    tag,
                    name,
                    player.getUniqueId(),
                    clanConfig.maxMembersDefault(),
                    now
            );
            return clanRepository.create(record).thenCompose(clan ->
                    rolePermissionService.seedDefaults(clan.id()).thenApply(ignored -> clan)
            ).thenAccept(clan -> notifyCreated(player, clan));
        });
    }

    private void notifyCreated(Player player, Clan clan) {
        asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            messageService.send(player, "clan.create.success", Map.of(
                    "tag", clan.tag(),
                    "name", clan.name()
            ));
            guiOpenService.openHub(player);
        });
    }
}
