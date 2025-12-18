--changeset Codex:fix-sequences
--comment Fix sequences after seed inserts with explicit IDs

select setval(pg_get_serial_sequence('categories', 'id'), coalesce((select max(id) from categories), 0) + 1, false);
select setval(pg_get_serial_sequence('cities', 'id'), coalesce((select max(id) from cities), 0) + 1, false);
select setval(pg_get_serial_sequence('roles', 'id'), coalesce((select max(id) from roles), 0) + 1, false);
select setval(pg_get_serial_sequence('users', 'id'), coalesce((select max(id) from users), 0) + 1, false);
select setval(pg_get_serial_sequence('posts', 'id'), coalesce((select max(id) from posts), 0) + 1, false);

