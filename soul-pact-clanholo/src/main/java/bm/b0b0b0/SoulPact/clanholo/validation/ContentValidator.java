package bm.b0b0b0.SoulPact.clanholo.validation;

import bm.b0b0b0.SoulPact.clanholo.config.ClanHoloConfig;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ContentValidator {

    private final ClanHoloConfig config;

    public ContentValidator(ClanHoloConfig config) {
        this.config = config;
    }

    public Optional<String> validateLine(String content) {
        if (content == null) {
            return Optional.empty();
        }
        String trimmed = content.trim();
        if (trimmed.isEmpty()) {
            return Optional.empty();
        }
        if (trimmed.length() > config.maxLineLength()) {
            return Optional.of("clanholo.error.line-too-long");
        }
        for (String blocked : config.blockedWords()) {
            if (blocked == null || blocked.isBlank()) {
                continue;
            }
            if (blocked.toLowerCase(Locale.ROOT).startsWith("regex:")) {
                String patternText = blocked.substring("regex:".length());
                if (Pattern.compile(patternText, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(trimmed).find()) {
                    return Optional.of("clanholo.error.blocked-word");
                }
                continue;
            }
            if (trimmed.toLowerCase(Locale.ROOT).contains(blocked.toLowerCase(Locale.ROOT))) {
                return Optional.of("clanholo.error.blocked-word");
            }
        }
        return Optional.empty();
    }
}
