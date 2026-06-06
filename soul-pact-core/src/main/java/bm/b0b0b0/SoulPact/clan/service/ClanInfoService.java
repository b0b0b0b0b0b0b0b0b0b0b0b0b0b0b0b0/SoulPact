package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.message.ClanInfoChatPresenter;
import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Player;

public final class ClanInfoService {

    private final ClanRepository clanRepository;
    private final ClanConfig clanConfig;
    private final ClanCreateValidator validator;
    private final ClanInfoChatPresenter infoChatPresenter;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanInfoService(
            ClanRepository clanRepository,
            ClanConfig clanConfig,
            ClanCreateValidator validator,
            ClanInfoChatPresenter infoChatPresenter,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.clanConfig = clanConfig;
        this.validator = validator;
        this.infoChatPresenter = infoChatPresenter;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void showOwn(Player player) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenAccept(clanOptional ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (clanOptional.isEmpty()) {
                        messageService.send(player, "clan.info.no-clan");
                        return;
                    }
                    showClan(player, clanOptional.get());
                })
        );
    }

    public void showByTag(Player player, String rawTag) {
        if (rawTag == null || rawTag.isBlank()) {
            showOwn(player);
            return;
        }
        Optional<String> tagError = validator.validateTag(rawTag, clanConfig);
        if (tagError.isPresent()) {
            messageService.send(player, "clan.info.not-found", Map.of("tag", rawTag.trim()));
            return;
        }
        String tag = validator.normalizeTag(rawTag);
        clanRepository.findByTag(tag).thenAccept(clanOptional ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    if (clanOptional.isEmpty()) {
                        messageService.send(player, "clan.info.not-found", Map.of("tag", tag));
                        return;
                    }
                    showClan(player, clanOptional.get());
                })
        );
    }

    private void showClan(Player player, Clan clan) {
        clanRepository.countMembers(clan.id()).thenAccept(memberCount ->
                asyncDatabaseExecutor.runSync(() -> {
                    if (!player.isOnline()) {
                        return;
                    }
                    infoChatPresenter.show(player, clan, memberCount);
                })
        );
    }
}
