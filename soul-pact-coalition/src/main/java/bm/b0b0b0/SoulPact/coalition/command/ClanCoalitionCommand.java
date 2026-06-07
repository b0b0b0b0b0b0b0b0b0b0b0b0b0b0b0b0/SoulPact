package bm.b0b0b0.SoulPact.coalition.command;

import bm.b0b0b0.SoulPact.coalition.gui.CoalitionGuiService;
import bm.b0b0b0.SoulPact.coalition.message.CoalitionMessages;
import bm.b0b0b0.SoulPact.coalition.service.CoalitionService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ClanCoalitionCommand implements CommandExecutor {

    private final CoalitionGuiService guiService;
    private final CoalitionService coalitionService;
    private final CoalitionMessages messages;

    public ClanCoalitionCommand(
            CoalitionGuiService guiService,
            CoalitionService coalitionService,
            CoalitionMessages messages
    ) {
        this.guiService = guiService;
        this.coalitionService = coalitionService;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length == 0) {
            guiService.open(player);
            return true;
        }
        return switch (args[0].toLowerCase()) {
            case "invite" -> handleInvite(player, args);
            case "accept" -> handleAccept(player, args);
            case "deny" -> handleAcceptDeny(player, args, true);
            case "leave" -> {
                coalitionService.leave(player);
                yield true;
            }
            default -> {
                messages.send(player, "coalition.command.usage");
                yield true;
            }
        };
    }

    private boolean handleInvite(Player player, String[] args) {
        if (args.length < 2) {
            messages.send(player, "coalition.command.invite-usage");
            return true;
        }
        coalitionService.invite(player, args[1]);
        return true;
    }

    private boolean handleAccept(Player player, String[] args) {
        return handleAcceptDeny(player, args, false);
    }

    private boolean handleAcceptDeny(Player player, String[] args, boolean deny) {
        if (args.length < 2) {
            messages.send(player, deny ? "coalition.command.deny-usage" : "coalition.command.accept-usage");
            return true;
        }
        try {
            long inviteId = Long.parseLong(args[1]);
            if (deny) {
                coalitionService.denyInvite(player, inviteId);
            } else {
                coalitionService.acceptInvite(player, inviteId);
            }
        } catch (NumberFormatException exception) {
            messages.send(player, "coalition.error.invalid-id");
        }
        return true;
    }
}
