create table if not exists course_teacher_assignment
(
    id            varchar constraint course_teacher_assignment_pk primary key,
    course_id     varchar not null
        constraint course_teacher_assignment_course_fk references course,
    teacher_id    varchar not null
        constraint course_teacher_assignment_teacher_fk references teacher,
    academic_year integer not null
);
