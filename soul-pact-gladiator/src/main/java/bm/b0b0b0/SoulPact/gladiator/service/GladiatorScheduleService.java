package bm.b0b0b0.SoulPact.gladiator.service;

import bm.b0b0b0.SoulPact.api.SoulPactApi;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaSchedule;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class GladiatorScheduleService {

    private final SoulPactApi api;
    private final ArenaCatalog catalog;
    private final GladiatorEventService eventService;
    private final ZoneId zone = ZoneId.systemDefault();
    private final ConcurrentHashMap<Long, Long> triggeredMinutes = new ConcurrentHashMap<>();

    public GladiatorScheduleService(SoulPactApi api, ArenaCatalog catalog, GladiatorEventService eventService) {
        this.api = api;
        this.catalog = catalog;
        this.eventService = eventService;
    }

    public void checkDue() {
        LocalDateTime nowMinute = LocalDateTime.now(zone).truncatedTo(ChronoUnit.MINUTES);
        long minuteKey = nowMinute.toEpochSecond(zone.getRules().getOffset(nowMinute)) / 60;
        for (Map.Entry<String, List<ArenaSchedule>> entry : catalog.allSchedules().entrySet()) {
            for (ArenaSchedule schedule : entry.getValue()) {
                if (!schedule.matches(nowMinute)) {
                    continue;
                }
                Long previous = triggeredMinutes.put(schedule.id(), minuteKey);
                if (previous != null && previous == minuteKey) {
                    continue;
                }
                api.scheduler().runSync(() -> eventService.start(schedule.arenaName()));
            }
        }
    }

    public Optional<NextEvent> nextEvent() {
        NextEvent next = null;
        for (Map.Entry<String, List<ArenaSchedule>> entry : catalog.allSchedules().entrySet()) {
            boolean enabled = catalog.find(entry.getKey()).map(arena -> arena.enabled()).orElse(false);
            if (!enabled) {
                continue;
            }
            for (ArenaSchedule schedule : entry.getValue()) {
                long atMillis = schedule.nextOccurrenceMillis(zone);
                if (next == null || atMillis < next.atMillis()) {
                    next = new NextEvent(schedule.arenaName(), atMillis);
                }
            }
        }
        return Optional.ofNullable(next);
    }

    public record NextEvent(String arenaName, long atMillis) {
    }
}
