INSERT INTO countries (iso_code, name, citizenship, zip_regex, zip_example)
VALUES ('US', 'United States', 'American',
        '^[0-9]{5}(-[0-9]{4})?$', -- 12345 or 12345-6789
        '90210'),

       ('CA', 'Canada', 'Canadian',
        '^[A-Za-z][0-9][A-Za-z][ -]?[0-9][A-Za-z][0-9]$', -- A1A 1A1
        'K1A 0B1'),

       ('DE', 'Germany', 'German',
        '^[0-9]{5}$', -- 5 digits
        '10115'),

       ('FR', 'France', 'French',
        '^[0-9]{5}$',
        '75001'),

       ('IT', 'Italy', 'Italian',
        '^[0-9]{5}$',
        '00100'),

       ('ES', 'Spain', 'Spanish',
        '^[0-9]{5}$',
        '28001'),

       ('NL', 'Netherlands', 'Dutch',
        '^[0-9]{4}\\s?[A-Za-z]{2}$', -- 1234 AB
        '1011 AC'),

       ('GB', 'United Kingdom', 'British',
        '^[A-Za-z]{1,2}[0-9][A-Za-z0-9]? ?[0-9][A-Za-z]{2}$', -- EC1A 1BB
        'SW1A 1AA'),

       ('SE', 'Sweden', 'Swedish',
        '^[0-9]{3}\\s?[0-9]{2}$', -- 123 45
        '114 55'),

       ('NO', 'Norway', 'Norwegian',
        '^[0-9]{4}$',
        '0150'),

       ('UA', 'Ukraine', 'Ukrainian',
        '^[0-9]{5}$',
        '01001'),

       ('BY', 'Belarus', 'Belarusian',
        '^[0-9]{6}$', -- 6 digits
        '220030');
