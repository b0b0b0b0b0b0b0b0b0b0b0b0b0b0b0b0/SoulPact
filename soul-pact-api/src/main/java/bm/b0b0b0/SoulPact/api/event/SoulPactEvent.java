package bm.b0b0b0.SoulPact.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

public abstract class SoulPactEvent extends Event {

    protected SoulPactEvent() {
        super(!Bukkit.isPrimaryThread());
    }
}
