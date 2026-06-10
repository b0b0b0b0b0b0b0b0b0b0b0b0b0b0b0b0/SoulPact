package bm.b0b0b0.SoulPact.quests.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public final class QuestDefinitionParser {

    private static final int SEGMENT_ID = 0;
    private static final int SEGMENT_TYPE = 1;
    private static final int SEGMENT_MISSION = 2;
    private static final int SEGMENT_FILTER = 3;
    private static final int SEGMENT_AMOUNT = 4;
    private static final int SEGMENT_POINTS = 5;
    private static final int SEGMENT_TREASURY = 6;
    private static final int SEGMENT_COMMANDS = 7;
    private static final int MIN_SEGMENTS = 5;

    private QuestDefinitionParser() {
    }

    public static Optional<QuestDefinition> parse(String spec) {
        if (spec == null || spec.isBlank()) {
            return Optional.empty();
        }
        String[] segments = spec.split("\\|");
        if (segments.length < MIN_SEGMENTS) {
            return Optional.empty();
        }
        try {
            String id = segments[SEGMENT_ID].trim().toLowerCase(Locale.ROOT);
            QuestType type = QuestType.valueOf(segments[SEGMENT_TYPE].trim().toUpperCase(Locale.ROOT));
            QuestMission mission = QuestMission.valueOf(segments[SEGMENT_MISSION].trim().toUpperCase(Locale.ROOT));
            Set<String> filters = parseFilters(segments[SEGMENT_FILTER]);
            int amount = Integer.parseInt(segments[SEGMENT_AMOUNT].trim());
            int points = segments.length > SEGMENT_POINTS ? Integer.parseInt(segments[SEGMENT_POINTS].trim()) : 0;
            double treasury = segments.length > SEGMENT_TREASURY ? Double.parseDouble(segments[SEGMENT_TREASURY].trim()) : 0D;
            List<String> commands = segments.length > SEGMENT_COMMANDS ? parseCommands(segments[SEGMENT_COMMANDS]) : List.of();
            if (id.isEmpty() || amount <= 0) {
                return Optional.empty();
            }
            return Optional.of(new QuestDefinition(id, type, mission, filters, amount, points, treasury, commands));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    private static Set<String> parseFilters(String segment) {
        Set<String> filters = new LinkedHashSet<>();
        for (String token : segment.split(",")) {
            String normalized = token.trim().toUpperCase(Locale.ROOT);
            if (!normalized.isEmpty() && !normalized.equals("*")) {
                filters.add(normalized);
            }
        }
        return Set.copyOf(filters);
    }

    private static List<String> parseCommands(String segment) {
        List<String> commands = new ArrayList<>();
        for (String token : segment.split("&&")) {
            String normalized = token.trim();
            if (!normalized.isEmpty()) {
                commands.add(normalized);
            }
        }
        return List.copyOf(commands);
    }
}
