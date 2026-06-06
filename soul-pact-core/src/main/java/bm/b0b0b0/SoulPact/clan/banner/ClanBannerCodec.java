package bm.b0b0b0.SoulPact.clan.banner;

import java.util.Base64;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerCodec {

    private ClanBannerCodec() {
    }

    public static ItemStack defaultBanner() {
        return new ItemStack(Material.WHITE_BANNER);
    }

    public static String encode(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return null;
        }
        return Base64.getEncoder().encodeToString(itemStack.serializeAsBytes());
    }

    public static ItemStack decode(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            return defaultBanner();
        }
        try {
            ItemStack itemStack = ItemStack.deserializeBytes(Base64.getDecoder().decode(encoded));
            if (itemStack == null || !isBanner(itemStack)) {
                return defaultBanner();
            }
            return itemStack;
        } catch (Exception exception) {
            return defaultBanner();
        }
    }

    public static boolean isBanner(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return false;
        }
        return itemStack.getType().name().endsWith("_BANNER");
    }
}
