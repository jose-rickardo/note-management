create table if not exists account
(
    id       varchar constraint account_pk primary key,
    email    varchar not null unique,
    password varchar not null,
    role     varchar not null
);
