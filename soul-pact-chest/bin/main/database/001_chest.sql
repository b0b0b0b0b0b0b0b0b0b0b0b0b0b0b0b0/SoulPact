CREATE TABLE IF NOT EXISTS clan_chest_meta (
    clan_id INTEGER NOT NULL PRIMARY KEY,
    unlocked_cells INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_chest_items (
    clan_id INTEGER NOT NULL,
    cell_index INTEGER NOT NULL,
    item_data BLOB NOT NULL,
    PRIMARY KEY (clan_id, cell_index),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);
