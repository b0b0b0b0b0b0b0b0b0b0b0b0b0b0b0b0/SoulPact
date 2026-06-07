CREATE TABLE IF NOT EXISTS clan_bases (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL UNIQUE,
    world VARCHAR(64) NOT NULL,
    flag_x INTEGER NOT NULL,
    flag_y INTEGER NOT NULL,
    flag_z INTEGER NOT NULL,
    region_name VARCHAR(64) NOT NULL UNIQUE,
    pvp_enabled INTEGER NOT NULL DEFAULT 0,
    mob_spawn_enabled INTEGER NOT NULL DEFAULT 1,
    created_at BIGINT NOT NULL,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_base_border_blocks (
    base_id INTEGER NOT NULL,
    world VARCHAR(64) NOT NULL,
    x INTEGER NOT NULL,
    y INTEGER NOT NULL,
    z INTEGER NOT NULL,
    PRIMARY KEY (base_id, world, x, y, z),
    FOREIGN KEY (base_id) REFERENCES clan_bases(id) ON DELETE CASCADE
);
