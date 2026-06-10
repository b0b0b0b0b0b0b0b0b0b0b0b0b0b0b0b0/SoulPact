package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.clan.model.Clan;
import bm.b0b0b0.SoulPact.clan.model.ClanMail;
import bm.b0b0b0.SoulPact.clan.model.ClanMember;
import bm.b0b0b0.SoulPact.clan.repository.ClanMailRepository;
import bm.b0b0b0.SoulPact.clan.repository.ClanRepository;
import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import bm.b0b0b0.SoulPact.core.database.AsyncDatabaseExecutor;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import bm.b0b0b0.SoulPact.core.placeholder.ClanPlaceholderInvalidatorRegistry;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanMailService {

    private final ClanRepository clanRepository;
    private final ClanMailRepository mailRepository;
    private final ClanConfig clanConfig;
    private final MessageService messageService;
    private final AsyncDatabaseExecutor asyncDatabaseExecutor;

    public ClanMailService(
            ClanRepository clanRepository,
            ClanMailRepository mailRepository,
            ClanConfig clanConfig,
            MessageService messageService,
            AsyncDatabaseExecutor asyncDatabaseExecutor
    ) {
        this.clanRepository = clanRepository;
        this.mailRepository = mailRepository;
        this.clanConfig = clanConfig;
        this.messageService = messageService;
        this.asyncDatabaseExecutor = asyncDatabaseExecutor;
    }

    public void sendUsageHint(Player player) {
        messageService.send(player, "clan.mail.usage");
    }

    public void send(Player player, String rawText) {
        String text = rawText == null ? "" : rawText.trim();
        if (text.isEmpty()) {
            sendUsageHint(player);
            return;
        }
        if (text.length() > clanConfig.mailMessageMaxLength()) {
            messageService.send(player, "clan.mail.too-long", Map.of(
                    "max", String.valueOf(clanConfig.mailMessageMaxLength())
            ));
            return;
        }
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.mail.not-in-clan"));
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            long now = System.currentTimeMillis();
            return mailRepository.send(clan.id(), player.getUniqueId(), player.getName(), text, now)
                    .thenCompose(mail -> mailRepository.trimToLimit(clan.id(), clanConfig.mailMaxStored()))
                    .thenCompose(ignored -> clanRepository.findMembersByClanId(clan.id()))
                    .thenApply(members -> {
                        asyncDatabaseExecutor.runSync(() -> {
                            ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
                            notifyMembers(clan, members, player, text);
                        });
                        return null;
                    });
        });
    }

    public void read(Player player, int page) {
        int safePage = Math.max(1, page);
        int pageSize = Math.max(1, clanConfig.mailPageSize());
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.mail.not-in-clan"));
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            return mailRepository.countByClanId(clan.id()).thenCompose(total ->
                    mailRepository.findPage(clan.id(), (safePage - 1) * pageSize, pageSize).thenCompose(mails ->
                            mailRepository.markRead(clan.id(), player.getUniqueId(), System.currentTimeMillis())
                                    .thenApply(ignored -> {
                                        asyncDatabaseExecutor.runSync(() -> {
                                            ClanPlaceholderInvalidatorRegistry.invalidatePlayer(player.getUniqueId());
                                            presentPage(player, mails, total, safePage, pageSize);
                                        });
                                        return null;
                                    })
                    )
            );
        });
    }

    public void clear(Player player) {
        clanRepository.findByPlayerId(player.getUniqueId()).thenCompose(clanOptional -> {
            if (clanOptional.isEmpty()) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.mail.not-in-clan"));
                return CompletableFuture.completedFuture(null);
            }
            Clan clan = clanOptional.get();
            if (!clan.leaderId().equals(player.getUniqueId())) {
                asyncDatabaseExecutor.runSync(() -> messageService.send(player, "clan.mail.only-leader"));
                return CompletableFuture.completedFuture(null);
            }
            return mailRepository.clear(clan.id()).thenApply(removed -> {
                asyncDatabaseExecutor.runSync(() -> {
                    ClanPlaceholderInvalidatorRegistry.invalidateClan(clan.id());
                    messageService.send(player, "clan.mail.cleared", Map.of(
                            "count", String.valueOf(removed)
                    ));
                });
                return null;
            });
        });
    }

    private void notifyMembers(Clan clan, List<ClanMember> members, Player sender, String text) {
        messageService.send(sender, "clan.mail.sent");
        for (ClanMember member : members) {
            Player online = Bukkit.getPlayer(member.playerId());
            if (online == null || online.getUniqueId().equals(sender.getUniqueId())) {
                continue;
            }
            messageService.send(online, "clan.mail.received", Map.of(
                    "sender", sender.getName(),
                    "tag", clan.tag(),
                    "message", text
            ));
        }
    }

    private void presentPage(Player player, List<ClanMail> mails, int total, int page, int pageSize) {
        if (total == 0) {
            messageService.send(player, "clan.mail.empty");
            return;
        }
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);
        if (mails.isEmpty()) {
            messageService.send(player, "clan.mail.page-empty", Map.of(
                    "page", String.valueOf(page),
                    "pages", String.valueOf(pages)
            ));
            return;
        }
        messageService.send(player, "clan.mail.header", Map.of(
                "page", String.valueOf(page),
                "pages", String.valueOf(pages),
                "total", String.valueOf(total)
        ));
        DateTimeFormatter formatter = timeFormatter(player);
        for (ClanMail mail : mails) {
            messageService.send(player, "clan.mail.entry", Map.of(
                    "sender", mail.senderName(),
                    "message", mail.message(),
                    "time", formatter.format(Instant.ofEpochMilli(mail.createdAt()))
            ));
        }
        if (page < pages) {
            messageService.send(player, "clan.mail.footer-next", Map.of(
                    "next", String.valueOf(page + 1)
            ));
        }
    }

    private DateTimeFormatter timeFormatter(Player player) {
        String pattern = messageService.resolve(player, "clan.mail.time-format");
        try {
            return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault());
        } catch (IllegalArgumentException exception) {
            return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault());
        }
    }
}
