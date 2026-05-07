SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

USE art_schedule;

-- 清空现有数据
DELETE FROM schedules;
DELETE FROM class_students;
DELETE FROM classes;
DELETE FROM classroom_equipment;
DELETE FROM classrooms;
DELETE FROM student_courses;
DELETE FROM students;
DELETE FROM teacher_courses;
DELETE FROM teachers;
DELETE FROM courses;
DELETE FROM adjust_requests;
DELETE FROM users;

-- 重置自增ID
ALTER TABLE teachers AUTO_INCREMENT = 1;
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;
ALTER TABLE classrooms AUTO_INCREMENT = 1;
ALTER TABLE classes AUTO_INCREMENT = 1;
ALTER TABLE schedules AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 1;

-- 插入教师数据
INSERT INTO teachers (id, name, phone, specialty, join_date) VALUES
(1, '张老师', '13800001001', '钢琴', '2023-01-15'),
(2, '李老师', '13800001002', '芭蕾舞', '2023-03-20'),
(3, '王老师', '13800001003', '素描', '2023-05-10');

-- 插入学生数据
INSERT INTO students (id, name, age, gender, parent_name, phone, enroll_date) VALUES
(1, '小明', 8, '男', '明爸爸', '13900001001', '2023-09-01'),
(2, '小红', 7, '女', '红妈妈', '13900001002', '2023-09-01'),
(3, '小刚', 9, '男', '刚爸爸', '13900001003', '2023-09-01');

-- 插入用户数据（所有密码都是 test123）
INSERT INTO users (id, username, password, name, role, teacher_id, student_id) VALUES
(1, 'admin', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '管理员', 'admin', NULL, NULL),
(2, 'teacher1', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '张老师', 'teacher', 1, NULL),
(3, 'teacher2', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '李老师', 'teacher', 2, NULL),
(4, 'teacher3', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '王老师', 'teacher', 3, NULL),
(5, 'student1', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '小明', 'student', NULL, 1),
(6, 'student2', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '小红', 'student', NULL, 2),
(7, 'student3', '$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq', '小刚', 'student', NULL, 3);

-- 插入课程数据
INSERT INTO courses (id, name, category, type, duration, age_range, price, description) VALUES
(1, '钢琴基础', '音乐', '小组课', 60, '6-12岁', 200.00, '适合零基础儿童，学习基础乐理和钢琴演奏技法'),
(2, '芭蕾舞基础', '舞蹈', '小组课', 90, '5-10岁', 220.00, '芭蕾基础形体和基本步伐训练'),
(3, '素描基础', '美术', '小组课', 120, '8-16岁', 200.00, '线条、明暗、构图基础素描训练');

-- 插入教室数据
INSERT INTO classrooms (id, name, type, capacity, floor, status) VALUES
(1, '音乐教室1', '音乐教室', 10, '3楼', '使用中'),
(2, '舞蹈教室1', '舞蹈教室', 20, '2楼', '空闲'),
(3, '美术教室1', '美术教室', 15, '4楼', '空闲');

-- 插入班级数据（张老师的钢琴班）
INSERT INTO classes (id, name, category, course_name, teacher_id, student_count, schedule, start_date) VALUES
(1, '钢琴基础A班', '音乐', '钢琴基础', 1, 10, '周一、周三 09:00-10:00', '2026-05-01'),
(2, '钢琴基础B班', '音乐', '钢琴基础', 1, 8, '周二、周四 10:00-11:00', '2026-05-01');

-- 给班级添加学生
INSERT INTO class_students (class_id, student_id) VALUES
(1, 1), (1, 2),
(2, 3);

-- 创建本周的课表（2026-05-07 是周三）
INSERT INTO schedules (class_id, course_id, teacher_id, classroom_id, date, day_of_week, start_time, end_time, status) VALUES
-- 周一的课
(1, 1, 1, 1, '2026-05-05', 1, '09:00:00', '10:00:00', 'completed'),
(1, 1, 1, 1, '2026-05-12', 1, '09:00:00', '10:00:00', 'scheduled'),
-- 周二的课
(2, 1, 1, 1, '2026-05-06', 2, '10:00:00', '11:00:00', 'completed'),
(2, 1, 1, 1, '2026-05-13', 2, '10:00:00', '11:00:00', 'scheduled'),
-- 周三的课（今天）
(1, 1, 1, 1, '2026-05-07', 3, '09:00:00', '10:00:00', 'scheduled'),
-- 周四的课
(2, 1, 1, 1, '2026-05-08', 4, '10:00:00', '11:00:00', 'scheduled'),
-- 周五的课
(1, 1, 1, 1, '2026-05-09', 5, '14:00:00', '15:00:00', 'scheduled');
