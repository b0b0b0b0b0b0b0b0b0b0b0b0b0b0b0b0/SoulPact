package bm.b0b0b0.SoulPact.core.placeholder;

import java.util.List;
import java.util.UUID;

public final class ClanPlaceholderSnapshot {

    private static final ClanPlaceholderSnapshot EMPTY = new ClanPlaceholderSnapshot(
            false,
            0L,
            "",
            "",
            "",
            null,
            "",
            0,
            0,
            0,
            0,
            0,
            false,
            false,
            false,
            0L,
            "",
            0,
            0,
            0,
            0,
            0,
            List.of(),
            List.of(),
            List.of(),
            0.0D,
            0,
            0,
            0,
            "",
            0
    );

    private final boolean hasClan;
    private final long clanId;
    private final String tag;
    private final String name;
    private final String description;
    private final UUID leaderId;
    private final String leaderName;
    private final String bannerData;
    private final int points;
    private final int warsWon;
    private final int warsLost;
    private final int maxSlots;
    private final int memberCount;
    private final int onlineCount;
    private final boolean verified;
    private final boolean friendlyFire;
    private final boolean joinOpen;
    private final long createdAt;
    private final String memberRole;
    private final int memberKills;
    private final int memberDeaths;
    private final int clanKills;
    private final int clanDeaths;
    private final List<String> memberNames;
    private final List<String> onlineMemberNames;
    private final List<String> allyTags;
    private final double bankBalance;
    private final int statsLeave;
    private final int statsKick;
    private final int statsJoined;
    private final int roleRank;

    public ClanPlaceholderSnapshot(
            boolean hasClan,
            long clanId,
            String tag,
            String name,
            String description,
            UUID leaderId,
            String leaderName,
            int points,
            int warsWon,
            int warsLost,
            int maxSlots,
            int memberCount,
            boolean verified,
            boolean friendlyFire,
            boolean joinOpen,
            long createdAt,
            String memberRole,
            int memberKills,
            int memberDeaths,
            int clanKills,
            int clanDeaths,
            int onlineCount,
            List<String> memberNames,
            List<String> onlineMemberNames,
            List<String> allyTags,
            double bankBalance,
            int statsJoined,
            int statsLeave,
            int statsKick,
            String bannerData,
            int roleRank
    ) {
        this.hasClan = hasClan;
        this.clanId = clanId;
        this.tag = tag;
        this.name = name;
        this.description = description;
        this.leaderId = leaderId;
        this.leaderName = leaderName;
        this.bannerData = bannerData == null ? "" : bannerData;
        this.points = points;
        this.warsWon = warsWon;
        this.warsLost = warsLost;
        this.maxSlots = maxSlots;
        this.memberCount = memberCount;
        this.onlineCount = onlineCount;
        this.verified = verified;
        this.friendlyFire = friendlyFire;
        this.joinOpen = joinOpen;
        this.createdAt = createdAt;
        this.memberRole = memberRole == null ? "" : memberRole;
        this.memberKills = memberKills;
        this.memberDeaths = memberDeaths;
        this.clanKills = clanKills;
        this.clanDeaths = clanDeaths;
        this.memberNames = List.copyOf(memberNames);
        this.onlineMemberNames = List.copyOf(onlineMemberNames);
        this.allyTags = List.copyOf(allyTags);
        this.bankBalance = bankBalance;
        this.statsJoined = statsJoined;
        this.statsLeave = statsLeave;
        this.statsKick = statsKick;
        this.roleRank = roleRank;
    }

    public static ClanPlaceholderSnapshot empty() {
        return EMPTY;
    }

    public boolean hasClan() {
        return hasClan;
    }

    public long clanId() {
        return clanId;
    }

    public String tag() {
        return tag;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public UUID leaderId() {
        return leaderId;
    }

    public String leaderName() {
        return leaderName;
    }

    public String bannerData() {
        return bannerData;
    }

    public int points() {
        return points;
    }

    public int warsWon() {
        return warsWon;
    }

    public int warsLost() {
        return warsLost;
    }

    public int maxSlots() {
        return maxSlots;
    }

    public int memberCount() {
        return memberCount;
    }

    public int onlineCount() {
        return onlineCount;
    }

    public boolean verified() {
        return verified;
    }

    public boolean friendlyFire() {
        return friendlyFire;
    }

    public boolean joinOpen() {
        return joinOpen;
    }

    public long createdAt() {
        return createdAt;
    }

    public String memberRole() {
        return memberRole;
    }

    public int memberKills() {
        return memberKills;
    }

    public int memberDeaths() {
        return memberDeaths;
    }

    public int clanKills() {
        return clanKills;
    }

    public int clanDeaths() {
        return clanDeaths;
    }

    public List<String> memberNames() {
        return memberNames;
    }

    public List<String> onlineMemberNames() {
        return onlineMemberNames;
    }

    public List<String> allyTags() {
        return allyTags;
    }

    public double bankBalance() {
        return bankBalance;
    }

    public int statsJoined() {
        return statsJoined;
    }

    public int statsLeave() {
        return statsLeave;
    }

    public int statsKick() {
        return statsKick;
    }

    public int roleRank() {
        return roleRank;
    }
}
