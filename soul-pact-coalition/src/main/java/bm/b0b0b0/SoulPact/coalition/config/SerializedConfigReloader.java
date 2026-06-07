package bm.b0b0b0.SoulPact.coalition.config;

import java.nio.file.Path;
import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

public final class SerializedConfigReloader {

    private SerializedConfigReloader() {
    }

    public static void reload(JavaPlugin plugin, YamlSerializable settings, Path relativePath) {
        Path path = plugin.getDataFolder().toPath().resolve(relativePath);
        settings.reload(path);
    }
}
