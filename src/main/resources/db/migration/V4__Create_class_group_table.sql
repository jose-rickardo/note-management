create table if not exists class_group
(
    id  varchar constraint class_group_pk primary key,
    ref varchar not null unique
);
