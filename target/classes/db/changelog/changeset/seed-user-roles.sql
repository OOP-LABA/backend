--changeset Codex:seed-user-roles
--comment Ensure seeded users have roles

insert into users_roles (user_id, role_id)
values (1, 1)
on conflict do nothing;

insert into users_roles (user_id, role_id)
values (2, 2)
on conflict do nothing;

