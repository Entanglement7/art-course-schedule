SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- Drop and recreate database
DROP DATABASE IF EXISTS art_schedule;
CREATE DATABASE art_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE art_schedule;

-- Users table
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    name        VARCHAR(50) NOT NULL,
    role        VARCHAR(20) NOT NULL DEFAULT 'student',
    teacher_id  BIGINT NULL,
    student_id  BIGINT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Teachers table
CREATE TABLE teachers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    phone       VARCHAR(20) NOT NULL,
    specialty   VARCHAR(50) NOT NULL,
    join_date   DATE NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE teacher_courses (
    teacher_id  BIGINT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (teacher_id, course_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Students table
CREATE TABLE students (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    age          INT NOT NULL,
    gender       VARCHAR(10) NOT NULL,
    parent_name  VARCHAR(50) NOT NULL,
    phone        VARCHAR(20) NOT NULL,
    enroll_date  DATE NOT NULL,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted      TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE student_courses (
    student_id  BIGINT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (student_id, course_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Courses table
CREATE TABLE courses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    category    VARCHAR(20) NOT NULL,
    type        VARCHAR(20) NOT NULL,
    duration    INT NOT NULL,
    age_range   VARCHAR(20) NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    description TEXT,
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Classrooms table
CREATE TABLE classrooms (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    type        VARCHAR(30) NOT NULL,
    capacity    INT NOT NULL,
    floor       VARCHAR(20) NOT NULL,
    status      VARCHAR(20) NOT NULL DEFAULT 'available',
    remark      VARCHAR(200) DEFAULT '',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE classroom_equipment (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    classroom_id   BIGINT NOT NULL,
    equipment_name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Classes table
CREATE TABLE classes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    category      VARCHAR(20) NOT NULL,
    course_name   VARCHAR(100) NOT NULL,
    teacher_id    BIGINT NOT NULL,
    student_count INT NOT NULL,
    schedule      VARCHAR(100) NOT NULL,
    start_date    DATE NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE class_students (
    class_id   BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    PRIMARY KEY (class_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Schedules table
CREATE TABLE schedules (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id     BIGINT NOT NULL,
    course_id    BIGINT NOT NULL,
    teacher_id   BIGINT NOT NULL,
    classroom_id BIGINT NOT NULL,
    date         DATE NOT NULL,
    day_of_week  INT NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'scheduled',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Adjust requests table
CREATE TABLE adjust_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id    BIGINT NOT NULL,
    class_id        BIGINT NOT NULL,
    schedule_id     BIGINT NULL,
    original_time   VARCHAR(50) NOT NULL,
    new_day_of_week INT NULL,
    new_start_time  TIME NULL,
    new_end_time    TIME NULL,
    reason          VARCHAR(500) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'pending',
    reject_reason   VARCHAR(500) NULL,
    apply_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at     DATETIME NULL,
    reviewer_id     BIGINT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert test data
-- Password for all accounts: admin123
-- BCrypt hash: $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.

-- Insert teachers
INSERT INTO teachers (id, name, phone, specialty, join_date) VALUES
(1, 'Teacher Zhang', '13800001001', 'Piano', '2023-01-15'),
(2, 'Teacher Li', '13800001002', 'Ballet', '2023-03-20'),
(3, 'Teacher Wang', '13800001003', 'Drawing', '2023-05-10');

-- Insert students
INSERT INTO students (id, name, age, gender, parent_name, phone, enroll_date) VALUES
(1, 'Student A', 8, 'Male', 'Parent A', '13900001001', '2023-09-01'),
(2, 'Student B', 7, 'Female', 'Parent B', '13900001002', '2023-09-01'),
(3, 'Student C', 9, 'Male', 'Parent C', '13900001003', '2023-09-01');

-- Insert users (all passwords are: admin123)
INSERT INTO users (id, username, password, name, role, teacher_id, student_id) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Administrator', 'admin', NULL, NULL),
(2, 'teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Teacher Zhang', 'teacher', 1, NULL),
(3, 'teacher2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Teacher Li', 'teacher', 2, NULL),
(4, 'teacher3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Teacher Wang', 'teacher', 3, NULL),
(5, 'student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Student A', 'student', NULL, 1),
(6, 'student2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Student B', 'student', NULL, 2),
(7, 'student3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU4SVZJ.', 'Student C', 'student', NULL, 3);

-- Insert courses
INSERT INTO courses (id, name, category, type, duration, age_range, price, description) VALUES
(1, 'Piano Basic', 'Music', 'Group', 60, '6-12', 200.00, 'Basic piano course for beginners'),
(2, 'Ballet Basic', 'Dance', 'Group', 90, '5-10', 220.00, 'Basic ballet training'),
(3, 'Drawing Basic', 'Art', 'Group', 120, '8-16', 200.00, 'Basic drawing skills');

-- Insert classrooms
INSERT INTO classrooms (id, name, type, capacity, floor, status) VALUES
(1, 'Music Room 1', 'Music', 10, '3F', 'available'),
(2, 'Dance Room 1', 'Dance', 20, '2F', 'available'),
(3, 'Art Room 1', 'Art', 15, '4F', 'available');
