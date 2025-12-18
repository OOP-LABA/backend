--changeset Codex:complaints
--comment Complaints + moderation workflow

create table complaints (
    id bigserial primary key,
    reason text not null,
    reporter_id bigint not null references user_profiles(user_id),
    target_user_id bigint references user_profiles(user_id),
    target_post_id bigint references posts(id),
    status varchar(20) not null default 'OPEN',
    admin_note text,
    created_at timestamp not null default now(),
    resolved_at timestamp,
    resolved_by bigint references users(id),
    constraint complaints_target_check check (target_user_id is not null or target_post_id is not null)
);

create index complaints_status_idx on complaints(status);

