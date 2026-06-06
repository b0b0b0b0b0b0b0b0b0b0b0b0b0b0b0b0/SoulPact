package bm.b0b0b0.SoulPact.api.message;

import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface SoulPactMessages {

    String resolve(Player player, String key);

    String resolve(Player player, String key, Map<String, String> placeholders);

    String resolveDefault(String key);

    String resolveDefault(String key, Map<String, String> placeholders);

    void send(CommandSender sender, String key);

    void send(CommandSender sender, String key, Map<String, String> placeholders);

    void send(Player player, String key);

    void send(Player player, String key, Map<String, String> placeholders);
}
