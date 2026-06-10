CREATE TABLE IF NOT EXISTS clan_mail (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL,
    sender_uuid VARCHAR(36) NOT NULL,
    sender_name VARCHAR(32) NOT NULL,
    message TEXT NOT NULL,
    created_at BIGINT NOT NULL,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_mail_clan ON clan_mail(clan_id, created_at DESC);

CREATE TABLE IF NOT EXISTS clan_mail_reads (
    clan_id INTEGER NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    last_read_at BIGINT NOT NULL,
    PRIMARY KEY (clan_id, player_uuid),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_homes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL,
    name VARCHAR(32) NOT NULL,
    world VARCHAR(64) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    yaw REAL NOT NULL DEFAULT 0,
    pitch REAL NOT NULL DEFAULT 0,
    password_hash VARCHAR(64) NOT NULL DEFAULT '',
    created_at BIGINT NOT NULL,
    UNIQUE(clan_id, name),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_homes_clan ON clan_homes(clan_id);
