package bm.b0b0b0.SoulPact.api;

public interface SoulPactExtension {

    String id();

    void enable(SoulPactApi api);

    void disable();

    void reload();
}
