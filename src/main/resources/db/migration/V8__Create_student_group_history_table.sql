create table if not exists student_group_history
(
    id         varchar constraint student_group_history_pk primary key,
    student_id varchar not null
        constraint student_group_history_student_fk references student,
    group_id   varchar not null
        constraint student_group_history_group_fk references class_group,
    start_date timestamp not null,
    end_date   timestamp
);
