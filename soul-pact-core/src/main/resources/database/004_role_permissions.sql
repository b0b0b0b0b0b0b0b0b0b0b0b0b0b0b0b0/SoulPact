CREATE TABLE IF NOT EXISTS clan_role_permissions (
    clan_id INTEGER NOT NULL,
    role VARCHAR(32) NOT NULL,
    permission_key VARCHAR(32) NOT NULL,
    enabled INTEGER NOT NULL,
    PRIMARY KEY (clan_id, role, permission_key),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_role_permissions_clan ON clan_role_permissions(clan_id);
