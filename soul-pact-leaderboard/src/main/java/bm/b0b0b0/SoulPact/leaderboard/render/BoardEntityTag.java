package bm.b0b0b0.SoulPact.leaderboard.render;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class BoardEntityTag {

    private final NamespacedKey key;

    public BoardEntityTag(JavaPlugin plugin) {
        this.key = new NamespacedKey(plugin, "lb-board");
    }

    public void apply(Entity entity, long boardId) {
        entity.getPersistentDataContainer().set(key, PersistentDataType.LONG, boardId);
    }

    public boolean matches(Entity entity, long boardId) {
        Long stored = entity.getPersistentDataContainer().get(key, PersistentDataType.LONG);
        return stored != null && stored == boardId;
    }

    public boolean isBoardEntity(Entity entity) {
        return entity.getPersistentDataContainer().has(key, PersistentDataType.LONG);
    }
}
