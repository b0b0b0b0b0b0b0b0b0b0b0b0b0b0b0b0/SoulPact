package bm.b0b0b0.SoulPact.gladiator.command.admin;

import bm.b0b0b0.SoulPact.gladiator.message.GladiatorMessages;
import bm.b0b0b0.SoulPact.gladiator.message.GladiatorTextParser;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaSchedule;
import bm.b0b0b0.SoulPact.gladiator.model.ScheduleType;
import bm.b0b0b0.SoulPact.gladiator.service.ArenaCatalog;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.CommandSender;

public final class SchedulerAdminHandler {

    private final GladiatorMessages messages;
    private final ArenaCatalog catalog;

    public SchedulerAdminHandler(GladiatorMessages messages, ArenaCatalog catalog) {
        this.messages = messages;
        this.catalog = catalog;
    }

    public void handle(CommandSender sender, String[] args) {
        if (args.length < 3) {
            messages.send(sender, "gladiator.usage.scheduler", Map.of());
            return;
        }
        if (catalog.find(args[2]).isEmpty()) {
            messages.send(sender, "gladiator.error.unknown-arena", Map.of("arena", args[2]));
            return;
        }
        switch (args[1].toLowerCase(Locale.ROOT)) {
            case "list" -> list(sender, args[2]);
            case "remove" -> remove(sender, args);
            case "create" -> create(sender, args);
            default -> messages.send(sender, "gladiator.usage.scheduler", Map.of());
        }
    }

    private void list(CommandSender sender, String arenaName) {
        List<ArenaSchedule> schedules = catalog.schedulesOf(arenaName);
        messages.send(sender, "gladiator.admin.scheduler-list-header", Map.of(
                "arena", arenaName,
                "count", String.valueOf(schedules.size())
        ));
        for (ArenaSchedule schedule : schedules) {
            sender.sendMessage(GladiatorTextParser.parse(messages.resolveDefault("gladiator.admin.scheduler-list-line", Map.of(
                    "id", String.valueOf(schedule.id()),
                    "type", schedule.type().name(),
                    "day", String.valueOf(schedule.dayOfWeek()),
                    "time", String.format(Locale.ROOT, "%02d:%02d", schedule.hour(), schedule.minute())
            ))));
        }
    }

    private void remove(CommandSender sender, String[] args) {
        if (args.length < 4) {
            messages.send(sender, "gladiator.usage.scheduler-remove", Map.of());
            return;
        }
        long scheduleId;
        try {
            scheduleId = Long.parseLong(args[3]);
        } catch (NumberFormatException exception) {
            messages.send(sender, "gladiator.usage.scheduler-remove", Map.of());
            return;
        }
        if (catalog.removeSchedule(args[2], scheduleId)) {
            messages.send(sender, "gladiator.admin.scheduler-removed", Map.of("arena", args[2], "id", args[3]));
        } else {
            messages.send(sender, "gladiator.error.unknown-schedule", Map.of("id", args[3]));
        }
    }

    private void create(CommandSender sender, String[] args) {
        if (args.length < 6) {
            messages.send(sender, "gladiator.usage.scheduler-create", Map.of());
            return;
        }
        ScheduleType type;
        try {
            type = ScheduleType.valueOf(args[3].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            messages.send(sender, "gladiator.usage.scheduler-create", Map.of());
            return;
        }
        Integer day = parseInt(args[4]);
        int[] time = parseTime(args[5]);
        if (day == null || day < 1 || day > 7 || time == null) {
            messages.send(sender, "gladiator.usage.scheduler-create", Map.of());
            return;
        }
        catalog.addSchedule(args[2], new ArenaSchedule(0L, args[2], type, day, time[0], time[1]));
        messages.send(sender, "gladiator.admin.scheduler-created", Map.of(
                "arena", args[2],
                "type", type.name(),
                "day", String.valueOf(day),
                "time", args[5]
        ));
    }

    private Integer parseInt(String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private int[] parseTime(String raw) {
        String[] parts = raw.split(":");
        if (parts.length != 2) {
            return null;
        }
        Integer hour = parseInt(parts[0]);
        Integer minute = parseInt(parts[1]);
        if (hour == null || minute == null || hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            return null;
        }
        return new int[]{hour, minute};
    }
}
