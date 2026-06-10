package bm.b0b0b0.SoulPact.clanholo.extension;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.api.SoulPactGuiExtension;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloMessages;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloTextParser;
import java.util.Map;
import org.bukkit.entity.Player;

public final class ClanHoloExtension implements SoulPactGuiExtension {

    private final ClanHoloMessages messages;
    private final Runnable reloadAction;

    public ClanHoloExtension(ClanHoloMessages messages, Runnable reloadAction) {
        this.messages = messages;
        this.reloadAction = reloadAction;
    }

    @Override
    public String id() {
        return "clanholo";
    }

    @Override
    public void enable(SoulPactApi api) {
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
        reloadAction.run();
    }

    @Override
    public void openGui(Player player) {
        for (String line : messages.resolveList("clanholo.command.help", Map.of())) {
            player.sendMessage(ClanHoloTextParser.parse(line));
        }
    }
}
