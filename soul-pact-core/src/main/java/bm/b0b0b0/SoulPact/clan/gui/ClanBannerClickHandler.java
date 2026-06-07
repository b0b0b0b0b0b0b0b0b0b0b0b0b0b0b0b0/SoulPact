package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.clan.banner.ClanBannerCodec;
import bm.b0b0b0.SoulPact.clan.banner.ClanBannerEditor;
import bm.b0b0b0.SoulPact.clan.banner.ClanBannerPatternCatalog;
import bm.b0b0b0.SoulPact.clan.service.ClanBannerService;
import bm.b0b0b0.SoulPact.clan.standard.ClanStandardService;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerClickHandler {

    private final MessageService messageService;
    private final ClanBannerService clanBannerService;
    private final ClanStandardService clanStandardService;
    private final ClanBannerMenuPopulator populator;
    private final ClanGuiOpenService guiOpenService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ClanBannerReturnService bannerReturnService;

    public ClanBannerClickHandler(
            MessageService messageService,
            ClanBannerService clanBannerService,
            ClanStandardService clanStandardService,
            ClanBannerMenuPopulator populator,
            ClanGuiOpenService guiOpenService,
            AsyncDatabaseExecutor asyncDatabaseExecutor,
            ClanBannerReturnService bannerReturnService
    ) {
        this.messageService = messageService;
        this.clanBannerService = clanBannerService;
        this.clanStandardService = clanStandardService;
        this.populator = populator;
        this.guiOpenService = guiOpenService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
        this.bannerReturnService = bannerReturnService;
    }

    public void handle(ClanBannerMenu menu, Player player, int slot) {
        if (slot == menu.config().backSlot()) {
            bannerReturnService.openAfterBanner(player, guiOpenService);
            return;
        }
        if (!menu.clanLeader()) {
            if (slot == menu.config().previewSlot()) {
                return;
            }
            messageService.send(player, "clan.banner.view-only");
            return;
        }
        if (menu.standardOut()) {
            handleStandardOutMode(menu, player, slot);
            return;
        }
        if (slot == menu.config().saveSlot()) {
            save(menu, player);
            return;
        }
        if (slot == menu.config().takeStandardSlot()) {
            takeStandard(menu, player);
            return;
        }
        if (slot == menu.config().clearPatternsSlot()) {
            menu.setWorkingBanner(ClanBannerEditor.clearPatterns(menu.workingBanner()));
            menu.refresh(populator, player);
            return;
        }
        if (slot == menu.config().undoPatternSlot()) {
            menu.setWorkingBanner(ClanBannerEditor.removeLastPattern(menu.workingBanner()));
            menu.refresh(populator, player);
            return;
        }
        if (slot == menu.config().patternColorSlot()) {
            menu.setPatternColor(ClanBannerPatternCatalog.nextPatternColor(menu.patternColor()));
            menu.refresh(populator, player);
            return;
        }
        if (slot == menu.config().fromHandSlot()) {
            copyFromHand(menu, player);
            return;
        }
        if (slot == menu.config().previewSlot()) {
            return;
        }
        ClanBannerPatternCatalog.PatternOption patternOption = resolvePatternOption(menu, slot);
        if (patternOption != null) {
            applyPattern(menu, player, patternOption);
            return;
        }
        Integer baseColorIndex = resolveBaseColorIndex(menu, slot);
        if (baseColorIndex != null) {
            menu.setWorkingBanner(ClanBannerEditor.applyBaseColor(
                    menu.workingBanner(),
                    ClanBannerPatternCatalog.baseColorAt(baseColorIndex)
            ));
            menu.refresh(populator, player);
        }
    }

    private void handleStandardOutMode(ClanBannerMenu menu, Player player, int slot) {
        if (slot == menu.config().previewSlot()) {
            return;
        }
        if (slot == menu.config().takeStandardSlot() && menu.canDepositStandard()) {
            depositStandard(menu, player);
            return;
        }
        if (slot == menu.config().saveSlot() || slot == menu.config().takeStandardSlot()) {
            if (menu.canDepositStandard()) {
                messageService.send(player, "clan.banner.deposit-first");
            } else {
                messageService.send(player, "clan.banner.standard-away");
            }
            return;
        }
        messageService.send(player, "clan.banner.edit-locked");
    }

    private void depositStandard(ClanBannerMenu menu, Player player) {
        clanStandardService.depositStandard(player, menu.clanId()).thenAccept(result ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    clanStandardService.sendDepositResult(player, result, Map.of("tag", menu.clanTag()));
                    if (result != ClanStandardService.DepositStandardResult.SUCCESS) {
                        return;
                    }
                    menu.setStandardOut(false);
                    menu.setCanDepositStandard(false);
                    menu.refresh(populator, player);
                })
        );
    }

    private void save(ClanBannerMenu menu, Player player) {
        clanBannerService.saveBanner(menu.clanId(), menu.workingBanner()).thenAccept(saved ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (!saved) {
                        messageService.send(player, "clan.banner.save-failed");
                        return;
                    }
                    clanStandardService.refreshTrackedStandard(
                            player,
                            menu.clanId(),
                            menu.clanTag(),
                            menu.workingBanner()
                    );
                    messageService.send(player, "clan.banner.saved");
                    bannerReturnService.openAfterBanner(player, guiOpenService);
                })
        );
    }

    private void takeStandard(ClanBannerMenu menu, Player player) {
        clanStandardService.takeStandard(player, menu.clanId()).thenAccept(result ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    clanStandardService.sendTakeResult(player, result, Map.of("tag", menu.clanTag()));
                    if (result == ClanStandardService.TakeStandardResult.SUCCESS) {
                        player.closeInventory();
                    }
                })
        );
    }

    private void copyFromHand(ClanBannerMenu menu, Player player) {
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (handItem == null || handItem.getType().isAir()) {
            messageService.send(player, "clan.banner.from-hand-empty");
            return;
        }
        if (!ClanBannerCodec.isBanner(handItem)) {
            messageService.send(player, "clan.banner.from-hand-invalid");
            return;
        }
        menu.setWorkingBanner(ClanBannerEditor.copy(handItem));
        menu.refresh(populator, player);
        messageService.send(player, "clan.banner.from-hand-applied");
    }

    private void applyPattern(ClanBannerMenu menu, Player player, ClanBannerPatternCatalog.PatternOption option) {
        if (!ClanBannerEditor.canAddPattern(menu.workingBanner())) {
            messageService.send(player, "clan.banner.max-layers");
            return;
        }
        menu.setWorkingBanner(ClanBannerEditor.addPattern(
                menu.workingBanner(),
                option.type(),
                menu.patternColor()
        ));
        menu.refresh(populator, player);
    }

    private ClanBannerPatternCatalog.PatternOption resolvePatternOption(ClanBannerMenu menu, int slot) {
        List<ClanBannerPatternCatalog.PatternOption> options = ClanBannerPatternCatalog.patternOptions();
        for (int index = 0; index < options.size(); index++) {
            if (slot == menu.config().patternSlot(index)) {
                return options.get(index);
            }
        }
        return null;
    }

    private Integer resolveBaseColorIndex(ClanBannerMenu menu, int slot) {
        for (int index = 0; index < ClanBannerPatternCatalog.baseColors().size(); index++) {
            if (slot == menu.config().baseColorSlot(index)) {
                return index;
            }
        }
        return null;
    }
}
