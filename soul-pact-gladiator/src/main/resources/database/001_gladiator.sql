CREATE TABLE IF NOT EXISTS glad_arenas (
    name VARCHAR(32) NOT NULL PRIMARY KEY,
    enabled INTEGER NOT NULL DEFAULT 1,
    icon VARCHAR(64) NOT NULL DEFAULT 'IRON_SWORD',
    tag TEXT NOT NULL DEFAULT '',
    description TEXT NOT NULL DEFAULT '',
    holder_clan_id INTEGER NOT NULL DEFAULT 0,
    holder_clan_tag TEXT NOT NULL DEFAULT '',
    region TEXT NOT NULL DEFAULT '',
    spawn_point TEXT NOT NULL DEFAULT '',
    watch_point TEXT NOT NULL DEFAULT '',
    exit_point TEXT NOT NULL DEFAULT '',
    lobby_point TEXT NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS glad_rewards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    arena_name VARCHAR(32) NOT NULL,
    command TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_glad_rewards_arena ON glad_rewards(arena_name);

CREATE TABLE IF NOT EXISTS glad_schedules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    arena_name VARCHAR(32) NOT NULL,
    schedule_type VARCHAR(16) NOT NULL,
    day_of_week INTEGER NOT NULL DEFAULT 0,
    hour INTEGER NOT NULL,
    minute INTEGER NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_glad_schedules_arena ON glad_schedules(arena_name);
