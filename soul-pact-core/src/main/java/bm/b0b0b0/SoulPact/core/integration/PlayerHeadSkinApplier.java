package bm.b0b0b0.SoulPact.core.integration;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
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
        Player online = Bukkit.getPlayer(ownerId);
        if (online != null) {
            PlayerProfile profile = copyProfile(online.getPlayerProfile());
            skullMeta.setPlayerProfile(profile);
            PROFILE_CACHE.put(ownerId, profile);
            return;
        }
        if (applySkinRestorer(skullMeta, ownerId)) {
            PlayerProfile profile = skullMeta.getPlayerProfile();
            if (profile != null) {
                PlayerProfile stored = copyProfile(profile);
                PROFILE_CACHE.put(ownerId, stored);
            }
            return;
        }
        PlayerProfile profile = Bukkit.createProfile(ownerId);
        skullMeta.setPlayerProfile(profile);
        PROFILE_CACHE.put(ownerId, copyProfile(profile));
    }

    private boolean applySkinRestorer(SkullMeta skullMeta, UUID ownerId) {
        if (!skinRestorerIntegration.available()) {
            return false;
        }
        Optional<SkinProperty> skinProperty = resolveSkinProperty(ownerId);
        if (skinProperty.isEmpty()) {
            return false;
        }
        PlayerProfile profile = Bukkit.createProfile(ownerId);
        SkinProperty property = skinProperty.get();
        profile.setProperty(new ProfileProperty(
                SkinProperty.TEXTURES_NAME,
                property.getValue(),
                property.getSignature()
        ));
        skullMeta.setPlayerProfile(profile);
        return true;
    }

    private Optional<SkinProperty> resolveSkinProperty(UUID ownerId) {
        try {
            PlayerStorage playerStorage = skinRestorerIntegration.skinsRestorer().getPlayerStorage();
            return playerStorage.getSkinOfPlayer(ownerId);
        } catch (Throwable ignored) {
            return Optional.empty();
        }
    }

    private static PlayerProfile copyProfile(PlayerProfile source) {
        PlayerProfile copy = Bukkit.createProfile(source.getId(), source.getName());
        for (ProfileProperty property : source.getProperties()) {
            copy.setProperty(new ProfileProperty(property.getName(), property.getValue(), property.getSignature()));
        }
        return copy;
    }
}
