--changeset RuslanYuneev:insert data
--comment Insert data migration

insert into categories (id, name, is_verifiable)
values
    (1, 'Medicine', true),
    (2, 'Household', false);

insert into cities (id, name)
values
    (1, 'Moscow'),
    (2, 'New York'),
    (3, 'Kazan'),
    (4, 'Troitsk');

insert into roles (id, name)
values
    (1, 'ROLE_USER'),
    (2, 'ROLE_ADMIN');

insert into users (id, username, password, email)
values
    (1, 'supervoid13', '$2a$12$JVNAtOTx/W.unuNoPALDHuKnySqljYysiElbgkHVWrH0dM7Wcfz6C', 'spaceejs@gmail.com'),
    (2, 'admin', '$2a$12$O5qsnJ5oqu1JbjgP9V/lyuV8Z6zGE1uNxcsCFkVS9w6WPfcGvq6ui', 'admin@gmail.com');