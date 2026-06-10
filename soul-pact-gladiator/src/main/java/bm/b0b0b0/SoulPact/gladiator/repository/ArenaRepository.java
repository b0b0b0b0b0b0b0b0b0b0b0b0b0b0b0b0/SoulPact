package bm.b0b0b0.SoulPact.gladiator.repository;

import bm.b0b0b0.SoulPact.gladiator.model.Arena;
import bm.b0b0b0.SoulPact.gladiator.model.ArenaSchedule;
import java.util.List;
import java.util.Map;

public interface ArenaRepository {

    List<Arena> loadArenas();

    Map<String, List<String>> loadRewards();

    Map<String, List<ArenaSchedule>> loadSchedules();

    void upsertArena(Arena arena);

    void deleteArena(String arenaName);

    long addReward(String arenaName, String command);

    void clearRewards(String arenaName);

    long addSchedule(String arenaName, ArenaSchedule schedule);

    boolean deleteSchedule(long scheduleId);
}
