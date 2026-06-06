package bm.b0b0b0.SoulPact.soulPact;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.core.SoulPactApplication;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulPact extends JavaPlugin {

    private SoulPactApplication application;

    @Override
    public void onEnable() {
        application = new SoulPactApplication(this);
        application.enable();
    }

    @Override
    public void onDisable() {
        if (application != null) {
            application.disable();
        }
    }

    public SoulPactApi getApi() {
        return application == null ? null : application.api();
    }
}
