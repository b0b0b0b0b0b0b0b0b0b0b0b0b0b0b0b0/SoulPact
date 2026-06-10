CREATE TABLE IF NOT EXISTS lb_boards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    statistic VARCHAR(16) NOT NULL,
    rank_position INTEGER NOT NULL,
    kind VARCHAR(16) NOT NULL,
    world VARCHAR(64) NOT NULL,
    x DOUBLE NOT NULL,
    y DOUBLE NOT NULL,
    z DOUBLE NOT NULL,
    yaw REAL NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_lb_boards_stat ON lb_boards(statistic);
