package bm.b0b0b0.SoulPact.clanholo.service;

import bm.b0b0b0.SoulPact.api.clan.ClanSnapshot;
import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import bm.b0b0b0.SoulPact.clanholo.message.ClanHoloMessages;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

public final class HologramTemplateService {

    private final ClanHoloMessages messages;
    private final Supplier<ClanHoloConfig> configSupplier;

    public HologramTemplateService(ClanHoloMessages messages, Supplier<ClanHoloConfig> configSupplier) {
        this.messages = messages;
        this.configSupplier = configSupplier;
    }

    public List<String> resolveTemplateLines(String template, ClanSnapshot clan) {
        if (template == null || template.isBlank()) {
            return List.of();
        }
        Map<String, String> placeholders = clan == null ? Map.of() : Map.of(
                "tag", clan.tag(),
                "name", clan.name(),
                "description", clan.description() == null ? "" : clan.description(),
                "points", String.valueOf(clan.points())
        );
        String key = "clanholo.templates." + template.toLowerCase(Locale.ROOT);
        List<String> lines = messages.resolveList(key, placeholders);
        if (!lines.isEmpty()) {
            return lines;
        }
        if ("rules".equalsIgnoreCase(template)) {
            return new ArrayList<>(configSupplier.get().defaultRulesLines());
        }
        return List.of();
    }
}
