package bm.b0b0b0.SoulPact.leaderboard.render;

import bm.b0b0b0.SoulPact.leaderboard.config.LeaderboardConfig;
import bm.b0b0b0.SoulPact.leaderboard.config.StandEquipmentSet;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.model.Board;
import bm.b0b0b0.SoulPact.leaderboard.model.ClanStanding;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public final class StandBoardRenderer {

    private final LeaderboardMessages messages;
    private final BoardEntityTag entityTag;
    private final Supplier<LeaderboardConfig> configSupplier;

    public StandBoardRenderer(
            LeaderboardMessages messages,
            BoardEntityTag entityTag,
            Supplier<LeaderboardConfig> configSupplier
    ) {
        this.messages = messages;
        this.entityTag = entityTag;
        this.configSupplier = configSupplier;
    }

    public void render(Board board, Location location, Optional<ClanStanding> standing, Map<String, String> placeholders) {
        ArmorStand stand = findExisting(board, location).orElseGet(() -> spawn(board, location));
        stand.customName(messages.component("leaderboard.format.stand.name", placeholders));
        stand.setCustomNameVisible(true);
        equip(stand, board, standing);
    }

    public void removeEntities(Board board, Location location) {
        for (Entity entity : nearbyCandidates(location)) {
            if (entity instanceof ArmorStand && entityTag.matches(entity, board.id())) {
                entity.remove();
            }
        }
    }

    private Optional<ArmorStand> findExisting(Board board, Location location) {
        for (Entity entity : nearbyCandidates(location)) {
            if (entity instanceof ArmorStand stand && entityTag.matches(entity, board.id())) {
                return Optional.of(stand);
            }
        }
        return Optional.empty();
    }

    private Iterable<Entity> nearbyCandidates(Location location) {
        return location.getWorld().getNearbyEntities(location, 2, 3, 2);
    }

    private ArmorStand spawn(Board board, Location location) {
        return location.getWorld().spawn(location, ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setInvulnerable(true);
            stand.setBasePlate(false);
            stand.setArms(true);
            stand.setPersistent(true);
            stand.setRotation(board.yaw(), 0F);
            entityTag.apply(stand, board.id());
        });
    }

    private void equip(ArmorStand stand, Board board, Optional<ClanStanding> standing) {
        LeaderboardConfig config = configSupplier.get();
        StandEquipmentSet equipment = config.equipmentFor(board.rankPosition());
        stand.getEquipment().setHelmet(headItem(standing, config));
        stand.getEquipment().setChestplate(item(equipment.chestplate()));
        stand.getEquipment().setLeggings(item(equipment.leggings()));
        stand.getEquipment().setBoots(item(equipment.boots()));
    }

    private ItemStack headItem(Optional<ClanStanding> standing, LeaderboardConfig config) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (!config.leaderHead() || standing.isEmpty()) {
            return head;
        }
        if (head.getItemMeta() instanceof SkullMeta skullMeta) {
            skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(standing.get().leaderId()));
            head.setItemMeta(skullMeta);
        }
        return head;
    }

    private ItemStack item(Material material) {
        return material == null ? new ItemStack(Material.AIR) : new ItemStack(material);
    }
}
