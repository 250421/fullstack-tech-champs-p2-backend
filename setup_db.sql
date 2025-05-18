CREATE DATABASE "nflfantasydraftinventory";

\connect "nflfantasydraftinventory";

CREATE TABLE users(
    user_id SERIAL PRIMARY KEY,
    userName VARCHAR(20) NOT NULL,
    email VARCHAR(30) NOT NULL,
    password TEXT NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'user' CHECK (role IN ('user', 'admin', 'BOT'))
);

CREATE TABLE leagues(
    id SERIAL PRIMARY KEY,
    num_players INTEGER NOT NULL DEFAULT 2,
    drafting BOOLEAN DEFAULT TRUE,
    current_pick INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE draft_picks(
    id SERIAL PRIMARY KEY,
    league_id INTEGER NOT NULL,
    pick_number INTEGER NOT NULL,
    team_id INTEGER NOT NULL,
    player_id INTEGER,
    CONSTRAINT league_id_fk FOREIGN KEY (league_id)
    REFERENCES leagues (id) ON DELETE CASCADE
);

CREATE TABLE players(
    player_id SERIAL PRIMARY KEY,
    position VARCHAR(100) NOT NULL,
    team_id INTEGER,
    player_api_id INTEGER,
    fantasy_points INTEGER,
    name VARCHAR(100),
    team VARCHAR(100),
    number INTEGER,
    opponent VARCHAR(100),
    week INTEGER,
    season INTEGER,
    drafted BOOLEAN DEFAULT FALSE
);

CREATE TABLE teams(
    id SERIAL PRIMARY KEY,
    img_url TEXT, -- base64 string
    team_name VARCHAR(100) NOT NULL,
    is_bot BOOLEAN DEFAULT FALSE,
    user_id INTEGER,
    bot_id INTEGER,
    league_id INTEGER,
    qb VARCHAR(100),
    k VARCHAR(100),
    rb VARCHAR(100),
    wr VARCHAR(100),
    te VARCHAR(100),
    de VARCHAR(100),
    CONSTRAINT user_id_fk FOREIGN KEY (user_id)
    REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE bots(
    id SERIAL PRIMARY KEY,
    league_id INTEGER NOT NULL,
    team_id INTEGER,
    difficulty_level VARCHAR(100),
    strategy VARCHAR(100)
);

ALTER TABLE players
ADD CONSTRAINT team_foreignKey
FOREIGN KEY (team_id) REFERENCES teams(id)
ON DELETE CASCADE;

ALTER TABLE users ALTER COLUMN role SET DEFAULT 'user';

DELETE FROM players
WHERE week NOT IN (1, 2, 3, 4);
