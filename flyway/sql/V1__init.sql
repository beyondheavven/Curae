CREATE SCHEMA IF NOT EXISTS public;
CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS public.roles (
    id SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS public.user_roles (
    user_id INT REFERENCES public.users(id),
    role_id INT REFERENCES public.roles(id),
    PRIMARY KEY (user_id, role_id)
);

INSERT INTO public.roles (role_name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO public.roles (role_name) VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO public.users (username, password)
VALUES ('admin', 'PaSsWoRd')
ON CONFLICT DO NOTHING;
