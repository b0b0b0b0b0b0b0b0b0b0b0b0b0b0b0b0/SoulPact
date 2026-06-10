package bm.b0b0b0.SoulPact.quests.util;

import bm.b0b0b0.SoulPact.quests.message.QuestsMessages;
import java.time.Duration;

public final class QuestTimeFormat {

    private QuestTimeFormat() {
    }

    public static String remaining(QuestsMessages messages, long untilMillis) {
        long remaining = untilMillis - System.currentTimeMillis();
        if (remaining <= 0) {
            return messages.resolveDefault("quests.time.now");
        }
        Duration duration = Duration.ofMillis(remaining);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        if (days > 0) {
            return days + messages.resolveDefault("quests.time.days")
                    + " " + hours + messages.resolveDefault("quests.time.hours");
        }
        if (hours > 0) {
            return hours + messages.resolveDefault("quests.time.hours")
                    + " " + minutes + messages.resolveDefault("quests.time.minutes");
        }
        if (minutes > 0) {
            return minutes + messages.resolveDefault("quests.time.minutes");
        }
        return duration.toSeconds() + messages.resolveDefault("quests.time.seconds");
    }
}
