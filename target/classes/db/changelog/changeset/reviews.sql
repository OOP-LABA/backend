--changeset Codex:reviews
--comment Ratings & reviews for profiles

create table reviews (
    id bigserial primary key,
    rating int not null,
    content text,
    reviewer_id bigint not null references user_profiles(user_id),
    reviewee_id bigint not null references user_profiles(user_id),
    post_id bigint not null references posts(id),
    created_at timestamp not null default now(),
    constraint reviews_rating_check check (rating between 1 and 5),
    constraint reviews_unique_reviewer_post unique (reviewer_id, post_id)
);

create index reviews_reviewee_idx on reviews(reviewee_id);

