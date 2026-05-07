-- MySQL dump 10.13  Distrib 8.4.8, for Win64 (x86_64)
--
-- Host: localhost    Database: art_schedule
-- ------------------------------------------------------
-- Server version	8.4.8

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `adjust_requests`
--

DROP TABLE IF EXISTS `adjust_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `adjust_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `applicant_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `schedule_id` bigint DEFAULT NULL,
  `original_time` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `new_day_of_week` int DEFAULT NULL,
  `new_start_time` time DEFAULT NULL,
  `new_end_time` time DEFAULT NULL,
  `reason` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'pending',
  `reject_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `reviewed_at` datetime DEFAULT NULL,
  `reviewer_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `adjust_requests`
--

LOCK TABLES `adjust_requests` WRITE;
/*!40000 ALTER TABLE `adjust_requests` DISABLE KEYS */;
INSERT INTO `adjust_requests` VALUES (1,2,6,NULL,'1121',6,'04:00:00','00:00:00','21212','已通过',NULL,'2026-05-07 18:37:05','2026-05-07 18:37:28',1);
/*!40000 ALTER TABLE `adjust_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `attendance`
--

DROP TABLE IF EXISTS `attendance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `schedule_id` bigint NOT NULL COMMENT '课表ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'present' COMMENT '出勤状态: present-出勤, absent-缺勤, late-迟到, leave-请假',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '备注',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_schedule_student` (`schedule_id`,`student_id`),
  KEY `idx_student` (`student_id`),
  KEY `idx_schedule` (`schedule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance`
--

LOCK TABLES `attendance` WRITE;
/*!40000 ALTER TABLE `attendance` DISABLE KEYS */;
INSERT INTO `attendance` VALUES (1,1,1,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(2,1,2,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(3,1,4,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(4,1,5,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(5,1,6,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(6,1,7,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(7,1,8,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(8,3,3,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(9,3,9,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30'),(10,3,10,'present','','2026-05-07 17:36:30','2026-05-07 17:36:30');
/*!40000 ALTER TABLE `attendance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class_students`
--

DROP TABLE IF EXISTS `class_students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `class_students` (
  `class_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  PRIMARY KEY (`class_id`,`student_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class_students`
--

LOCK TABLES `class_students` WRITE;
/*!40000 ALTER TABLE `class_students` DISABLE KEYS */;
INSERT INTO `class_students` VALUES (1,1),(1,2),(1,4),(1,5),(1,6),(1,7),(1,8),(2,3),(2,9),(2,10),(3,4),(3,5),(3,7),(3,10),(4,4),(4,5),(4,7),(4,10),(5,1),(5,5),(5,6),(5,7),(6,3),(6,4),(6,9);
/*!40000 ALTER TABLE `class_students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classes`
--

DROP TABLE IF EXISTS `classes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `course_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `teacher_id` bigint NOT NULL,
  `student_count` int NOT NULL,
  `schedule` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classes`
--

LOCK TABLES `classes` WRITE;
/*!40000 ALTER TABLE `classes` DISABLE KEYS */;
INSERT INTO `classes` VALUES (1,'钢琴基础A班','音乐','钢琴基础',1,10,'周一、周三 09:00-10:00','2026-05-01','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(2,'钢琴基础B班','音乐','钢琴基础',1,8,'周二、周四 10:00-11:00','2026-05-01','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(3,'测试排课','音乐','钢琴基础',1,4,'智能排课生成','2026-05-07','2026-05-07 18:12:45','2026-05-07 18:12:45',0),(4,'测试排课','音乐','钢琴基础',1,4,'智能排课生成','2026-05-07','2026-05-07 18:12:57','2026-05-07 18:12:57',0),(5,'测试排课','音乐','钢琴基础',1,4,'智能排课生成','2026-05-07','2026-05-07 18:13:34','2026-05-07 18:13:34',0),(6,'测试排课','音乐','钢琴基础',1,3,'智能排课生成','2026-05-07','2026-05-07 18:17:47','2026-05-07 18:50:53',1);
/*!40000 ALTER TABLE `classes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classroom_equipment`
--

DROP TABLE IF EXISTS `classroom_equipment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classroom_equipment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `classroom_id` bigint NOT NULL,
  `equipment_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classroom_equipment`
--

LOCK TABLES `classroom_equipment` WRITE;
/*!40000 ALTER TABLE `classroom_equipment` DISABLE KEYS */;
/*!40000 ALTER TABLE `classroom_equipment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classrooms`
--

DROP TABLE IF EXISTS `classrooms`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classrooms` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `capacity` int NOT NULL,
  `floor` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'available',
  `remark` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classrooms`
--

LOCK TABLES `classrooms` WRITE;
/*!40000 ALTER TABLE `classrooms` DISABLE KEYS */;
INSERT INTO `classrooms` VALUES (1,'音乐教室1','音乐教室',10,'3楼','使用中','','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(2,'舞蹈教室1','舞蹈教室',20,'2楼','空闲','','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(3,'美术教室1','美术教室',15,'4楼','空闲','','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(4,'音乐教室2','音乐教室',8,'3楼','空闲','','2026-05-07 19:49:17','2026-05-07 19:51:01',0),(5,'音乐教室3','音乐教室',1,'3楼','空闲','','2026-05-07 19:49:17','2026-05-07 19:51:01',0),(6,'舞蹈教室2','舞蹈教室',15,'2楼','空闲','','2026-05-07 19:49:17','2026-05-07 19:51:01',0),(7,'美术教室2','美术教室',12,'4楼','空闲','','2026-05-07 19:49:17','2026-05-07 19:51:01',0),(8,'多功能教室','多功能教室',30,'1楼','空闲','','2026-05-07 19:49:17','2026-05-07 19:51:01',0);
/*!40000 ALTER TABLE `classrooms` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `category` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `duration` int NOT NULL,
  `age_range` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses` DISABLE KEYS */;
INSERT INTO `courses` VALUES (1,'钢琴基础','音乐','小组课',60,'6-12岁',200.00,'适合零基础儿童，学习基础乐理和钢琴演奏技法','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(2,'芭蕾舞基础','舞蹈','小组课',90,'5-10岁',220.00,'芭蕾基础形体和基本步伐训练','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(3,'素描基础','美术','小组课',120,'8-16岁',200.00,'线条、明暗、构图基础素描训练','2026-05-07 17:18:02','2026-05-07 17:18:02',0);
/*!40000 ALTER TABLE `courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `schedules`
--

DROP TABLE IF EXISTS `schedules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `schedules` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `teacher_id` bigint NOT NULL,
  `classroom_id` bigint NOT NULL,
  `date` date NOT NULL,
  `day_of_week` int NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'scheduled',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `schedules`
--

LOCK TABLES `schedules` WRITE;
/*!40000 ALTER TABLE `schedules` DISABLE KEYS */;
INSERT INTO `schedules` VALUES (1,1,1,1,1,'2026-05-05',1,'09:00:00','10:00:00','completed','2026-05-07 17:18:02','2026-05-07 17:18:02'),(2,1,1,1,1,'2026-05-12',1,'09:00:00','10:00:00','scheduled','2026-05-07 17:18:02','2026-05-07 17:18:02'),(3,2,1,1,1,'2026-05-06',2,'10:00:00','11:00:00','completed','2026-05-07 17:18:02','2026-05-07 17:18:02'),(4,2,1,1,1,'2026-05-13',2,'10:00:00','11:00:00','scheduled','2026-05-07 17:18:02','2026-05-07 17:18:02'),(5,1,1,1,1,'2026-05-07',3,'09:00:00','10:00:00','scheduled','2026-05-07 17:18:02','2026-05-07 17:18:02'),(6,2,1,1,1,'2026-05-08',4,'10:00:00','11:00:00','scheduled','2026-05-07 17:18:02','2026-05-07 17:18:02'),(7,1,1,1,1,'2026-05-09',5,'14:00:00','15:00:00','scheduled','2026-05-07 17:18:02','2026-05-07 17:18:02'),(8,3,1,1,1,'2026-05-11',1,'10:00:00','11:00:00','scheduled','2026-05-07 18:12:45','2026-05-07 18:12:45'),(9,4,1,1,1,'2026-05-12',2,'09:00:00','10:00:00','scheduled','2026-05-07 18:12:57','2026-05-07 18:12:57'),(10,4,1,1,1,'2026-05-19',2,'09:00:00','10:00:00','scheduled','2026-05-07 18:12:57','2026-05-07 18:12:57'),(11,4,1,1,1,'2026-05-26',2,'09:00:00','10:00:00','scheduled','2026-05-07 18:12:57','2026-05-07 18:12:57'),(12,4,1,1,1,'2026-06-02',2,'09:00:00','10:00:00','scheduled','2026-05-07 18:12:57','2026-05-07 18:12:57'),(13,5,1,1,1,'2026-05-13',3,'10:00:00','11:00:00','scheduled','2026-05-07 18:13:34','2026-05-07 18:13:34'),(14,5,1,1,1,'2026-05-20',3,'10:00:00','11:00:00','scheduled','2026-05-07 18:13:34','2026-05-07 18:13:34'),(15,5,1,1,1,'2026-05-27',3,'10:00:00','11:00:00','scheduled','2026-05-07 18:13:34','2026-05-07 18:13:34'),(16,5,1,1,1,'2026-06-03',3,'10:00:00','11:00:00','scheduled','2026-05-07 18:13:34','2026-05-07 18:13:34'),(17,6,1,1,1,'2026-05-07',4,'09:00:00','10:00:00','scheduled','2026-05-07 18:17:47','2026-05-07 18:17:47'),(18,2,1,1,1,'2026-05-11',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(19,2,1,1,1,'2026-05-18',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(20,2,1,1,1,'2026-05-25',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(21,2,1,1,1,'2026-06-01',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(22,2,1,1,1,'2026-06-08',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(23,2,1,1,1,'2026-06-15',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(24,2,1,1,1,'2026-06-22',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(25,2,1,1,1,'2026-06-29',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(26,2,1,1,1,'2026-07-06',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(27,2,1,1,1,'2026-07-13',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(28,2,1,1,1,'2026-07-20',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(29,2,1,1,1,'2026-07-27',1,'13:00:00','14:00:00','scheduled','2026-05-07 19:43:20','2026-05-07 19:43:20'),(30,2,1,1,8,'2026-05-11',1,'00:00:00','01:00:00','scheduled','2026-05-07 19:55:18','2026-05-07 19:55:18'),(31,2,1,1,8,'2026-05-11',1,'14:00:00','15:00:00','scheduled','2026-05-07 19:55:59','2026-05-07 19:55:59');
/*!40000 ALTER TABLE `schedules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_courses`
--

DROP TABLE IF EXISTS `student_courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_courses` (
  `student_id` bigint NOT NULL,
  `course_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`student_id`,`course_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_courses`
--

LOCK TABLES `student_courses` WRITE;
/*!40000 ALTER TABLE `student_courses` DISABLE KEYS */;
INSERT INTO `student_courses` VALUES (1,'钢琴基础'),(2,'芭蕾舞初级'),(2,'钢琴基础'),(3,'水彩入门'),(3,'素描基础'),(4,'声乐基础'),(5,'中国舞初级'),(5,'芭蕾舞初级'),(6,'国画基础'),(6,'素描提高'),(7,'钢琴进阶'),(8,'素描基础'),(8,'街舞入门'),(9,'声乐基础'),(9,'钢琴基础'),(10,'中国舞初级'),(10,'芭蕾舞中级');
/*!40000 ALTER TABLE `student_courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `age` int NOT NULL,
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `enroll_date` date NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'小明',8,'男','明爸爸','13900001001','2023-09-01','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(2,'小红',7,'女','红妈妈','13900001002','2023-09-01','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(3,'小刚',9,'男','刚爸爸','13900001003','2023-09-01','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(4,'小丽',7,'女','丽妈妈','13900001004','2023-09-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0),(5,'小芳',8,'女','芳妈妈','13900001005','2023-10-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0),(6,'小华',10,'男','华爸爸','13900001006','2023-10-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0),(7,'小美',7,'女','美妈妈','13900001007','2023-10-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0),(8,'小强',9,'男','强爸爸','13900001008','2023-11-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0),(9,'小军',8,'男','军爸爸','13900001009','2023-11-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0),(10,'小兰',9,'女','兰妈妈','13900001010','2023-11-01','2026-05-07 17:22:26','2026-05-07 17:22:26',0);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacher_courses`
--

DROP TABLE IF EXISTS `teacher_courses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacher_courses` (
  `teacher_id` bigint NOT NULL,
  `course_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`teacher_id`,`course_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacher_courses`
--

LOCK TABLES `teacher_courses` WRITE;
/*!40000 ALTER TABLE `teacher_courses` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacher_courses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teachers`
--

DROP TABLE IF EXISTS `teachers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teachers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `specialty` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `join_date` date NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teachers`
--

LOCK TABLES `teachers` WRITE;
/*!40000 ALTER TABLE `teachers` DISABLE KEYS */;
INSERT INTO `teachers` VALUES (1,'张老师','13800001001','钢琴','2023-01-15','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(2,'李老师','13800001002','芭蕾舞','2023-03-20','2026-05-07 17:18:02','2026-05-07 17:18:02',0),(3,'王老师','13800001003','素描','2023-05-10','2026-05-07 17:18:02','2026-05-07 17:18:02',0);
/*!40000 ALTER TABLE `teachers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'student',
  `teacher_id` bigint DEFAULT NULL,
  `student_id` bigint DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','管理员','admin',NULL,NULL,'2026-05-07 17:18:02'),(2,'teacher1','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','张老师','teacher',1,NULL,'2026-05-07 17:18:02'),(3,'teacher2','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','李老师','teacher',2,NULL,'2026-05-07 17:18:02'),(4,'teacher3','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','王老师','teacher',3,NULL,'2026-05-07 17:18:02'),(5,'student1','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小明','student',NULL,1,'2026-05-07 17:18:02'),(6,'student2','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小红','student',NULL,2,'2026-05-07 17:18:02'),(7,'student3','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小刚','student',NULL,3,'2026-05-07 17:18:02'),(8,'student4','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小丽','student',NULL,4,'2026-05-07 17:22:26'),(9,'student5','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小芳','student',NULL,5,'2026-05-07 17:22:26'),(10,'student6','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小华','student',NULL,6,'2026-05-07 17:22:26'),(11,'student7','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小美','student',NULL,7,'2026-05-07 17:22:26'),(12,'student8','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小强','student',NULL,8,'2026-05-07 17:22:26'),(13,'student9','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小军','student',NULL,9,'2026-05-07 17:22:26'),(14,'student10','$2a$10$BQu61hVHGVn6A8yl93YjS..Ksm8ovz/MDhUzl3GbO5j6.3ZHZuWeq','小兰','student',NULL,10,'2026-05-07 17:22:26');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'art_schedule'
--

--
-- Dumping routines for database 'art_schedule'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-05-07 20:38:27
