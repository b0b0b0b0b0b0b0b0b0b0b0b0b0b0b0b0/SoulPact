package bm.b0b0b0.SoulPact.clan.command;

import bm.b0b0b0.SoulPact.clan.runtime.ClanRuntimeHolder;
import bm.b0b0b0.SoulPact.core.config.PermissionsConfig;
import bm.b0b0b0.SoulPact.core.database.DataSourceProvider;
import bm.b0b0b0.SoulPact.core.message.MessageService;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

public final class ClanCommandRegistrar {

    private final PermissionsConfig permissionsConfig;
    private final MessageService messageService;
    private final ClanRuntimeHolder runtimeHolder;
    private final DataSourceProvider dataSourceProvider;

    public ClanCommandRegistrar(
            PermissionsConfig permissionsConfig,
            MessageService messageService,
            ClanRuntimeHolder runtimeHolder,
            DataSourceProvider dataSourceProvider
    ) {
        this.permissionsConfig = permissionsConfig;
        this.messageService = messageService;
        this.runtimeHolder = runtimeHolder;
        this.dataSourceProvider = dataSourceProvider;
    }

    public void register(Commands registrar) {
        LiteralCommandNode<CommandSourceStack> clanRoot = Commands.literal("clan")
                .requires(source -> source.getExecutor() instanceof Player player && player.hasPermission(permissionsConfig.clanUse()))
                .executes(context -> executeOpenMenu(context.getSource()))
                .then(Commands.literal("profile").executes(context -> executeProfile(context.getSource())))
                .then(Commands.literal("settings").executes(context -> executeSettings(context.getSource())))
                .then(Commands.literal("help").executes(context -> executeHelpChat(context.getSource())))
                .then(Commands.literal("list").executes(context -> executeList(context.getSource())))
                .then(Commands.literal("info")
                        .executes(context -> executeInfoOwn(context.getSource()))
                        .then(Commands.argument("tag", StringArgumentType.greedyString())
                                .executes(context -> executeInfoFromArgs(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "tag")
                                ))))
                .then(Commands.literal("leave").executes(context -> executeLeave(context.getSource())))
                .then(Commands.literal("disband").executes(context -> executeDisband(context.getSource())))
                .then(Commands.literal("join")
                        .executes(context -> executeJoinUsage(context.getSource()))
                        .then(Commands.argument("target", StringArgumentType.greedyString())
                                .executes(context -> executeJoinRequest(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "target")
                                ))))
                .then(Commands.literal("invite")
                        .then(Commands.literal("accept")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> executeInviteAccept(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "id")
                                        ))))
                        .then(Commands.literal("deny")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> executeInviteDeny(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "id")
                                        )))))
                .then(Commands.literal("request")
                        .then(Commands.literal("accept")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> executeRequestAccept(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "id")
                                        ))))
                        .then(Commands.literal("deny")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> executeRequestDeny(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "id")
                                        ))))
                        .then(Commands.literal("block")
                                .then(Commands.argument("id", StringArgumentType.word())
                                        .executes(context -> executeRequestBlock(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "id")
                                        )))))
                .then(Commands.literal("member")
                        .then(Commands.literal("invite")
                                .then(Commands.argument("player", StringArgumentType.word())
                                        .executes(context -> executeMemberInvite(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "player")
                                        )))))
                .then(Commands.literal("create")
                        .executes(context -> executeCreateUsage(context.getSource()))
                        .then(Commands.argument("args", StringArgumentType.greedyString())
                                .executes(context -> executeCreateFromArgs(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "args")
                                ))))
                .then(Commands.literal("description")
                        .executes(context -> executeDescriptionUsage(context.getSource()))
                        .then(Commands.argument("text", StringArgumentType.greedyString())
                                .executes(context -> executeDescriptionFromArgs(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "text")
                                ))))
                .build();
        registrar.register(clanRoot, "Clan menu and commands");
    }

    private int executeOpenMenu(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady()) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        if (runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().guiOpenService().openHub(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeCreateUsage(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().createService().sendUsageHint(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeCreateFromArgs(CommandSourceStack source, String args) {
        ClanCreateArgsParser.ParsedCreateArgs parsed = ClanCreateArgsParser.parse(args);
        return switch (parsed.kind()) {
            case USAGE -> executeCreateUsage(source);
            case MISSING_NAME -> executeCreateMissingName(source, parsed.tag());
            case CREATE -> executeCreate(source, parsed.tag(), parsed.name());
        };
    }

    private int executeCreateMissingName(CommandSourceStack source, String tag) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().createService().sendMissingNameHint(player, tag);
        return Command.SINGLE_SUCCESS;
    }

    private int executeCreate(CommandSourceStack source, String tag, String name) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().createService().create(player, tag, name);
        return Command.SINGLE_SUCCESS;
    }

    private int executeProfile(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().guiOpenService().openProfile(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeSettings(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().guiOpenService().openSettings(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeDescriptionUsage(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().descriptionService().sendUsageHint(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeDescriptionFromArgs(CommandSourceStack source, String text) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        if (text == null || text.isBlank()) {
            runtimeHolder.services().descriptionService().sendMissingTextHint(player);
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().descriptionService().update(player, text);
        return Command.SINGLE_SUCCESS;
    }

    private int executeHelpChat(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().helpChatPresenter().show(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeList(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().guiOpenService().openList(player, 0);
        return Command.SINGLE_SUCCESS;
    }

    private int executeInfoOwn(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().infoService().showOwn(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeInfoFromArgs(CommandSourceStack source, String args) {
        String tag = ClanCreateArgsParser.firstToken(args);
        if (tag.isBlank()) {
            return executeInfoOwn(source);
        }
        return executeInfoTag(source, tag);
    }

    private int executeInfoTag(CommandSourceStack source, String tag) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().infoService().showByTag(player, tag);
        return Command.SINGLE_SUCCESS;
    }

    private int executeLeave(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().leaveService().leave(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeDisband(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().disbandService().disband(player);
        return Command.SINGLE_SUCCESS;
    }

    private int executeJoinUsage(CommandSourceStack source) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        messageService.send(player, "clan.request.usage");
        return Command.SINGLE_SUCCESS;
    }

    private int executeJoinRequest(CommandSourceStack source, String target) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().membershipService().submitJoinRequest(player, target);
        return Command.SINGLE_SUCCESS;
    }

    private int executeMemberInvite(CommandSourceStack source, String playerName) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        runtimeHolder.services().membershipService().inviteMember(player, playerName);
        return Command.SINGLE_SUCCESS;
    }

    private int executeInviteAccept(CommandSourceStack source, String rawId) {
        return executePendingId(source, rawId, (player, id) ->
                runtimeHolder.services().membershipService().acceptInvite(player, id));
    }

    private int executeInviteDeny(CommandSourceStack source, String rawId) {
        return executePendingId(source, rawId, (player, id) ->
                runtimeHolder.services().membershipService().denyInvite(player, id));
    }

    private int executeRequestAccept(CommandSourceStack source, String rawId) {
        return executePendingId(source, rawId, (player, id) ->
                runtimeHolder.services().membershipService().acceptRequest(player, id));
    }

    private int executeRequestDeny(CommandSourceStack source, String rawId) {
        return executePendingId(source, rawId, (player, id) ->
                runtimeHolder.services().membershipService().denyRequest(player, id));
    }

    private int executeRequestBlock(CommandSourceStack source, String rawId) {
        return executePendingId(source, rawId, (player, id) ->
                runtimeHolder.services().membershipService().blockRequest(player, id));
    }

    private int executePendingId(CommandSourceStack source, String rawId, PendingAction action) {
        if (!(source.getExecutor() instanceof Player player)) {
            messageService.send(source.getSender(), "general.players-only");
            return Command.SINGLE_SUCCESS;
        }
        if (!dataSourceProvider.isReady() || runtimeHolder.services() == null) {
            messageService.send(player, "startup.loading");
            return Command.SINGLE_SUCCESS;
        }
        var parsedId = ClanPendingIdParser.parse(rawId);
        if (parsedId.isEmpty()) {
            messageService.send(player, "clan.pending.invalid-id");
            return Command.SINGLE_SUCCESS;
        }
        action.run(player, parsedId.get());
        return Command.SINGLE_SUCCESS;
    }

    @FunctionalInterface
    private interface PendingAction {
        void run(Player player, long id);
    }
}
