CREATE TABLE IF NOT EXISTS ch_holograms (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL,
    name VARCHAR(32) NOT NULL,
    world VARCHAR(64) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    creator_uuid VARCHAR(36) NOT NULL,
    creator_name VARCHAR(32) NOT NULL,
    template VARCHAR(16) NOT NULL DEFAULT '',
    created_at BIGINT NOT NULL,
    UNIQUE(clan_id, name),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_ch_holograms_clan ON ch_holograms(clan_id);

CREATE TABLE IF NOT EXISTS ch_hologram_lines (
    hologram_id INTEGER NOT NULL,
    line_index INTEGER NOT NULL,
    content TEXT NOT NULL,
    PRIMARY KEY (hologram_id, line_index),
    FOREIGN KEY (hologram_id) REFERENCES ch_holograms(id) ON DELETE CASCADE
);
