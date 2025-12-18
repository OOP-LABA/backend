--changeset RuslanYuneev:init
--comment Init migration

create table users (
    id  bigserial primary key ,
    username varchar(30) not null unique,
    password varchar(80) not null,
    email varchar(50) not null unique
);

create table roles (
    id serial primary key,
    name varchar(50) not null unique
);

create table users_roles (
    user_id bigint not null references users(id),
    role_id bigint not null references roles(id),
    primary key (user_id, role_id)
);


create table cities (
    id serial primary key,
    name varchar(85) unique
);

create table user_profiles (
    user_id bigint primary key references users(id),
    first_name varchar(50),
    second_name varchar(50),
    city_id int references cities(id)
);

create table categories (
    id serial primary key,
    name varchar(100) not null unique,
    is_verifiable boolean not null
);

create table posts (
    id bigserial primary key,
    title varchar(255) not null,
    content text,
    category_id int not null references categories(id),
    created_at timestamp,
    user_id bigint references users(id),
    avatar_url varchar(255),
    goal bigint not null,
    raised bigint,
    account_details varchar(35)
);

create table comments (
    id bigserial primary key,
    content text not null,
    user_id bigint references user_profiles(user_id),
    post_id bigint references posts(id),
    created_at timestamp
);

create table images (
    id bigserial primary key,
    url varchar(255) not null unique,
    post_id bigint not null references posts(id)
);