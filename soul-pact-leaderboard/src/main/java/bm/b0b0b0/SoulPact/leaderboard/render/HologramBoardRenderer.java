package bm.b0b0b0.SoulPact.leaderboard.render;

import bm.b0b0b0.SoulPact.leaderboard.config.LeaderboardConfig;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardTextParser;
import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.joml.Vector3f;

public final class HologramBoardRenderer {

    private final LeaderboardMessages messages;
    private final BoardEntityTag entityTag;
    private final Supplier<LeaderboardConfig> configSupplier;

    public HologramBoardRenderer(
            LeaderboardMessages messages,
            BoardEntityTag entityTag,
            Supplier<LeaderboardConfig> configSupplier
    ) {
        this.messages = messages;
        this.entityTag = entityTag;
        this.configSupplier = configSupplier;
    }

    public void render(Board board, Location location, Map<String, String> placeholders) {
        Location displayLocation = location.clone().add(0, configSupplier.get().hologramYOffset(), 0);
        TextDisplay display = findExisting(board, displayLocation).orElseGet(() -> spawn(board, displayLocation));
        display.text(buildText(placeholders));
    }

    public void removeEntities(Board board, Location location) {
        Location displayLocation = location.clone().add(0, configSupplier.get().hologramYOffset(), 0);
        for (Entity entity : nearbyCandidates(displayLocation)) {
            if (entity instanceof TextDisplay && entityTag.matches(entity, board.id())) {
                entity.remove();
            }
        }
    }

    private Optional<TextDisplay> findExisting(Board board, Location location) {
        for (Entity entity : nearbyCandidates(location)) {
            if (entity instanceof TextDisplay display && entityTag.matches(entity, board.id())) {
                return Optional.of(display);
            }
        }
        return Optional.empty();
    }

    private Iterable<Entity> nearbyCandidates(Location location) {
        return location.getWorld().getNearbyEntities(location, 2, 4, 2);
    }

    private TextDisplay spawn(Board board, Location location) {
        float scale = (float) configSupplier.get().hologramScale();
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            display.setBillboard(Display.Billboard.CENTER);
            display.setShadowed(true);
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
            display.setPersistent(true);
            display.setTransformation(new org.bukkit.util.Transformation(
                    new Vector3f(),
                    display.getTransformation().getLeftRotation(),
                    new Vector3f(scale, scale, scale),
                    display.getTransformation().getRightRotation()
            ));
            entityTag.apply(display, board.id());
        });
    }

    private Component buildText(Map<String, String> placeholders) {
        List<String> lines = messages.resolveList("leaderboard.format.hologram.lines", placeholders);
        Component text = Component.empty();
        for (int index = 0; index < lines.size(); index++) {
            if (index > 0) {
                text = text.append(Component.newline());
            }
            text = text.append(LeaderboardTextParser.parse(lines.get(index)));
        }
        return text;
    }
}
