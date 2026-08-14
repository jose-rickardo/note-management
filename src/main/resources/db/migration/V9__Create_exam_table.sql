create table if not exists exam
(
    id            varchar constraint exam_pk primary key,
    course_id     varchar not null
        constraint exam_course_fk references course,
    date_exam     timestamp not null,
    coefficient   numeric not null,
    academic_year integer not null
);
