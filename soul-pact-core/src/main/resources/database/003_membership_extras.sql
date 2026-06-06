CREATE TABLE IF NOT EXISTS clan_membership_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_uuid VARCHAR(36) NOT NULL,
    clan_id INTEGER NOT NULL,
    clan_tag VARCHAR(16) NOT NULL,
    clan_name VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    joined_at BIGINT NOT NULL,
    left_at BIGINT NOT NULL,
    reason VARCHAR(16) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_clan_membership_history_player ON clan_membership_history(player_uuid, left_at DESC);

CREATE TABLE IF NOT EXISTS clan_membership_notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    player_uuid VARCHAR(36) NOT NULL,
    kind VARCHAR(16) NOT NULL,
    clan_id INTEGER NOT NULL,
    clan_tag VARCHAR(16) NOT NULL,
    clan_name VARCHAR(64) NOT NULL,
    created_at BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_clan_membership_notifications_player ON clan_membership_notifications(player_uuid, created_at ASC);
