CREATE TABLE IF NOT EXISTS user_snapshots
(
    user_id     bigint PRIMARY KEY,
    email       varchar(255) NOT NULL,
    first_name  varchar(255) NOT NULL,
    last_name   varchar(255) NOT NULL,
    birth_date  date,
    profile_url varchar(255),
    status      varchar(30)  NOT NULL
);
