--changeset RuslanYuneev:insert user profiles
--comment Insert user profiles migration

insert into user_profiles (user_id, first_name, second_name, city_id)
values
    (1, 'Ruslan', 'Yuneev', 4);