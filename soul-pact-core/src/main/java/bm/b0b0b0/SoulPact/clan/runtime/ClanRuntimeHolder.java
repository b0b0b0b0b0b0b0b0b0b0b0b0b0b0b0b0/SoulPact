package bm.b0b0b0.SoulPact.clan.runtime;

import java.util.concurrent.atomic.AtomicReference;

public final class ClanRuntimeHolder {

    private final AtomicReference<ClanRuntimeServices> services = new AtomicReference<>();

    public void install(ClanRuntimeServices runtimeServices) {
        services.set(runtimeServices);
    }

    public ClanRuntimeServices services() {
        return services.get();
    }
}
