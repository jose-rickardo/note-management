create table if not exists grade_history
(
    id                    varchar constraint grade_history_pk primary key,
    grade_id              varchar not null
        constraint grade_history_grade_fk references grade,
    old_value             numeric,
    new_value             numeric,
    reason                varchar not null,
    changed_by_account_id varchar not null
        constraint grade_history_account_fk references account,
    changed_at            timestamp not null
);
