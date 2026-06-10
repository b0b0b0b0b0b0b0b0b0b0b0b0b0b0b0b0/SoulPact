package bm.b0b0b0.SoulPact.clanholo.render;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public final class HologramEntityTag {

    private final NamespacedKey hologramIdKey;
    private final NamespacedKey lineIndexKey;

    public HologramEntityTag(JavaPlugin plugin) {
        this.hologramIdKey = new NamespacedKey(plugin, "ch-holo-id");
        this.lineIndexKey = new NamespacedKey(plugin, "ch-holo-line");
    }

    public void apply(Entity entity, long hologramId, int lineIndex) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(hologramIdKey, PersistentDataType.LONG, hologramId);
        container.set(lineIndexKey, PersistentDataType.INTEGER, lineIndex);
    }

    public boolean matches(Entity entity, long hologramId) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        Long storedId = container.get(hologramIdKey, PersistentDataType.LONG);
        return storedId != null && storedId == hologramId;
    }

    public boolean matchesLine(Entity entity, long hologramId, int lineIndex) {
        if (!matches(entity, hologramId)) {
            return false;
        }
        Integer storedLine = entity.getPersistentDataContainer().get(lineIndexKey, PersistentDataType.INTEGER);
        return storedLine != null && storedLine == lineIndex;
    }
}
