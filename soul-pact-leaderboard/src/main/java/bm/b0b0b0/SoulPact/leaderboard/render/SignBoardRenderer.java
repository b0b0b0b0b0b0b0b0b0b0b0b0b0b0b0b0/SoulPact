package bm.b0b0b0.SoulPact.leaderboard.render;

import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardMessages;
import bm.b0b0b0.SoulPact.leaderboard.message.LeaderboardTextParser;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

public final class SignBoardRenderer {

    private static final int SIGN_LINES = 4;

    private final LeaderboardMessages messages;

    public SignBoardRenderer(LeaderboardMessages messages) {
        this.messages = messages;
    }

    public boolean render(Location location, Map<String, String> placeholders) {
        Block block = location.getBlock();
        if (!(block.getState() instanceof Sign sign)) {
            return false;
        }
        List<String> lines = messages.resolveList("leaderboard.format.sign.lines", placeholders);
        SignSide side = sign.getSide(Side.FRONT);
        for (int index = 0; index < SIGN_LINES; index++) {
            String line = index < lines.size() ? lines.get(index) : "";
            side.line(index, LeaderboardTextParser.parse(line));
        }
        sign.setWaxed(true);
        sign.update();
        return true;
    }
}
