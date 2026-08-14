create table if not exists course_group_assignment
(
    id            varchar constraint course_group_assignment_pk primary key,
    course_id     varchar not null
        constraint course_group_assignment_course_fk references course,
    group_id      varchar not null
        constraint course_group_assignment_group_fk references class_group,
    academic_year integer not null
);
