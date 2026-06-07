package bm.b0b0b0.SoulPact.clan.banner;

import java.util.List;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;

public final class ClanBannerPatternCatalog {

    public record PatternOption(String id, PatternType type) {
    }

    private static final List<PatternOption> OPTIONS = List.of(
            new PatternOption("stripe_bottom", PatternType.STRIPE_BOTTOM),
            new PatternOption("stripe_top", PatternType.STRIPE_TOP),
            new PatternOption("stripe_left", PatternType.STRIPE_LEFT),
            new PatternOption("cross", PatternType.CROSS),
            new PatternOption("triangle_top", PatternType.TRIANGLE_TOP),
            new PatternOption("border", PatternType.CURLY_BORDER),
            new PatternOption("gradient", PatternType.GRADIENT),
            new PatternOption("rhombus", PatternType.RHOMBUS),
            new PatternOption("skull", PatternType.SKULL)
    );

    private static final List<Material> BASE_COLORS = List.of(
            Material.WHITE_BANNER,
            Material.ORANGE_BANNER,
            Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER,
            Material.YELLOW_BANNER,
            Material.LIME_BANNER,
            Material.PINK_BANNER,
            Material.GRAY_BANNER,
            Material.LIGHT_GRAY_BANNER,
            Material.CYAN_BANNER,
            Material.PURPLE_BANNER,
            Material.BLUE_BANNER,
            Material.BROWN_BANNER,
            Material.GREEN_BANNER,
            Material.RED_BANNER,
            Material.BLACK_BANNER
    );

    private static final List<DyeColor> PATTERN_COLORS = List.of(
            DyeColor.PURPLE,
            DyeColor.WHITE,
            DyeColor.BLACK,
            DyeColor.RED,
            DyeColor.YELLOW,
            DyeColor.CYAN,
            DyeColor.LIME,
            DyeColor.ORANGE
    );

    private static final DyeColor DEFAULT_PATTERN_COLOR = DyeColor.PURPLE;

    private ClanBannerPatternCatalog() {
    }

    public static List<PatternOption> patternOptions() {
        return OPTIONS;
    }

    public static List<Material> baseColors() {
        return BASE_COLORS;
    }

    public static List<DyeColor> patternColors() {
        return PATTERN_COLORS;
    }

    public static DyeColor defaultPatternColor() {
        return DEFAULT_PATTERN_COLOR;
    }

    public static PatternOption optionAt(int index) {
        if (index < 0 || index >= OPTIONS.size()) {
            return null;
        }
        return OPTIONS.get(index);
    }

    public static Material baseColorAt(int index) {
        if (index < 0 || index >= BASE_COLORS.size()) {
            return Material.WHITE_BANNER;
        }
        return BASE_COLORS.get(index);
    }

    public static DyeColor nextPatternColor(DyeColor current) {
        if (current == null) {
            return DEFAULT_PATTERN_COLOR;
        }
        int index = PATTERN_COLORS.indexOf(current);
        if (index < 0) {
            return DEFAULT_PATTERN_COLOR;
        }
        return PATTERN_COLORS.get((index + 1) % PATTERN_COLORS.size());
    }

    public static ItemStack previewPattern(PatternOption option, DyeColor color) {
        DyeColor patternColor = color == null ? DEFAULT_PATTERN_COLOR : color;
        Material baseMaterial = contrastingPreviewBase(patternColor);
        return ClanBannerEditor.createPreview(baseMaterial, new org.bukkit.block.banner.Pattern(patternColor, option.type()));
    }

    private static Material contrastingPreviewBase(DyeColor patternColor) {
        return switch (patternColor) {
            case WHITE, YELLOW, LIME, ORANGE, LIGHT_BLUE, PINK, CYAN, LIGHT_GRAY -> Material.BLACK_BANNER;
            default -> Material.WHITE_BANNER;
        };
    }
}
