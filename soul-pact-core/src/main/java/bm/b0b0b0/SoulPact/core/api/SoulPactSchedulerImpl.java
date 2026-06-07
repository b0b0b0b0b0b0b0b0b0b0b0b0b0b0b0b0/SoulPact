package bm.b0b0b0.SoulPact.core.api;

import bm.b0b0b0.SoulPact.api.platform.SoulPactScheduler;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class SoulPactSchedulerImpl implements SoulPactScheduler {

    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public SoulPactSchedulerImpl(AsyncDatabaseExecutor asyncDatabaseExecutor) {
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    @Override
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return asyncDatabaseExecutor.supply(supplier);
    }

    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        return asyncDatabaseExecutor.run(runnable);
    }

    @Override
    public void runSync(Runnable runnable) {
        asyncDatabaseExecutor.runSync(runnable);
    }

    @Override
    public void runSyncLater(long delayTicks, Runnable runnable) {
        asyncDatabaseExecutor.runSyncLater(delayTicks, runnable);
    }
}
