CREATE TABLE IF NOT EXISTS clan_treasury (
    clan_id INTEGER NOT NULL PRIMARY KEY,
    balance REAL NOT NULL DEFAULT 0,
    locked INTEGER NOT NULL DEFAULT 0,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_treasury_ledger (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL,
    actor_uuid VARCHAR(36) NOT NULL,
    entry_type VARCHAR(32) NOT NULL,
    amount REAL NOT NULL,
    balance_after REAL NOT NULL,
    note TEXT,
    created_at BIGINT NOT NULL,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_treasury_ledger_clan ON clan_treasury_ledger(clan_id, created_at DESC);

CREATE TABLE IF NOT EXISTS clan_treasury_contributions (
    clan_id INTEGER NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    total_deposited REAL NOT NULL DEFAULT 0,
    PRIMARY KEY (clan_id, player_uuid),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);
