CREATE TABLE IF NOT EXISTS clan_chest_spoils (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    owner_clan_id INTEGER NOT NULL,
    source_clan_id INTEGER NOT NULL,
    captured_at BIGINT NOT NULL,
    FOREIGN KEY (owner_clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_chest_spoils_items (
    spoils_id INTEGER NOT NULL,
    cell_index INTEGER NOT NULL,
    item_data BLOB NOT NULL,
    PRIMARY KEY (spoils_id, cell_index),
    FOREIGN KEY (spoils_id) REFERENCES clan_chest_spoils(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_chest_spoils_owner ON clan_chest_spoils(owner_clan_id);
