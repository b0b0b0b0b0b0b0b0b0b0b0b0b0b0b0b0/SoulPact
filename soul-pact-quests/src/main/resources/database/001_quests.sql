CREATE TABLE IF NOT EXISTS clan_quests (
    clan_id INTEGER NOT NULL,
    quest_id VARCHAR(64) NOT NULL,
    status VARCHAR(16) NOT NULL,
    progress INTEGER NOT NULL DEFAULT 0,
    started_at BIGINT NOT NULL,
    expires_at BIGINT NOT NULL DEFAULT 0,
    completed_at BIGINT NOT NULL DEFAULT 0,
    started_by VARCHAR(36) NOT NULL,
    PRIMARY KEY (clan_id, quest_id),
    FOREIGN KEY (clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clan_quests_status ON clan_quests(status);
