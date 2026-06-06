package bm.b0b0b0.SoulPact.core.integration;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Optional;
import java.util.UUID;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;

public final class PlayerHeadSkinApplier {

    private final SkinRestorerIntegration skinRestorerIntegration;

    public PlayerHeadSkinApplier(SkinRestorerIntegration skinRestorerIntegration) {
        this.skinRestorerIntegration = skinRestorerIntegration;
    }

    public void apply(SkullMeta skullMeta, UUID ownerId, String ownerName) {
        if (ownerId == null) {
            return;
        }
        if (applySkinRestorer(skullMeta, ownerId, ownerName)) {
            return;
        }
        skullMeta.setPlayerProfile(createOwnerProfile(ownerId, ownerName));
    }

    private boolean applySkinRestorer(SkullMeta skullMeta, UUID ownerId, String ownerName) {
        if (!skinRestorerIntegration.available()) {
            return false;
        }
        Optional<SkinProperty> skinProperty = resolveSkinProperty(ownerId, ownerName);
        if (skinProperty.isEmpty()) {
            return false;
        }
        PlayerProfile profile = createOwnerProfile(ownerId, ownerName);
        SkinProperty property = skinProperty.get();
        profile.setProperty(new ProfileProperty(
                SkinProperty.TEXTURES_NAME,
                property.getValue(),
                property.getSignature()
        ));
        skullMeta.setPlayerProfile(profile);
        return true;
    }

    private PlayerProfile createOwnerProfile(UUID ownerId, String ownerName) {
        if (ownerName == null || ownerName.isBlank()) {
            return Bukkit.createProfile(ownerId);
        }
        return Bukkit.createProfile(ownerId, ownerName);
    }

    private Optional<SkinProperty> resolveSkinProperty(UUID ownerId, String ownerName) {
        try {
            PlayerStorage playerStorage = skinRestorerIntegration.skinsRestorer().getPlayerStorage();
            String resolvedName = ownerName == null ? "" : ownerName;
            boolean onlineMode = Bukkit.getServer().getOnlineMode();
            Optional<SkinProperty> skinProperty = playerStorage.getSkinForPlayer(ownerId, resolvedName, onlineMode);
            if (skinProperty.isPresent()) {
                return skinProperty;
            }
            return playerStorage.getSkinOfPlayer(ownerId);
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }
}
