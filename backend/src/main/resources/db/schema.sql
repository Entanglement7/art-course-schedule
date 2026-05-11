SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
CREATE DATABASE IF NOT EXISTS art_schedule DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE art_schedule;

CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE COMMENT '登录名',
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt密码',
    name        VARCHAR(50) NOT NULL COMMENT '显示名',
    role        VARCHAR(20) NOT NULL DEFAULT 'student',
    teacher_id  BIGINT NULL COMMENT '关联教师ID',
    student_id  BIGINT NULL COMMENT '关联学生ID',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS teachers (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL COMMENT '姓名',
    phone       VARCHAR(20) NOT NULL DEFAULT '' COMMENT '联系电话',
    specialty   VARCHAR(50) NOT NULL DEFAULT '' COMMENT '专业',
    join_date   DATE NOT NULL DEFAULT (CURRENT_DATE) COMMENT '入职日期',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS teacher_courses (
    teacher_id  BIGINT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (teacher_id, course_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS students (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    age          INT NOT NULL DEFAULT 0,
    gender       VARCHAR(10) NOT NULL DEFAULT '',
    parent_name  VARCHAR(50) NOT NULL DEFAULT '' COMMENT '家长姓名',
    phone        VARCHAR(20) NOT NULL DEFAULT '',
    enroll_date  DATE NOT NULL DEFAULT (CURRENT_DATE) COMMENT '入学日期',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted      TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS student_courses (
    student_id  BIGINT NOT NULL,
    course_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (student_id, course_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS courses (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL COMMENT '课程名称',
    category    VARCHAR(20) NOT NULL,
    type        VARCHAR(20) NOT NULL,
    duration    INT NOT NULL COMMENT '课时时长(分钟)',
    age_range   VARCHAR(20) NOT NULL COMMENT '适合年龄',
    price       DECIMAL(10,2) NOT NULL COMMENT '单节价格(元)',
    description TEXT COMMENT '课程简介',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS classrooms (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL COMMENT '教室名称',
    type        VARCHAR(30) NOT NULL,
    capacity    INT NOT NULL COMMENT '容纳人数',
    floor       VARCHAR(20) NOT NULL COMMENT '楼层',
    status      VARCHAR(20) NOT NULL DEFAULT '空闲',
    remark      VARCHAR(200) DEFAULT '' COMMENT '备注',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS classroom_equipment (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    classroom_id   BIGINT NOT NULL,
    equipment_name VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS classes (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL COMMENT '班级名称',
    category      VARCHAR(20) NOT NULL,
    course_name   VARCHAR(100) NOT NULL,
    teacher_id    BIGINT NOT NULL,
    student_count INT NOT NULL COMMENT '班级容量',
    schedule      VARCHAR(100) NOT NULL COMMENT '上课时间描述',
    start_date    DATE NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS class_students (
    class_id   BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    PRIMARY KEY (class_id, student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS schedules (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    class_id     BIGINT NOT NULL,
    course_id    BIGINT NOT NULL,
    teacher_id   BIGINT NOT NULL,
    classroom_id BIGINT NOT NULL,
    date         DATE NOT NULL COMMENT '上课日期',
    day_of_week  INT NOT NULL COMMENT '星期(1=周一,7=周日)',
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'scheduled',
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS adjust_requests (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    applicant_id    BIGINT NOT NULL COMMENT '申请人用户ID',
    class_id        BIGINT NOT NULL,
    schedule_id     BIGINT NULL COMMENT '关联课表记录',
    original_time   VARCHAR(50) NOT NULL COMMENT '原上课时间描述',
    new_day_of_week INT NULL COMMENT '新星期',
    new_start_time  TIME NULL,
    new_end_time    TIME NULL,
    reason          VARCHAR(500) NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT '待审核',
    reject_reason   VARCHAR(500) NULL,
    apply_time      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at     DATETIME NULL,
    reviewer_id     BIGINT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
