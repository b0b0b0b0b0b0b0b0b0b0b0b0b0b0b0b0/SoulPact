package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.core.config.CreateEconomyState;
import bm.b0b0b0.SoulPact.core.config.EconomyConfig;
import bm.b0b0b0.SoulPact.core.integration.VaultIntegration;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanEconomyMessages {

    private final EconomyConfig economyConfig;
    private final VaultIntegration vaultIntegration;
    private final MessageService messageService;

    public ClanEconomyMessages(
            EconomyConfig economyConfig,
            VaultIntegration vaultIntegration,
            MessageService messageService
    ) {
        this.economyConfig = economyConfig;
        this.vaultIntegration = vaultIntegration;
        this.messageService = messageService;
    }

    public String createCostLine(Player player) {
        return messageService.resolve(player, resolveCreateCostKey(player), resolveCreateCostPlaceholders(player));
    }

    public void sendCreateHint(Player player) {
        CreateEconomyState state = economyConfig.resolveCreateState(vaultIntegration);
        if (state == CreateEconomyState.ACTIVE) {
            messageService.send(player, "clan.command.create-cost-hint", Map.of(
                    "amount", String.valueOf(economyConfig.createCostAmount())
            ));
            return;
        }
        messageService.send(player, "clan.command.create-free-hint");
    }

    private String resolveCreateCostKey(Player player) {
        CreateEconomyState state = economyConfig.resolveCreateState(vaultIntegration);
        if (state == CreateEconomyState.ACTIVE) {
            return "clan.gui.hub.value.create-cost";
        }
        return "clan.gui.hub.value.create-free";
    }

    private Map<String, String> resolveCreateCostPlaceholders(Player player) {
        if (economyConfig.resolveCreateState(vaultIntegration) != CreateEconomyState.ACTIVE) {
            return Map.of();
        }
        return Map.of("amount", String.valueOf(economyConfig.createCostAmount()));
    }
}
