create table if not exists grade
(
    id         varchar constraint grade_pk primary key,
    exam_id    varchar not null
        constraint grade_exam_fk references exam,
    student_id varchar not null
        constraint grade_student_fk references student,
    value      numeric,
    updated_at timestamp not null,
    constraint grade_exam_student_uq unique (exam_id, student_id)
);
