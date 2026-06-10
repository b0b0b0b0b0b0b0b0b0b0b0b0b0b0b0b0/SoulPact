package bm.b0b0b0.SoulPact.core.placeholder;

public record ClanPlaceholderComputed(
        String hasClanFormatted,
        String hasClanFormattedLevelUp,
        String hasClanFormattedLevelDown,
        String tagNoColor,
        String tagFormatted,
        String tagFormattedNoColor,
        String tagFormattedLevelUp,
        String tagFormattedLevelDown,
        String descNoColor,
        String leaderFormatted,
        String creationDate,
        String creationDateFormatted,
        String membersLine,
        String onlineMembersLine,
        String alliesLine,
        String verifiedTagFormatted,
        String bankBalanceFormatted,
        String patentFormatted,
        String patentName,
        String clanKdr,
        String level,
        String maxLevelReached,
        String pointsToNextLevel,
        String friendlyFireFormatted,
        String joinOpenFormatted
) {

    private static final ClanPlaceholderComputed EMPTY = new ClanPlaceholderComputed(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "0",
            "0",
            "false",
            "0",
            "",
            ""
    );

    public static ClanPlaceholderComputed empty() {
        return EMPTY;
    }

    public ClanPlaceholderComputed withOnlineMembersLine(String onlineMembersLine) {
        return new ClanPlaceholderComputed(
                hasClanFormatted,
                hasClanFormattedLevelUp,
                hasClanFormattedLevelDown,
                tagNoColor,
                tagFormatted,
                tagFormattedNoColor,
                tagFormattedLevelUp,
                tagFormattedLevelDown,
                descNoColor,
                leaderFormatted,
                creationDate,
                creationDateFormatted,
                membersLine,
                onlineMembersLine,
                alliesLine,
                verifiedTagFormatted,
                bankBalanceFormatted,
                patentFormatted,
                patentName,
                clanKdr,
                level,
                maxLevelReached,
                pointsToNextLevel,
                friendlyFireFormatted,
                joinOpenFormatted
        );
    }
}
