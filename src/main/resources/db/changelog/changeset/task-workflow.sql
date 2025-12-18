--changeset Codex:task-workflow
--comment Add task workflow fields (status/executor/deposit)

alter table users
    add column is_banned boolean not null default false;

alter table users
    add column ban_reason text;

alter table users
    add column banned_at timestamp;

alter table user_profiles
    add column headline varchar(120);

alter table user_profiles
    add column about text;

alter table user_profiles
    add column skills text;

alter table user_profiles
    add column portfolio text;

alter table user_profiles
    add column contacts text;

alter table posts
    add column status varchar(20) not null default 'OPEN';

alter table posts
    add column executor_id bigint references user_profiles(user_id);

alter table posts
    add column deposit_amount bigint not null default 0;

alter table posts
    add column deposit_status varchar(20) not null default 'NONE';

