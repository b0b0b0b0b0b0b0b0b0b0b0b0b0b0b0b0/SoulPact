package bm.b0b0b0.SoulPact.chest.gui;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.chest.config.ChestConfig;
import bm.b0b0b0.SoulPact.chest.message.ChestMessages;
import bm.b0b0b0.SoulPact.chest.service.ChestGuiLayout;
import bm.b0b0b0.SoulPact.chest.service.ClanChestService;
import org.bukkit.entity.Player;

public final class ChestGuiService {

    private final SoulPactApi api;
    private final ChestConfig config;
    private final ChestMessages messages;
    private final ClanChestService chestService;
    private final ChestGuiLayout layout;
    private final ChestMenuPopulator populator;
    private final ChestClickHandler clickHandler;

    public ChestGuiService(
            SoulPactApi api,
            ChestConfig config,
            ChestMessages messages,
            ClanChestService chestService,
            ChestGuiLayout layout
    ) {
        this.api = api;
        this.config = config;
        this.messages = messages;
        this.chestService = chestService;
        this.layout = layout;
        this.populator = new ChestMenuPopulator(config, messages, layout);
        ChestClanNavigation navigation = new ChestClanNavigation(api, this);
        this.clickHandler = new ChestClickHandler(this, navigation, config);
    }

    public ChestClickHandler clickHandler() {
        return clickHandler;
    }

    public ChestGuiLayout layout() {
        return layout;
    }

    public ChestMessages messages() {
        return messages;
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        api.findClanByPlayer(player.getUniqueId()).thenAccept(clanOptional -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (clanOptional.isEmpty()) {
                messages.send(player, "chest.error.not-in-clan");
                return;
            }
            openLoaded(player, clanOptional.get(), page, null);
        }));
    }

    public void openLoaded(Player player, ClanSnapshot clan, int page, ChestMenu previousMenu) {
        if (previousMenu != null) {
            previousMenu.syncPageToItems();
            persist(previousMenu);
        }
        chestService.loadSnapshot(player, clan, page).thenAccept(snapshot -> api.scheduler().runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            ChestMenu menu = new ChestMenu(config, messages, layout, populator, player, snapshot);
            player.openInventory(menu.getInventory());
        }));
    }

    public void switchPage(ChestMenu menu, Player player, int targetPage) {
        if (targetPage == menu.snapshot().page()) {
            return;
        }
        openLoaded(player, menu.snapshot().clan(), targetPage, menu);
    }

    public void purchaseCell(ChestMenu menu, Player player) {
        menu.syncPageToItems();
        chestService.purchaseNextCell(player, menu.snapshot()).thenAccept(success -> {
            if (!success) {
                return;
            }
            api.scheduler().runSync(() -> {
                if (!player.isOnline()) {
                    return;
                }
                if (!(player.getOpenInventory().getTopInventory().getHolder(false) instanceof ChestMenu openMenu)) {
                    return;
                }
                if (openMenu != menu) {
                    return;
                }
                refreshMenu(menu, player);
            });
        });
    }

    public void persistAndLeave(ChestMenu menu, Player player, Runnable afterSave) {
        menu.syncPageToItems();
        persist(menu);
        api.scheduler().runSyncLater(1L, () -> {
            if (!player.isOnline()) {
                return;
            }
            afterSave.run();
        });
    }

    public void persist(ChestMenu menu) {
        chestService.saveItems(menu.snapshot().clan().id(), menu.snapshot().items());
    }

    private void refreshMenu(ChestMenu menu, Player player) {
        menu.syncPageToItems();
        persist(menu);
        chestService.loadSnapshot(player, menu.snapshot().clan(), menu.snapshot().page()).thenAccept(snapshot ->
                api.scheduler().runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (!(player.getOpenInventory().getTopInventory().getHolder(false) instanceof ChestMenu openMenu)) {
                        return;
                    }
                    if (openMenu != menu) {
                        return;
                    }
                    menu.replaceSnapshot(snapshot, player, populator);
                })
        );
    }
}
