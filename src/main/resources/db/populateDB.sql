DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM meals;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin');


INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES (100000, '10.02.2021 08:01:00', 'Завтрак', 1500),
       (100001, '10.02.2021 08:01:00', 'Завтрак', 1500),
       (100000, '9.02.2021 08:01:00', 'Завтрак', 1500),
       (100000, '20.02.2021 08:01:00', 'Завтрак', 100),
       (100001, '20.02.2021 05:01:00', 'Завтрак', 100),
       (100001, '2.02.2021 08:01:00', 'Завтрак', 100),
       (100001, '20.02.2021 20:01:00', 'Ужин', 100),
       (100000, '2.01.2021 18:01:00', 'Обед', 100),
       (100000, '2.02.2021 08:01:00', 'Завтрак', 700),
       (100000, '10.02.2021 21:30:45', 'Ужин', 500);