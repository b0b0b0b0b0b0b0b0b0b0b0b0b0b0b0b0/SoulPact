package bm.b0b0b0.SoulPact.gladiator.util;

import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import java.time.Duration;

public final class GladTimeFormat {

    private GladTimeFormat() {
    }

    public static String remaining(GladiatorMessages messages, long untilMillis) {
        long remaining = untilMillis - System.currentTimeMillis();
        if (remaining <= 0) {
            return messages.resolveDefault("gladiator.time.now");
        }
        Duration duration = Duration.ofMillis(remaining);
        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        if (days > 0) {
            return days + messages.resolveDefault("gladiator.time.days")
                    + " " + hours + messages.resolveDefault("gladiator.time.hours");
        }
        if (hours > 0) {
            return hours + messages.resolveDefault("gladiator.time.hours")
                    + " " + minutes + messages.resolveDefault("gladiator.time.minutes");
        }
        if (minutes > 0) {
            return minutes + messages.resolveDefault("gladiator.time.minutes");
        }
        return duration.toSeconds() + messages.resolveDefault("gladiator.time.seconds");
    }
}
