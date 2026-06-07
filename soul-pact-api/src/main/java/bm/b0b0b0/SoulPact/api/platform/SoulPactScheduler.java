package bm.b0b0b0.SoulPact.api.platform;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface SoulPactScheduler {

    <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier);

    CompletableFuture<Void> runAsync(Runnable runnable);

    void runSync(Runnable runnable);

    void runSyncLater(long delayTicks, Runnable runnable);
}
