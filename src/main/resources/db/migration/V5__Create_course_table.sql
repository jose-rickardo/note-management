create table if not exists course
(
    id      varchar constraint course_pk primary key,
    ref     varchar not null unique,
    title   varchar not null,
    credits integer not null
);
