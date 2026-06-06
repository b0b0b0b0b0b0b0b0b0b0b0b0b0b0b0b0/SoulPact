package bm.b0b0b0.SoulPact.core.database;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import org.bukkit.plugin.java.JavaPlugin;

public final class AsyncDatabaseExecutor {

    private final JavaPlugin plugin;

    public AsyncDatabaseExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        CompletableFuture<T> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                future.complete(supplier.get());
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        return future;
    }

    public CompletableFuture<Void> run(Runnable runnable) {
        return supply(() -> {
            runnable.run();
            return null;
        });
    }

    public void runSync(Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }
}
