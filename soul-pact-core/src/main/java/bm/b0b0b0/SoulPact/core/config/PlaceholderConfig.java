package bm.b0b0b0.SoulPact.core.config;

import bm.b0b0b0.SoulPact.core.config.settings.PlaceholderSettings;

public final class PlaceholderConfig {

    private final int cacheMillis;
    private final int maxClanLevel;
    private final int pointsPerLevel;
    private final int extraSlots;
    private final String levelUpSymbol;
    private final String levelDownSymbol;
    private final String tagFormated;
    private final String tagFormatedNocolor;
    private final String hasClanFormated;
    private final String hasClanFormatedNone;
    private final String leaderFormated;
    private final String creationDateFormated;
    private final String booleanYes;
    private final String booleanNo;
    private final String verifiedTagFormated;
    private final String membersSeparator;
    private final String alliesSeparator;
    private final String mailUnavailable;
    private final String homeUnavailable;
    private final String rivalUnavailable;

    public PlaceholderConfig(PlaceholderSettings settings) {
        this.cacheMillis = settings.cacheMillis;
        this.maxClanLevel = settings.maxClanLevel;
        this.pointsPerLevel = settings.pointsPerLevel;
        this.extraSlots = settings.extraSlots;
        this.levelUpSymbol = settings.levelUpSymbol;
        this.levelDownSymbol = settings.levelDownSymbol;
        this.tagFormated = settings.tagFormated;
        this.tagFormatedNocolor = settings.tagFormatedNocolor;
        this.hasClanFormated = settings.hasClanFormated;
        this.hasClanFormatedNone = settings.hasClanFormatedNone;
        this.leaderFormated = settings.leaderFormated;
        this.creationDateFormated = settings.creationDateFormated;
        this.booleanYes = settings.booleanYes;
        this.booleanNo = settings.booleanNo;
        this.verifiedTagFormated = settings.verifiedTagFormated;
        this.membersSeparator = settings.membersSeparator;
        this.alliesSeparator = settings.alliesSeparator;
        this.mailUnavailable = settings.mailUnavailable;
        this.homeUnavailable = settings.homeUnavailable;
        this.rivalUnavailable = settings.rivalUnavailable;
    }

    public int cacheMillis() {
        return cacheMillis;
    }

    public int maxClanLevel() {
        return maxClanLevel;
    }

    public int pointsPerLevel() {
        return pointsPerLevel;
    }

    public int extraSlots() {
        return extraSlots;
    }

    public String levelUpSymbol() {
        return levelUpSymbol;
    }

    public String levelDownSymbol() {
        return levelDownSymbol;
    }

    public String tagFormated() {
        return tagFormated;
    }

    public String tagFormatedNocolor() {
        return tagFormatedNocolor;
    }

    public String hasClanFormated() {
        return hasClanFormated;
    }

    public String hasClanFormatedNone() {
        return hasClanFormatedNone;
    }

    public String leaderFormated() {
        return leaderFormated;
    }

    public String creationDateFormated() {
        return creationDateFormated;
    }

    public String booleanYes() {
        return booleanYes;
    }

    public String booleanNo() {
        return booleanNo;
    }

    public String verifiedTagFormated() {
        return verifiedTagFormated;
    }

    public String membersSeparator() {
        return membersSeparator;
    }

    public String alliesSeparator() {
        return alliesSeparator;
    }

    public String mailUnavailable() {
        return mailUnavailable;
    }

    public String homeUnavailable() {
        return homeUnavailable;
    }

    public String rivalUnavailable() {
        return rivalUnavailable;
    }
}
