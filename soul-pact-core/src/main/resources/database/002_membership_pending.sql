CREATE TABLE IF NOT EXISTS clan_invites (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    inviter_uuid VARCHAR(36) NOT NULL,
    created_at BIGINT NOT NULL,
    UNIQUE(clan_id, player_uuid),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_join_requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    clan_id INTEGER NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    created_at BIGINT NOT NULL,
    UNIQUE(clan_id, player_uuid),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS clan_join_blocks (
    clan_id INTEGER NOT NULL,
    player_uuid VARCHAR(36) NOT NULL,
    blocked_at BIGINT NOT NULL,
    PRIMARY KEY (clan_id, player_uuid),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_invites_player ON clan_invites(player_uuid);

CREATE INDEX IF NOT EXISTS idx_clan_join_requests_clan ON clan_join_requests(clan_id);
