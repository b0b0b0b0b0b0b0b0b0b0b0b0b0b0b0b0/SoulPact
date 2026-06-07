package bm.b0b0b0.SoulPact.war.integration;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.clan.SoulPactClanStandard;
import bm.b0b0b0.SoulPact.api.war.WarFlagBreakGate;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Method;

public final class WorldGuardWarEventBridge {

    private static volatile boolean registered;

    private static final Listener REGISTRATION_STUB = new Listener() {
    };

    private static final String BREAK_BLOCK_EVENT = "com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent";

    private final JavaPlugin plugin;
    private final SoulPactClanStandard clanStandard;
    private final WarFlagBreakGate flagBreakGate;
    private final Method getOriginalEvent;
    private final Method setAllowed;
    private final Method setSilent;

    private WorldGuardWarEventBridge(
            JavaPlugin plugin,
            SoulPactClanStandard clanStandard,
            WarFlagBreakGate flagBreakGate,
            Method getOriginalEvent,
            Method setAllowed,
            Method setSilent
    ) {
        this.plugin = plugin;
        this.clanStandard = clanStandard;
        this.flagBreakGate = flagBreakGate;
        this.getOriginalEvent = getOriginalEvent;
        this.setAllowed = setAllowed;
        this.setSilent = setSilent;
    }

    public static void register(JavaPlugin plugin, SoulPactApi api, WarFlagBreakGate flagBreakGate) {
        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            return;
        }
        if (registered) {
            return;
        }
        try {
            Class<?> delegateClass = Class.forName("com.sk89q.worldguard.bukkit.event.DelegateEvent");
            Method getOriginalEvent = delegateClass.getMethod("getOriginalEvent");
            Method setAllowed = delegateClass.getMethod("setAllowed", boolean.class);
            Method setSilent = delegateClass.getMethod("setSilent", boolean.class);
            WorldGuardWarEventBridge bridge = new WorldGuardWarEventBridge(
                    plugin,
                    api.clanStandard(),
                    flagBreakGate,
                    getOriginalEvent,
                    setAllowed,
                    setSilent
            );
            bridge.bind(BREAK_BLOCK_EVENT, bridge::handleDelegate);
            registered = true;
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("WorldGuard war override unavailable: " + exception.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void bind(String eventClassName, EventExecutor executor) throws ReflectiveOperationException {
        Class<? extends Event> eventClass = (Class<? extends Event>) Class.forName(eventClassName);
        plugin.getServer().getPluginManager().registerEvent(
                eventClass,
                REGISTRATION_STUB,
                EventPriority.LOWEST,
                executor,
                plugin,
                false
        );
    }

    private void handleDelegate(org.bukkit.event.Listener ignored, Event wgEvent) {
        try {
            if (!shouldAllow(wgEvent)) {
                return;
            }
            setAllowed.invoke(wgEvent, true);
            setSilent.invoke(wgEvent, true);
        } catch (ReflectiveOperationException exception) {
            plugin.getLogger().warning("WorldGuard war allow failed: " + exception.getMessage());
        }
    }

    private boolean shouldAllow(Event wgEvent) throws ReflectiveOperationException {
        Event original = (Event) getOriginalEvent.invoke(wgEvent);
        if (!(original instanceof BlockBreakEvent breakEvent)) {
            return false;
        }
        Player player = breakEvent.getPlayer();
        if (player == null) {
            return false;
        }
        Block block = breakEvent.getBlock();
        if (!block.getType().name().endsWith("_BANNER")) {
            return false;
        }
        Long clanId = clanStandard.readClanIdFromBlock(block.getState());
        if (clanId == null) {
            return false;
        }
        return flagBreakGate.allowsEnemyStandardBreak(player.getUniqueId(), clanId);
    }
}
