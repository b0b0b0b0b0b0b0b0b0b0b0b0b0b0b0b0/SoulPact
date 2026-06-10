package bm.b0b0b0.SoulPact.core.integration;

public final class IntegrationBootstrap {

    private final IntegrationRegistry integrationRegistry;
    private final VaultIntegration vaultIntegration;
    private final SkinRestorerIntegration skinRestorerIntegration;
    private final PlaceholderApiIntegration placeholderApiIntegration;

    public IntegrationBootstrap(IntegrationRegistry integrationRegistry) {
        this.integrationRegistry = integrationRegistry;
        this.vaultIntegration = new VaultIntegration();
        this.skinRestorerIntegration = new SkinRestorerIntegration();
        this.placeholderApiIntegration = new PlaceholderApiIntegration();
    }

    public void registerDefaults() {
        integrationRegistry.register(vaultIntegration);
        integrationRegistry.register(new EssentialsIntegration());
        integrationRegistry.register(placeholderApiIntegration);
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

    public PlaceholderApiIntegration placeholderApiIntegration() {
        return placeholderApiIntegration;
    }
}
