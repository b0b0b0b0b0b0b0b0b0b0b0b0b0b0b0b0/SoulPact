package bm.b0b0b0.SoulPact.clan.standard;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

public final class ClanStandardKeys {

    private final NamespacedKey clanId;
    private final NamespacedKey clanTag;

    public ClanStandardKeys(Plugin plugin) {
        this.clanId = new NamespacedKey(plugin, "clan_standard_id");
        this.clanTag = new NamespacedKey(plugin, "clan_standard_tag");
    }

    public NamespacedKey clanId() {
        return clanId;
    }

    public NamespacedKey clanTag() {
        return clanTag;
    }
}
