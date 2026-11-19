CREATE TABLE IF NOT EXISTS user_snapshots
(
    user_id     bigint PRIMARY KEY,
    email       varchar(255),
    first_name  varchar(255),
    last_name   varchar(255),
    birth_date  date,
    profile_url varchar(255),
    status      varchar(30)
);

CREATE TABLE IF NOT EXISTS service_snapshots
(
    service_id       bigint PRIMARY KEY,
    name             varchar(255) NOT NULL,
    description      text,
    price            decimal(19, 2),
    duration_minutes int
);