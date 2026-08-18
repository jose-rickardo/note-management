CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(32)  NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE promotions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code        VARCHAR(64) NOT NULL UNIQUE,
    entry_year  INTEGER     NOT NULL UNIQUE,
    created_at  TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE TABLE academic_years (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    label       VARCHAR(32) NOT NULL UNIQUE,
    start_year  INTEGER     NOT NULL,
    end_year    INTEGER     NOT NULL
);

CREATE TABLE programs (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code    VARCHAR(16)  NOT NULL UNIQUE,
    name    VARCHAR(128) NOT NULL
);

CREATE TABLE students (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID UNIQUE REFERENCES users(id),
    student_number  VARCHAR(32) NOT NULL UNIQUE,
    first_name      VARCHAR(128) NOT NULL,
    last_name       VARCHAR(128) NOT NULL,
    promotion_id    UUID NOT NULL REFERENCES promotions(id),
    entry_year      INTEGER NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_students_promotion_id ON students(promotion_id);

CREATE TABLE teachers (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL UNIQUE REFERENCES users(id),
    teacher_code    VARCHAR(32) NOT NULL UNIQUE,
    first_name      VARCHAR(128) NOT NULL,
    last_name       VARCHAR(128) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE courses (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ref         VARCHAR(32)  NOT NULL UNIQUE,
    title       VARCHAR(255) NOT NULL,
    credits     INTEGER      NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE groups (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(64) NOT NULL,
    promotion_id        UUID NOT NULL REFERENCES promotions(id),
    academic_year_id    UUID NOT NULL REFERENCES academic_years(id),
    program_id          UUID REFERENCES programs(id),
    year_level          INTEGER NOT NULL,
    UNIQUE (promotion_id, academic_year_id, code)
);
CREATE INDEX idx_groups_promotion_id ON groups(promotion_id);
CREATE INDEX idx_groups_academic_year_id ON groups(academic_year_id);
CREATE INDEX idx_groups_program_id ON groups(program_id);

CREATE TABLE student_group_history (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id  UUID NOT NULL REFERENCES students(id),
    group_id    UUID NOT NULL REFERENCES groups(id),
    joined_at   TIMESTAMP NOT NULL,
    left_at     TIMESTAMP
);
CREATE INDEX idx_sgh_student_id ON student_group_history(student_id);
CREATE INDEX idx_sgh_group_id ON student_group_history(group_id);
-- A student can only have one active (left_at IS NULL) group membership at a time.
CREATE UNIQUE INDEX uq_sgh_one_active_group_per_student
    ON student_group_history(student_id) WHERE left_at IS NULL;

CREATE TABLE curriculum (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promotion_id    UUID NOT NULL REFERENCES promotions(id),
    course_id       UUID NOT NULL REFERENCES courses(id),
    program_id      UUID REFERENCES programs(id),
    year_level      INTEGER NOT NULL,
    semester        INTEGER NOT NULL,
    required        BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (promotion_id, course_id, program_id)
);
CREATE INDEX idx_curriculum_promotion_id ON curriculum(promotion_id);
CREATE INDEX idx_curriculum_course_id ON curriculum(course_id);
CREATE INDEX idx_curriculum_program_id ON curriculum(program_id);

CREATE TABLE course_offerings (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_id       UUID NOT NULL REFERENCES curriculum(id),
    group_id            UUID NOT NULL REFERENCES groups(id),
    academic_year_id    UUID NOT NULL REFERENCES academic_years(id),
    created_at          TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (curriculum_id, group_id, academic_year_id)
);
CREATE INDEX idx_co_curriculum_id ON course_offerings(curriculum_id);
CREATE INDEX idx_co_group_id ON course_offerings(group_id);
CREATE INDEX idx_co_academic_year_id ON course_offerings(academic_year_id);

CREATE TABLE course_teachers (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_offering_id      UUID NOT NULL REFERENCES course_offerings(id),
    teacher_id              UUID NOT NULL REFERENCES teachers(id),
    assigned_at             TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (course_offering_id, teacher_id)
);
CREATE INDEX idx_ct_course_offering_id ON course_teachers(course_offering_id);
CREATE INDEX idx_ct_teacher_id ON course_teachers(teacher_id);

CREATE TABLE course_enrollments (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id              UUID NOT NULL REFERENCES students(id),
    course_offering_id      UUID NOT NULL REFERENCES course_offerings(id),
    enrolled_at             TIMESTAMP NOT NULL DEFAULT now(),
    status                  VARCHAR(32) NOT NULL,
    UNIQUE (student_id, course_offering_id)
);
CREATE INDEX idx_ce_student_id ON course_enrollments(student_id);
CREATE INDEX idx_ce_course_offering_id ON course_enrollments(course_offering_id);

CREATE TABLE exams (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_offering_id      UUID NOT NULL REFERENCES course_offerings(id),
    title                   VARCHAR(255) NOT NULL,
    exam_type               VARCHAR(32) NOT NULL,
    date_exam               TIMESTAMP NOT NULL,
    coefficient             NUMERIC(4,2) NOT NULL
);
CREATE INDEX idx_exams_course_offering_id ON exams(course_offering_id);

CREATE TABLE exam_grades (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_id     UUID NOT NULL REFERENCES exams(id),
    student_id  UUID NOT NULL REFERENCES students(id),
    score       NUMERIC(5,2) NOT NULL,
    entered_by  UUID NOT NULL REFERENCES teachers(id),
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (exam_id, student_id)
);
CREATE INDEX idx_eg_exam_id ON exam_grades(exam_id);
CREATE INDEX idx_eg_student_id ON exam_grades(student_id);

CREATE TABLE exam_grade_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    exam_grade_id   UUID NOT NULL REFERENCES exam_grades(id),
    old_score       NUMERIC(5,2) NOT NULL,
    new_score       NUMERIC(5,2) NOT NULL,
    reason          VARCHAR(512),
    changed_by      UUID NOT NULL REFERENCES users(id),
    changed_at      TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_egh_exam_grade_id ON exam_grade_history(exam_grade_id);

CREATE TABLE course_results (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    enrollment_id   UUID NOT NULL UNIQUE REFERENCES course_enrollments(id),
    score           NUMERIC(5,2) NOT NULL,
    attempt_type    VARCHAR(32) NOT NULL,
    validated       BOOLEAN NOT NULL DEFAULT FALSE,
    validated_at    TIMESTAMP,
    validated_by    UUID REFERENCES users(id),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE course_result_history (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_result_id    UUID NOT NULL REFERENCES course_results(id),
    old_score           NUMERIC(5,2) NOT NULL,
    new_score           NUMERIC(5,2) NOT NULL,
    reason              VARCHAR(512),
    changed_by          UUID NOT NULL REFERENCES users(id),
    changed_at          TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_crh_course_result_id ON course_result_history(course_result_id);

CREATE TABLE transcripts (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id          UUID NOT NULL REFERENCES students(id),
    academic_year_id    UUID NOT NULL REFERENCES academic_years(id),
    transcript_type      VARCHAR(32) NOT NULL,
    status              VARCHAR(32) NOT NULL,
    s3_object_key       VARCHAR(512),
    file_name           VARCHAR(255),
    generated_at        TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_transcripts_student_id ON transcripts(student_id);
CREATE INDEX idx_transcripts_academic_year_id ON transcripts(academic_year_id);

CREATE TABLE transcript_emails (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transcript_id           UUID NOT NULL REFERENCES transcripts(id),
    recipient_email         VARCHAR(255) NOT NULL,
    status                  VARCHAR(32) NOT NULL,
    provider_message_id     VARCHAR(255),
    sent_at                 TIMESTAMP,
    error_message           TEXT,
    created_at              TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_te_transcript_id ON transcript_emails(transcript_id);

CREATE TABLE graduations (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id          UUID NOT NULL UNIQUE REFERENCES students(id),
    promotion_id        UUID NOT NULL REFERENCES promotions(id),
    program_id          UUID NOT NULL REFERENCES programs(id),
    general_average     NUMERIC(5,2) NOT NULL,
    "rank"              INTEGER,
    diploma_number      VARCHAR(64) UNIQUE,
    status              VARCHAR(32) NOT NULL,
    graduation_date     DATE,
    created_at          TIMESTAMP NOT NULL DEFAULT now()
);
CREATE INDEX idx_graduations_promotion_id ON graduations(promotion_id);
CREATE INDEX idx_graduations_program_id ON graduations(program_id);
