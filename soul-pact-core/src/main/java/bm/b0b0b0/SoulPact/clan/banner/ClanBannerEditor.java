package bm.b0b0b0.SoulPact.clan.banner;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public final class ClanBannerEditor {

    private static final int MAX_LAYERS = 6;

    private ClanBannerEditor() {
    }

    public static ItemStack createPreview(Material baseMaterial, Pattern pattern) {
        ItemStack itemStack = new ItemStack(baseMaterial);
        BannerMeta bannerMeta = (BannerMeta) itemStack.getItemMeta();
        bannerMeta.setPatterns(List.of(pattern));
        itemStack.setItemMeta(bannerMeta);
        return itemStack;
    }

    public static ItemStack copy(ItemStack source) {
        if (source == null || source.getType().isAir()) {
            return ClanBannerCodec.defaultBanner();
        }
        return source.clone();
    }

    public static ItemStack applyBaseColor(ItemStack banner, Material baseMaterial) {
        ItemStack updated = new ItemStack(baseMaterial);
        if (!(banner.getItemMeta() instanceof BannerMeta sourceMeta)) {
            return updated;
        }
        if (!(updated.getItemMeta() instanceof BannerMeta targetMeta)) {
            return updated;
        }
        targetMeta.setPatterns(new ArrayList<>(sourceMeta.getPatterns()));
        updated.setItemMeta(targetMeta);
        return updated;
    }

    public static ItemStack addPattern(ItemStack banner, PatternType patternType, DyeColor color) {
        ItemStack updated = banner.clone();
        if (!(updated.getItemMeta() instanceof BannerMeta bannerMeta)) {
            return updated;
        }
        List<Pattern> patterns = new ArrayList<>(bannerMeta.getPatterns());
        if (patterns.size() >= MAX_LAYERS) {
            return updated;
        }
        patterns.add(new Pattern(color, patternType));
        bannerMeta.setPatterns(patterns);
        updated.setItemMeta(bannerMeta);
        return updated;
    }

    public static ItemStack clearPatterns(ItemStack banner) {
        ItemStack updated = banner.clone();
        if (!(updated.getItemMeta() instanceof BannerMeta bannerMeta)) {
            return updated;
        }
        bannerMeta.setPatterns(List.of());
        updated.setItemMeta(bannerMeta);
        return updated;
    }

    public static ItemStack removeLastPattern(ItemStack banner) {
        ItemStack updated = banner.clone();
        if (!(updated.getItemMeta() instanceof BannerMeta bannerMeta)) {
            return updated;
        }
        List<Pattern> patterns = new ArrayList<>(bannerMeta.getPatterns());
        if (patterns.isEmpty()) {
            return updated;
        }
        patterns.removeLast();
        bannerMeta.setPatterns(patterns);
        updated.setItemMeta(bannerMeta);
        return updated;
    }

    public static int layerCount(ItemStack banner) {
        if (!(banner.getItemMeta() instanceof BannerMeta bannerMeta)) {
            return 0;
        }
        return bannerMeta.getPatterns().size();
    }

    public static boolean canAddPattern(ItemStack banner) {
        return layerCount(banner) < MAX_LAYERS;
    }
}
