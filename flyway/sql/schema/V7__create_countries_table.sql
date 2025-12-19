CREATE TABLE countries (
                           id           bigserial PRIMARY KEY,
                           iso_code     varchar(2)  NOT NULL UNIQUE,
                           name         varchar(255) NOT NULL,
                           citizenship  varchar(255) NOT NULL,
                           zip_regex    varchar(255) NOT NULL,
                           zip_example  varchar(20)
);
