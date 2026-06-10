CREATE TABLE IF NOT EXISTS coalition_invite_blocks (
    target_clan_id INTEGER NOT NULL,
    inviter_clan_id INTEGER NOT NULL,
    blocked_at BIGINT NOT NULL,
    PRIMARY KEY (target_clan_id, inviter_clan_id),
    FOREIGN KEY (target_clan_id) REFERENCES clans(id) ON DELETE CASCADE,
    FOREIGN KEY (inviter_clan_id) REFERENCES clans(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_coalition_invite_blocks_inviter ON coalition_invite_blocks(inviter_clan_id);
