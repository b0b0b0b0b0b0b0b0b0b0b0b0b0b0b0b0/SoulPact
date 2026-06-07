package bm.b0b0b0.SoulPact.clan.service;

import bm.b0b0b0.SoulPact.core.config.ClanConfig;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class ClanCreateValidator {

    private static final Pattern TAG_PATTERN = Pattern.compile("^[a-zA-Z0-9\u0400-\u04FF]+$");

    public Optional<String> validateTag(String rawTag, ClanConfig clanConfig) {
        if (rawTag == null || rawTag.isBlank()) {
            return Optional.of("clan.create.invalid-tag");
        }
        String tag = rawTag.trim();
        if (tag.length() < clanConfig.tagMinLength() || tag.length() > clanConfig.tagMaxLength()) {
            return Optional.of("clan.create.invalid-tag-length");
        }
        if (!TAG_PATTERN.matcher(tag).matches()) {
            return Optional.of("clan.create.invalid-tag");
        }
        return Optional.empty();
    }

    public Optional<String> validateName(String rawName, ClanConfig clanConfig) {
        if (rawName == null || rawName.isBlank()) {
            return Optional.of("clan.create.invalid-name");
        }
        String name = rawName.trim();
        if (name.length() > clanConfig.nameMaxLength()) {
            return Optional.of("clan.create.invalid-name-length");
        }
        return Optional.empty();
    }

    public Optional<String> validateDescription(String rawDescription, ClanConfig clanConfig) {
        if (rawDescription == null || rawDescription.isBlank()) {
            return Optional.of("clan.description.invalid-empty");
        }
        String description = normalizeDescription(rawDescription);
        if (description.isBlank()) {
            return Optional.of("clan.description.invalid-empty");
        }
        if (description.length() > clanConfig.descriptionMaxLength()) {
            return Optional.of("clan.description.invalid-length");
        }
        return Optional.empty();
    }

    public String normalizeDescription(String rawDescription) {
        return rawDescription.trim().replace('\n', ' ').replaceAll("\\s+", " ");
    }

    public String normalizeTag(String rawTag) {
        return rawTag.trim().toUpperCase(Locale.ROOT);
    }
}
