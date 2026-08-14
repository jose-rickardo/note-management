create table if not exists teacher
(
    id          varchar constraint teacher_pk primary key,
    first_name  varchar not null,
    last_name   varchar not null,
    account_id  varchar
        constraint teacher_account_fk references account
);
