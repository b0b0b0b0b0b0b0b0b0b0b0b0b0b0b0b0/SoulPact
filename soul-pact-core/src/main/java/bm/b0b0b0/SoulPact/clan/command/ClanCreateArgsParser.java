package bm.b0b0b0.SoulPact.clan.command;

public final class ClanCreateArgsParser {

    private ClanCreateArgsParser() {
    }

    public static ParsedCreateArgs parse(String rawArgs) {
        if (rawArgs == null) {
            return ParsedCreateArgs.usage();
        }
        String trimmed = rawArgs.trim();
        if (trimmed.isEmpty()) {
            return ParsedCreateArgs.usage();
        }
        int spaceIndex = indexOfFirstWhitespace(trimmed);
        if (spaceIndex < 0) {
            return ParsedCreateArgs.missingName(trimmed);
        }
        String tag = trimmed.substring(0, spaceIndex);
        String name = trimmed.substring(spaceIndex).trim();
        if (name.isEmpty()) {
            return ParsedCreateArgs.missingName(tag);
        }
        return ParsedCreateArgs.create(tag, name);
    }

    public static String firstToken(String rawArgs) {
        if (rawArgs == null) {
            return "";
        }
        String trimmed = rawArgs.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        int spaceIndex = indexOfFirstWhitespace(trimmed);
        if (spaceIndex < 0) {
            return trimmed;
        }
        return trimmed.substring(0, spaceIndex);
    }

    private static int indexOfFirstWhitespace(String value) {
        for (int index = 0; index < value.length(); index++) {
            if (Character.isWhitespace(value.charAt(index))) {
                return index;
            }
        }
        return -1;
    }

    public enum Kind {
        USAGE,
        MISSING_NAME,
        CREATE
    }

    public record ParsedCreateArgs(Kind kind, String tag, String name) {

        public static ParsedCreateArgs usage() {
            return new ParsedCreateArgs(Kind.USAGE, null, null);
        }

        public static ParsedCreateArgs missingName(String tag) {
            return new ParsedCreateArgs(Kind.MISSING_NAME, tag, null);
        }

        public static ParsedCreateArgs create(String tag, String name) {
            return new ParsedCreateArgs(Kind.CREATE, tag, name);
        }
    }
}
