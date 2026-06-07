package bm.b0b0b0.SoulPact.clan.gui;

import bm.b0b0b0.SoulPact.api.extension.ExtensionRegistry;
import bm.b0b0b0.SoulPact.api.land.ClanLandProvider;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class ClanBannerReturnService {

    public enum ReturnTarget {
        CLAN_HUB,
        LAND_MENU
    }

    private final ExtensionRegistry extensionRegistry;
    private final Map<UUID, ReturnTarget> returnTargets = new ConcurrentHashMap<>();

    public ClanBannerReturnService(ExtensionRegistry extensionRegistry) {
        this.extensionRegistry = extensionRegistry;
    }

    public void setReturnTarget(Player player, ReturnTarget target) {
        returnTargets.put(player.getUniqueId(), target);
    }

    public void openAfterBanner(Player player, ClanGuiOpenService guiOpenService) {
        ReturnTarget target = returnTargets.remove(player.getUniqueId());
        if (target == ReturnTarget.LAND_MENU && openLandMenu(player)) {
            return;
        }
        guiOpenService.openHub(player);
    }

    private boolean openLandMenu(Player player) {
        return extensionRegistry.find("land")
                .filter(ClanLandProvider.class::isInstance)
                .map(ClanLandProvider.class::cast)
                .map(land -> {
                    land.openGui(player);
                    return true;
                })
                .orElse(false);
    }
}
