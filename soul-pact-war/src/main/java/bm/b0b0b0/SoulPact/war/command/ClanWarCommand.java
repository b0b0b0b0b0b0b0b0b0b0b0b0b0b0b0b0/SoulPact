package bm.b0b0b0.SoulPact.war.command;

import bm.b0b0b0.SoulPact.war.gui.WarGuiService;
import bm.b0b0b0.SoulPact.war.service.ClanWarService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ClanWarCommand implements CommandExecutor {

    private final WarGuiService guiService;
    private final ClanWarService warService;

    public ClanWarCommand(WarGuiService guiService, ClanWarService warService) {
        this.guiService = guiService;
        this.warService = warService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        if (args.length == 0) {
            guiService.openPendingList(player);
            return true;
        }
        if (args.length < 2) {
            return true;
        }
        long declarationId;
        try {
            declarationId = Long.parseLong(args[1]);
        } catch (NumberFormatException exception) {
            return true;
        }
        if ("accept".equalsIgnoreCase(args[0])) {
            warService.acceptWar(player, declarationId);
            return true;
        }
        if ("ransom".equalsIgnoreCase(args[0])) {
            warService.payRansom(player, declarationId);
        }
        return true;
    }
}
