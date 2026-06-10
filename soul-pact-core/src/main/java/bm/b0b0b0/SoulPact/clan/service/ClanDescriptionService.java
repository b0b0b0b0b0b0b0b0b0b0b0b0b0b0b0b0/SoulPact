package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.api.event.ClanDescriptionChangeEvent;
import bm.b0b0b0.SoulPact.api.event.SoulPactEvents;
import bm.b0b0b0.SoulPact.clan.gui.ClanDescriptionChatPrompt;
import bm.b0b0b0.SoulPact.clan.gui.ClanGuiOpenService;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class ClanDescriptionService {

    private final ClanRepository clanRepository;
    private final ClanConfig clanConfig;
    private final ClanCreateValidator validator;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;
    private final ClanGuiOpenService guiOpenService;
    private final ClanDescriptionChatPrompt descriptionChatPrompt;

    public ClanDescriptionService(
            ClanRepository clanRepository,
            ClanConfig clanConfig,
            ClanCreateValidator validator,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor,
            ClanGuiOpenService guiOpenService,
            ClanDescriptionChatPrompt descriptionChatPrompt
    ) {
        this.clanRepository = clanRepository;
        this.clanConfig = clanConfig;
        this.validator = validator;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
        this.guiOpenService = guiOpenService;
        this.descriptionChatPrompt = descriptionChatPrompt;
    }

    public void openPrompt(Player player) {
        descriptionChatPrompt.open(player, clanConfig.descriptionMaxLength());
    }

    public void sendUsageHint(Player player) {
        messageService.send(player, "clan.description.usage", Map.of(
                "max", String.valueOf(clanConfig.descriptionMaxLength())
        ));
    }

    public void sendMissingTextHint(Player player) {
        messageService.send(player, "clan.description.missing-text", Map.of(
                "max", String.valueOf(clanConfig.descriptionMaxLength())
        ));
    }

    public void update(Player player, String rawDescription) {
        Optional<String> validationError = validator.validateDescription(rawDescription, clanConfig);
        if (validationError.isPresent()) {
            messageService.send(player, validationError.get(), Map.of(
                    "max", String.valueOf(clanConfig.descriptionMaxLength())
            ));
            return;
        }
        String description = validator.normalizeDescription(rawDescription);
        clanRepository.findByPlayerId(player.getUniqueId()).thenAccept(clanOptional -> asyncDatabaseExecutor.runSync(() -> {
            if (!player.isOnline()) {
                return;
            }
            if (clanOptional.isEmpty()) {
                messageService.send(player, "clan.leave.not-in-clan");
                return;
            }
            var clan = clanOptional.get();
            if (!ClanStaffPermissions.isLeader(clan, player.getUniqueId())) {
                messageService.send(player, "clan.settings.not-leader");
                return;
            }
            clanRepository.updateDescription(clan.id(), description).thenAccept(updated -> asyncDatabaseExecutor.runSync(() -> {
                if (updated) {
                    SoulPactEvents.fire(new ClanDescriptionChangeEvent(
                            clan.id(),
                            clan.tag(),
                            description,
                            player.getName()
                    ));
                }
                if (!player.isOnline()) {
                    return;
                }
                if (!updated) {
                    messageService.send(player, "clan.description.failed");
                    return;
                }
                messageService.send(player, "clan.description.updated", Map.of(
                        "description", description
                ));
                guiOpenService.openSettings(player);
            }));
        }));
    }
}
