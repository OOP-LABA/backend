--changeset Codex:post-avatars
--comment Store task attachment image in DB

alter table posts
    add column if not exists avatar_data bytea;

alter table posts
    add column if not exists avatar_content_type varchar(100);

alter table posts
    add column if not exists avatar_filename varchar(255);

