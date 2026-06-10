CREATE TABLE IF NOT EXISTS clan_war_declarations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    attacker_clan_id INTEGER NOT NULL,
    defender_clan_id INTEGER NOT NULL,
    declared_by_uuid VARCHAR(36) NOT NULL,
    created_at BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    FOREIGN KEY (attacker_clan_id) REFERENCES clans(id) ON DELETE CASCADE,
    FOREIGN KEY (defender_clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_wars (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    attacker_clan_id INTEGER NOT NULL,
    defender_clan_id INTEGER NOT NULL,
    started_at BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    FOREIGN KEY (attacker_clan_id) REFERENCES clans(id) ON DELETE CASCADE,
    FOREIGN KEY (defender_clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_war_captures (
    war_id INTEGER NOT NULL,
    holder_clan_id INTEGER NOT NULL,
    target_clan_id INTEGER NOT NULL DEFAULT 0,
    captured_at BIGINT NOT NULL,
    deadline_at BIGINT NOT NULL,
    PRIMARY KEY (war_id),
    FOREIGN KEY (war_id) REFERENCES clan_wars(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_war_declarations_defender ON clan_war_declarations(defender_clan_id, status);
CREATE INDEX IF NOT EXISTS idx_clan_wars_attacker ON clan_wars(attacker_clan_id, status);
CREATE INDEX IF NOT EXISTS idx_clan_wars_defender ON clan_wars(defender_clan_id, status);
