CREATE TABLE IF NOT EXISTS coalitions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    created_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS coalition_members (
    coalition_id INTEGER NOT NULL,
    clan_id INTEGER NOT NULL,
    joined_at BIGINT NOT NULL,
    PRIMARY KEY (clan_id),
    FOREIGN KEY (coalition_id) REFERENCES coalitions(id) ON DELETE CASCADE,
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS coalition_invites (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    coalition_id INTEGER NOT NULL,
    inviter_clan_id INTEGER NOT NULL,
    target_clan_id INTEGER NOT NULL,
    invited_by_uuid VARCHAR(36) NOT NULL,
    created_at BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL,
    FOREIGN KEY (coalition_id) REFERENCES coalitions(id) ON DELETE CASCADE,
    FOREIGN KEY (inviter_clan_id) REFERENCES clans(id) ON DELETE CASCADE,
    FOREIGN KEY (target_clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_coalition_members_coalition ON coalition_members(coalition_id);
CREATE INDEX IF NOT EXISTS idx_coalition_invites_target ON coalition_invites(target_clan_id, status);
