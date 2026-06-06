package bm.b0b0b0.SoulPact.core.integration;

public interface PluginIntegration {

    String id();

    String displayName();

    void hook();

    boolean available();
}
