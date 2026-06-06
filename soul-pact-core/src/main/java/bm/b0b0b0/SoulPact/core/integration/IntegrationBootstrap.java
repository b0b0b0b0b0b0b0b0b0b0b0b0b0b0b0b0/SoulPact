package bm.b0b0b0.SoulPact.core.integration;

public final class IntegrationBootstrap {

    private final IntegrationRegistry integrationRegistry;
    private final VaultIntegration vaultIntegration;
    private final SkinRestorerIntegration skinRestorerIntegration;

    public IntegrationBootstrap(IntegrationRegistry integrationRegistry) {
        this.integrationRegistry = integrationRegistry;
        this.vaultIntegration = new VaultIntegration();
        this.skinRestorerIntegration = new SkinRestorerIntegration();
    }

    public void registerDefaults() {
        integrationRegistry.register(vaultIntegration);
        integrationRegistry.register(new EssentialsIntegration());
        integrationRegistry.register(new PlaceholderApiIntegration());
        integrationRegistry.register(skinRestorerIntegration);
    }

    public void hookAll() {
        integrationRegistry.hookAll();
    }

    public VaultIntegration vaultIntegration() {
        return vaultIntegration;
    }

    public SkinRestorerIntegration skinRestorerIntegration() {
        return skinRestorerIntegration;
    }
}
