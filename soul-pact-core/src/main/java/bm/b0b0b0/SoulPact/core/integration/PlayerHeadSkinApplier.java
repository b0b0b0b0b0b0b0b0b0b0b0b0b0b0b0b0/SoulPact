package bm.b0b0b0.SoulPact.core.integration;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.property.InputDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

public final class PlayerHeadSkinApplier {

    private static final Map<UUID, PlayerProfile> PROFILE_CACHE = new ConcurrentHashMap<>();

    private final SkinRestorerIntegration skinRestorerIntegration;

    public PlayerHeadSkinApplier(SkinRestorerIntegration skinRestorerIntegration) {
        this.skinRestorerIntegration = skinRestorerIntegration;
    }

    public static void clearCache() {
        PROFILE_CACHE.clear();
    }

    public void apply(SkullMeta skullMeta, UUID ownerId, String ownerName) {
        if (ownerId == null) {
            return;
        }
        PlayerProfile cached = PROFILE_CACHE.get(ownerId);
        if (cached != null) {
            skullMeta.setPlayerProfile(copyProfile(cached));
            return;
        }
        String resolvedName = resolveOwnerName(ownerId, ownerName);
        Optional<PlayerProfile> profileOptional = resolveProfile(ownerId, resolvedName);
        if (profileOptional.isEmpty()) {
            return;
        }
        PlayerProfile stored = copyProfile(profileOptional.get());
        skullMeta.setPlayerProfile(stored);
        PROFILE_CACHE.put(ownerId, stored);
    }

    private Optional<PlayerProfile> resolveProfile(UUID ownerId, String ownerName) {
        Player online = Bukkit.getPlayer(ownerId);
        if (online != null) {
            Optional<PlayerProfile> onlineProfile = profileFromExistingTextures(online.getPlayerProfile());
            if (onlineProfile.isPresent()) {
                return onlineProfile;
            }
        }
        Optional<SkinProperty> skinRestorerSkin = resolveSkinRestorerSkinLocalOnly(ownerId, ownerName);
        if (skinRestorerSkin.isPresent()) {
            return Optional.of(buildProfile(ownerId, ownerName, skinRestorerSkin.get()));
        }
        if (online != null) {
            return profileFromExistingTextures(online.getPlayerProfile());
        }
        return Optional.empty();
    }

    private Optional<SkinProperty> resolveSkinRestorerSkinLocalOnly(UUID ownerId, String ownerName) {
        if (!skinRestorerIntegration.available()) {
            return Optional.empty();
        }
        try {
            SkinsRestorer skinsRestorer = skinRestorerIntegration.skinsRestorer();
            PlayerStorage playerStorage = skinsRestorer.getPlayerStorage();
            SkinStorage skinStorage = skinsRestorer.getSkinStorage();

            Optional<SkinProperty> linkedSkin = playerStorage.getSkinOfPlayer(ownerId);
            if (linkedSkin.isPresent()) {
                return linkedSkin;
            }

            Optional<SkinProperty> identifierSkin = playerStorage.getSkinIdOfPlayer(ownerId)
                    .flatMap(skinStorage::getSkinDataByIdentifier);
            if (identifierSkin.isPresent()) {
                return identifierSkin;
            }

            Optional<SkinProperty> skinByUuidLookup = readSkinDataByInput(skinStorage, ownerId.toString());
            if (skinByUuidLookup.isPresent()) {
                return skinByUuidLookup;
            }

            if (ownerName != null && !ownerName.isBlank()) {
                Optional<SkinProperty> skinByNameLookup = readSkinDataByInput(skinStorage, ownerName);
                if (skinByNameLookup.isPresent()) {
                    return skinByNameLookup;
                }
            }
        } catch (Throwable ignored) {
        }
        return Optional.empty();
    }

    private static Optional<SkinProperty> readSkinDataByInput(SkinStorage skinStorage, String input) {
        Optional<InputDataResult> skinData = skinStorage.findSkinData(input);
        if (skinData.isEmpty()) {
            return Optional.empty();
        }
        InputDataResult result = skinData.get();
        if (result.getProperty() != null) {
            return Optional.of(result.getProperty());
        }
        if (result.getIdentifier() == null) {
            return Optional.empty();
        }
        return skinStorage.getSkinDataByIdentifier(result.getIdentifier());
    }

    private static String resolveOwnerName(UUID ownerId, String ownerName) {
        if (ownerName != null && !ownerName.isBlank()) {
            return ownerName;
        }
        Player online = Bukkit.getPlayer(ownerId);
        if (online != null) {
            String onlineName = online.getPlayerProfile().getName();
            if (onlineName != null && !onlineName.isBlank()) {
                return onlineName;
            }
        }
        OfflinePlayer offline = Bukkit.getOfflinePlayer(ownerId);
        String offlineName = offline.getPlayerProfile().getName();
        if (offlineName != null && !offlineName.isBlank()) {
            return offlineName;
        }
        return null;
    }

    private static Optional<PlayerProfile> profileFromExistingTextures(PlayerProfile profile) {
        if (profile == null || !profile.hasProperty(SkinProperty.TEXTURES_NAME)) {
            return Optional.empty();
        }
        return Optional.of(copyProfile(profile));
    }

    private static PlayerProfile buildProfile(UUID ownerId, String ownerName, SkinProperty property) {
        PlayerProfile profile = Bukkit.createProfile(ownerId, ownerName);
        profile.setProperty(new ProfileProperty(
                SkinProperty.TEXTURES_NAME,
                property.getValue(),
                property.getSignature()
        ));
        return profile;
    }

    private static PlayerProfile copyProfile(PlayerProfile source) {
        PlayerProfile copy = Bukkit.createProfile(source.getId(), source.getName());
        for (ProfileProperty property : source.getProperties()) {
            copy.setProperty(new ProfileProperty(property.getName(), property.getValue(), property.getSignature()));
        }
        return copy;
    }
}
