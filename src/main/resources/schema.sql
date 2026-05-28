DROP TABLE IF EXISTS sert CASCADE;
DROP TABLE IF EXISTS lesson_progress CASCADE;
DROP TABLE IF EXISTS course_progress CASCADE;
DROP TABLE IF EXISTS test CASCADE;
DROP TABLE IF EXISTS lesson CASCADE;
DROP TABLE IF EXISTS module CASCADE;
DROP TABLE IF EXISTS course CASCADE;
DROP TABLE IF EXISTS teacher CASCADE;
DROP TABLE IF EXISTS student CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id                INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(100) NOT NULL,
    password_hash     TEXT NOT NULL,
    registration_date TIMESTAMPTZ NOT NULL,
    CONSTRAINT PK_users PRIMARY KEY (id)
);

CREATE TABLE teacher (
    teacher_id        INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id           INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(100) NOT NULL,
    password_hash     TEXT NOT NULL,
    registration_date TIMESTAMPTZ NOT NULL,
    CONSTRAINT PK_teacher PRIMARY KEY (teacher_id),
    CONSTRAINT FK_teacher_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE student (
    student_id        INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id           INT NOT NULL,
    zach_id           INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    email             VARCHAR(100) NOT NULL,
    password_hash     TEXT NOT NULL,
    registration_date TIMESTAMPTZ NOT NULL,
    CONSTRAINT PK_student PRIMARY KEY (student_id),
    CONSTRAINT FK_student_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE course (
    course_id         INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    teacher_id        INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    description       TEXT NOT NULL,
    duration          VARCHAR(100) NULL,
    CONSTRAINT PK_course PRIMARY KEY (course_id),
    CONSTRAINT FK_course_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(teacher_id)
);

CREATE TABLE module (
    module_id         INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    course_id         INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    description       TEXT NULL,
    CONSTRAINT PK_module PRIMARY KEY (module_id),
    CONSTRAINT FK_module_course FOREIGN KEY (course_id) REFERENCES course(course_id)
);

CREATE TABLE lesson (
    lesson_id         INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    module_id         INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    description       TEXT NULL,
    CONSTRAINT PK_lesson PRIMARY KEY (lesson_id),
    CONSTRAINT FK_lesson_module FOREIGN KEY (module_id) REFERENCES module(module_id)
);

CREATE TABLE test (
    test_id           INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    lesson_id         INT NOT NULL,
    name              VARCHAR(100) NOT NULL,
    CONSTRAINT PK_test PRIMARY KEY (test_id),
    CONSTRAINT FK_test_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(lesson_id)
);

CREATE TABLE course_progress (
    progress_id       INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    course_id         INT NOT NULL,
    user_id           INT NOT NULL,
    progress          VARCHAR(100) NOT NULL,
    CONSTRAINT PK_progress_id_c PRIMARY KEY (progress_id),
    CONSTRAINT FK_progress_course FOREIGN KEY (course_id) REFERENCES course(course_id),
    CONSTRAINT FK_progress_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE lesson_progress (
    progress_id       INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    course_progress_id INT NOT NULL,
    lesson_id         INT NOT NULL,
    user_id           INT NOT NULL,
    progress          VARCHAR(100) NOT NULL,
    CONSTRAINT PK_progress_id_l PRIMARY KEY (progress_id),
    CONSTRAINT FK_progress_lesson FOREIGN KEY (lesson_id) REFERENCES lesson(lesson_id),
    CONSTRAINT FK_course_progress_id FOREIGN KEY (course_progress_id) REFERENCES course_progress(progress_id),
    CONSTRAINT FK_progress_users FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE sert (
    sert_id           INT GENERATED ALWAYS AS IDENTITY NOT NULL,
    course_id         INT NOT NULL,
    user_id           INT NOT NULL,
    CONSTRAINT PK_sert PRIMARY KEY (sert_id),
    CONSTRAINT FK_sert_course FOREIGN KEY (course_id) REFERENCES course(course_id),
    CONSTRAINT FK_sert_users FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE INDEX idx_course_progress_course ON course_progress(course_id);
CREATE INDEX idx_course_progress_user ON course_progress(user_id);
CREATE INDEX idx_lesson_progress_course_progress ON lesson_progress(course_progress_id);
CREATE INDEX idx_sert_course ON sert(course_id);
CREATE INDEX idx_sert_user ON sert(user_id);