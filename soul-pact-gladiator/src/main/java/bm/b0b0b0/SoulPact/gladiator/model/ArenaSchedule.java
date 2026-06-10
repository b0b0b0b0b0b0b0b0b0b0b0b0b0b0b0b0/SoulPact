package bm.b0b0b0.SoulPact.gladiator.model;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ArenaSchedule(
        long id,
        String arenaName,
        ScheduleType type,
        int dayOfWeek,
        int hour,
        int minute
) {

    public long nextOccurrenceMillis(ZoneId zone) {
        ZonedDateTime now = ZonedDateTime.now(zone);
        ZonedDateTime candidate = now.toLocalDate().atTime(hour, minute).atZone(zone);
        if (type == ScheduleType.DAILY) {
            if (!candidate.isAfter(now)) {
                candidate = candidate.plusDays(1);
            }
            return candidate.toInstant().toEpochMilli();
        }
        DayOfWeek target = DayOfWeek.of(Math.min(7, Math.max(1, dayOfWeek)));
        while (candidate.getDayOfWeek() != target || !candidate.isAfter(now)) {
            candidate = candidate.plusDays(1);
        }
        return candidate.toInstant().toEpochMilli();
    }

    public boolean matches(LocalDateTime moment) {
        if (moment.getHour() != hour || moment.getMinute() != minute) {
            return false;
        }
        if (type == ScheduleType.DAILY) {
            return true;
        }
        return moment.getDayOfWeek().getValue() == Math.min(7, Math.max(1, dayOfWeek));
    }
}
