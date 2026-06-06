CREATE TABLE IF NOT EXISTS schema_version (
    version INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS clans (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tag VARCHAR(16) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    description TEXT,
    leader_uuid VARCHAR(36) NOT NULL,
    points INTEGER NOT NULL DEFAULT 0,
    max_slots INTEGER NOT NULL,
    verified INTEGER NOT NULL DEFAULT 0,
    friendly_fire INTEGER NOT NULL DEFAULT 0,
    created_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS clan_members (
    clan_id INTEGER NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    role VARCHAR(32) NOT NULL,
    nickname VARCHAR(32),
    kills INTEGER NOT NULL DEFAULT 0,
    deaths INTEGER NOT NULL DEFAULT 0,
    joined_at BIGINT NOT NULL,
    PRIMARY KEY (clan_id, player_uuid),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_members_player ON clan_members(player_uuid);
