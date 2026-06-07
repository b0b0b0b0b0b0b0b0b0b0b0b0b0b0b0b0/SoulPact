package bm.b0b0b0.SoulPact.war.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.war.config.WarConfig;
import bm.b0b0b0.SoulPact.war.message.WarMessages;
import bm.b0b0b0.SoulPact.war.model.WarDeclarationRecord;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import bm.b0b0b0.SoulPact.war.service.WarClanLookup;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class WarGuiService {

    private final SoulPactApi api;
    private final WarConfig config;
    private final WarMessages messages;
    private final ClanWarService warService;
    private final WarClanLookup clanLookup;
    private final WarDeclareConfirmMenuPopulator declareConfirmPopulator;
    private final WarPendingListMenuPopulator pendingListPopulator;
    private final WarPendingDetailMenuPopulator pendingDetailPopulator;
    private final WarGuiClickHandler clickHandler;

    public WarGuiService(
            SoulPactApi api,
            WarConfig config,
            WarMessages messages,
            ClanWarService warService,
            WarClanLookup clanLookup
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.warService = warService;
        this.clanLookup = clanLookup;
        this.declareConfirmPopulator = new WarDeclareConfirmMenuPopulator(config, messages);
        this.pendingListPopulator = new WarPendingListMenuPopulator(config, messages);
        this.pendingDetailPopulator = new WarPendingDetailMenuPopulator(config, messages, warService);
        this.clickHandler = new WarGuiClickHandler(this, warService, api);
    }

    public WarGuiClickHandler clickHandler() {
        return clickHandler;
    }

    public void openDeclareConfirm(Player player, long targetClanId, int listPage) {
        clanLookup.findClan(targetClanId).thenAccept(targetOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline() || targetOptional.isEmpty()) {
                return;
            }
            WarDeclareConfirmMenu menu = new WarDeclareConfirmMenu(
                    config,
                    declareConfirmPopulator,
                    messages,
                    player,
                    targetClanId,
                    targetOptional.get().tag(),
                    targetOptional.get().name(),
                    listPage
            );
            player.openInventory(menu.getInventory());
        }));
    }

    public void openPendingList(Player player) {
        warService.listPendingForDefenderLeader(player).thenCompose(this::resolvePendingEntries).thenAccept(entries ->
                api.scheduler().runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    WarPendingListMenu menu = new WarPendingListMenu(
                            config,
                            pendingListPopulator,
                            messages,
                            player,
                            entries
                    );
                    player.openInventory(menu.getInventory());
                })
        );
    }

    public void openPendingDetail(Player player, WarDeclarationRecord declaration) {
        clanLookup.findClan(declaration.attackerClanId()).thenAccept(attackerOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            String attackerTag = attackerOptional.map(clan -> clan.tag()).orElse("#" + declaration.attackerClanId());
            String attackerName = attackerOptional.map(clan -> clan.name()).orElse(String.valueOf(declaration.attackerClanId()));
            WarPendingDetailMenu menu = new WarPendingDetailMenu(
                    config,
                    pendingDetailPopulator,
                    messages,
                    player,
                    declaration,
                    attackerTag,
                    attackerName
            );
            player.openInventory(menu.getInventory());
        }));
    }

    private CompletableFuture<List<WarPendingListEntry>> resolvePendingEntries(List<WarDeclarationRecord> declarations) {
        CompletableFuture<List<WarPendingListEntry>> chain = CompletableFuture.completedFuture(new ArrayList<>());
        for (WarDeclarationRecord declaration : declarations) {
            chain = chain.thenCompose(entries -> clanLookup.findClan(declaration.attackerClanId()).thenApply(clanOptional -> {
                String tag = clanOptional.map(clan -> clan.tag()).orElse("#" + declaration.attackerClanId());
                String name = clanOptional.map(clan -> clan.name()).orElse(String.valueOf(declaration.attackerClanId()));
                entries.add(new WarPendingListEntry(declaration, tag, name));
                return entries;
            }));
        }
        return chain;
    }

    public void reopenPendingList(Player player) {
        openPendingList(player);
    }
}
