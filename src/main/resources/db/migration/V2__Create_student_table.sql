create table if not exists student
(
    id          varchar constraint student_pk primary key,
    std         varchar not null unique,
    first_name  varchar not null,
    last_name   varchar not null,
    entry_year  integer not null,
    account_id  varchar
        constraint student_account_fk references account
);
