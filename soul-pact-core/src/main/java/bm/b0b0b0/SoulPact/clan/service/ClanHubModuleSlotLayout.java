package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import java.util.Map;
import java.util.Optional;

public final class ClanHubModuleSlotLayout {

    private final Map<Integer, SoulPactGuiExtension> bySlot;

    public ClanHubModuleSlotLayout(Map<Integer, SoulPactGuiExtension> bySlot) {
        this.bySlot = bySlot;
    }

    public Map<Integer, SoulPactGuiExtension> bySlot() {
        return bySlot;
    }

    public Optional<SoulPactGuiExtension> extensionAt(int slot) {
        return Optional.ofNullable(bySlot.get(slot));
    }
}
