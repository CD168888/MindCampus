-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ry-vue
-- ------------------------------------------------------
-- Server version	8.0.34

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
-- Table structure for table `ai_chat_message`
--

DROP TABLE IF EXISTS `ai_chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_chat_message` (
  `message_id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `session_id` bigint DEFAULT NULL COMMENT '会话ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `message_type` tinyint(1) DEFAULT NULL COMMENT '消息类型（0 AI消息 1 用户消息）',
  `content` text COMMENT '消息内容',
  `file_urls` text COMMENT '附件URL列表',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`message_id`)
) ENGINE=InnoDB AUTO_INCREMENT=406 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI 聊天消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_chat_message`
--

LOCK TABLES `ai_chat_message` WRITE;
/*!40000 ALTER TABLE `ai_chat_message` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_chat_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_chat_session`
--

DROP TABLE IF EXISTS `ai_chat_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_chat_session` (
  `session_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `session_name` varchar(255) DEFAULT NULL COMMENT '会话名称',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI 聊天会话表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_chat_session`
--

LOCK TABLES `ai_chat_session` WRITE;
/*!40000 ALTER TABLE `ai_chat_session` DISABLE KEYS */;
INSERT INTO `ai_chat_session` VALUES (101,100,'图片查看受限','xiaodu','2026-03-19 09:40:28','xiaodu','2026-03-19 09:40:29',NULL),(102,100,'面试失利共情','xiaodu','2026-03-19 14:18:44','xiaodu','2026-03-19 14:18:44',NULL),(103,100,'今天面试没通过','xiaodu','2026-03-21 10:39:03','xiaodu','2026-03-21 10:39:03',NULL),(104,100,'今天面试没通过','xiaodu','2026-03-21 10:39:24','xiaodu','2026-03-21 10:39:24',NULL),(105,100,'我面试失败了','xiaodu','2026-03-21 11:14:17','xiaodu','2026-03-21 11:14:17',NULL),(106,100,'压力倾诉陪伴','xiaodu','2026-03-26 00:21:23','xiaodu','2026-03-26 00:21:23',NULL),(107,100,'心理陪伴伙伴','xiaodu','2026-04-09 23:54:12','xiaodu','2026-04-09 23:54:12',NULL),(108,100,'心理陪伴伙伴','xiaodu','2026-04-10 21:53:33','xiaodu','2026-04-10 21:53:33',NULL),(109,100,'基本信息查询','xiaodu','2026-04-10 21:55:28','xiaodu','2026-04-10 21:55:28',NULL),(110,100,'心理状态查询','xiaodu','2026-04-10 21:56:25','xiaodu','2026-04-10 21:56:25',NULL),(117,100,'评估报告查询','xiaodu','2026-04-10 23:22:43','xiaodu','2026-04-10 23:22:43',NULL),(118,100,'评估报告查询','xiaodu','2026-04-10 23:47:06','xiaodu','2026-04-10 23:47:06',NULL),(119,100,'评估报告查询','xiaodu','2026-04-10 23:59:00','xiaodu','2026-04-10 23:59:00',NULL),(120,100,'评估报告查询','xiaodu','2026-04-11 00:06:34','xiaodu','2026-04-11 00:06:34',NULL),(121,100,'评估报告查询','xiaodu','2026-04-11 01:22:33','xiaodu','2026-04-11 01:22:33',NULL),(122,100,'心理状态评估','xiaodu','2026-04-11 01:23:34','xiaodu','2026-04-11 01:23:34',NULL),(123,100,'近期状态查询','xiaodu','2026-04-11 17:36:17','xiaodu','2026-04-11 17:36:17',NULL),(124,100,'搜索功能说明','xiaodu','2026-04-12 01:26:27','xiaodu','2026-04-12 01:26:27',NULL),(125,100,'拖延症研究','xiaodu','2026-04-12 01:29:47','xiaodu','2026-04-12 01:29:47',NULL),(126,100,'模型说明','xiaodu','2026-04-12 01:30:56','xiaodu','2026-04-12 01:30:56',NULL),(127,100,'研究时间说明','xiaodu','2026-04-12 01:31:53','xiaodu','2026-04-12 01:31:53',NULL),(128,100,'拖延症研究','xiaodu','2026-04-12 01:45:58','xiaodu','2026-04-12 01:45:58',NULL),(129,100,'拖延症研究','xiaodu','2026-04-12 01:46:57','xiaodu','2026-04-12 01:46:57',NULL),(130,100,'正念科学依据','xiaodu','2026-04-12 01:56:21','xiaodu','2026-04-12 01:56:21',NULL),(131,100,'正念最新依据','xiaodu','2026-04-12 01:56:50','xiaodu','2026-04-12 01:56:50',NULL),(132,100,'拖延症新解','xiaodu','2026-04-12 01:57:24','xiaodu','2026-04-12 01:57:24',NULL),(133,100,'拖延症查询','xiaodu','2026-04-12 01:57:55','xiaodu','2026-04-12 01:57:55',NULL),(134,100,'拖延症查询','xiaodu','2026-04-12 02:01:26','xiaodu','2026-04-12 02:01:26',NULL),(135,100,'新闻查询','xiaodu','2026-04-12 02:02:02','xiaodu','2026-04-12 02:02:02',NULL),(147,100,'图片无法识别','xiaodu','2026-04-18 03:08:36','xiaodu','2026-04-18 03:08:36',NULL),(149,100,'牙齿图片咨询','xiaodu','2026-04-18 03:13:20','xiaodu','2026-04-18 03:13:20',NULL),(150,100,'牙齿图片咨询','xiaodu','2026-04-18 03:13:59','xiaodu','2026-04-18 03:13:59',NULL),(151,100,'我这个图片是怎么啦？','xiaodu','2026-04-18 03:18:36','xiaodu','2026-04-18 03:18:36',NULL),(152,100,'图片无法查看','xiaodu','2026-04-18 03:23:01','xiaodu','2026-04-18 03:23:01',NULL),(153,100,'图片问题分析','xiaodu','2026-04-18 03:54:06','xiaodu','2026-04-18 03:54:06',NULL);
/*!40000 ALTER TABLE `ai_chat_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_like`
--

DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_like`
--

LOCK TABLES `article_like` WRITE;
/*!40000 ALTER TABLE `article_like` DISABLE KEYS */;
INSERT INTO `article_like` VALUES (4,8,100,'2026-03-18 19:48:30');
/*!40000 ALTER TABLE `article_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banner`
--

DROP TABLE IF EXISTS `banner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banner` (
  `banner_id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) NOT NULL COMMENT '轮播标题',
  `image_url` varchar(255) NOT NULL COMMENT '轮播图链接',
  `link_url` varchar(255) DEFAULT NULL COMMENT '跳转链接（点击后访问的页面）',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序（越小越靠前）',
  `start_time` datetime DEFAULT NULL COMMENT '展示开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '展示结束时间',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1下架）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='轮播图表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banner`
--

LOCK TABLES `banner` WRITE;
/*!40000 ALTER TABLE `banner` DISABLE KEYS */;
INSERT INTO `banner` VALUES (1,'轮播图002','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/轮播图02_1770100406121_3905.jpg','12',1,'2025-10-01 18:00:40','2025-12-30 00:00:00','0','','2025-11-09 17:39:54','','2026-03-13 13:38:02','无'),(2,'轮播图001','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/轮播图01_1770100378983_6100.jpg','1123123',1,'2025-10-01 18:00:29','2026-11-30 00:00:00','0','','2025-11-09 17:43:08','','2026-03-13 13:37:37','无'),(3,'轮播图03','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/轮播图03_1770100427940_9296.jpg',NULL,0,'2025-03-01 00:00:00','2027-03-31 00:00:00','0','','2026-02-03 14:34:00','','2026-03-13 13:37:52',NULL);
/*!40000 ALTER TABLE `banner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_comment`
--

DROP TABLE IF EXISTS `community_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_comment` (
  `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint DEFAULT NULL COMMENT '帖子ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `like_count` bigint DEFAULT '0' COMMENT '点赞数',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID（用于楼中楼回复）',
  `reply_to_user_id` bigint DEFAULT NULL COMMENT '回复的用户ID',
  `reply_to_user_name` varchar(100) DEFAULT NULL COMMENT '回复的用户名',
  `content` varchar(500) DEFAULT NULL COMMENT '评论内容',
  `is_anonymous` char(1) DEFAULT '1' COMMENT '是否匿名（0否 1是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1屏蔽）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_comment`
--

LOCK TABLES `community_comment` WRITE;
/*!40000 ALTER TABLE `community_comment` DISABLE KEYS */;
INSERT INTO `community_comment` VALUES (20,22,100,0,NULL,NULL,NULL,'今天面试也通过啦！','0','0','','2026-03-21 09:36:45','',NULL,NULL),(21,23,100,0,NULL,NULL,NULL,'1','0','0','','2026-03-21 10:38:39','',NULL,NULL);
/*!40000 ALTER TABLE `community_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_post`
--

DROP TABLE IF EXISTS `community_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `title` varchar(200) DEFAULT NULL COMMENT '帖子标题',
  `content` text COMMENT '帖子内容',
  `images` text COMMENT '图片列表（逗号分隔）',
  `like_count` bigint DEFAULT '0' COMMENT '点赞数',
  `comment_count` bigint DEFAULT '0' COMMENT '评论数',
  `view_count` bigint DEFAULT '0' COMMENT '浏览数',
  `is_anonymous` char(1) DEFAULT '1' COMMENT '是否匿名（0否 1是）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1屏蔽）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='社区帖子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_post`
--

LOCK TABLES `community_post` WRITE;
/*!40000 ALTER TABLE `community_post` DISABLE KEYS */;
INSERT INTO `community_post` VALUES (22,100,'天气好晴朗','今天天气不错呀','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/下巴还挂着一滴不小心沾到的水_1_一只叫葡萄的猫_来自小红书网页版_1773486823957_8295.jpg,https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/下巴还挂着一滴不小心沾到的水_3_一只叫葡萄的猫_来自小红书网页版_1773486837623_0029.jpg',2,1,4,'0','0','','2026-03-14 19:13:59','',NULL,NULL),(23,100,'11','11','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/883fce96ee01728ad9ca63826abc339_1774060707363_4255.png',1,1,3,'1','0','','2026-03-21 10:38:30','',NULL,NULL);
/*!40000 ALTER TABLE `community_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `community_post_like`
--

DROP TABLE IF EXISTS `community_post_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `community_post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`,`user_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='社区帖子点赞记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `community_post_like`
--

LOCK TABLES `community_post_like` WRITE;
/*!40000 ALTER TABLE `community_post_like` DISABLE KEYS */;
INSERT INTO `community_post_like` VALUES (11,22,100,'2026-03-21 09:36:49'),(12,23,100,'2026-03-21 10:38:36');
/*!40000 ALTER TABLE `community_post_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `counselor_dept`
--

DROP TABLE IF EXISTS `counselor_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `counselor_dept` (
  `dept_counselor_id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `counselor_id` bigint NOT NULL COMMENT '辅导员ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dept_counselor_id`),
  UNIQUE KEY `uk_counselor_dept` (`counselor_id`,`dept_id`),
  KEY `idx_dept_id` (`dept_id`),
  CONSTRAINT `fk_counselor_dept_counselor` FOREIGN KEY (`counselor_id`) REFERENCES `counselor_info` (`counselor_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_counselor_dept_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='辅导员部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `counselor_dept`
--

LOCK TABLES `counselor_dept` WRITE;
/*!40000 ALTER TABLE `counselor_dept` DISABLE KEYS */;
INSERT INTO `counselor_dept` VALUES (1,2,103,'0','admin','2026-02-03 19:18:05','admin','2026-02-03 19:18:05',NULL),(2,2,101,'0','admin','2026-02-03 19:37:39','admin','2026-02-03 19:37:39',NULL),(3,2,201,'0','admin','2026-03-12 09:11:30','admin','2026-03-12 09:11:30',NULL);
/*!40000 ALTER TABLE `counselor_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `counselor_info`
--

DROP TABLE IF EXISTS `counselor_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `counselor_info` (
  `counselor_id` bigint NOT NULL AUTO_INCREMENT COMMENT '辅导员ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `office` varchar(100) DEFAULT NULL COMMENT '办公室地址',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`counselor_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='辅导员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `counselor_info`
--

LOCK TABLES `counselor_info` WRITE;
/*!40000 ALTER TABLE `counselor_info` DISABLE KEYS */;
INSERT INTO `counselor_info` VALUES (2,101,'李四','17374668924','2364728886@qq.com','湖南省张家界市永定区温泉路一号  吉首大学张家界学院','0','','2025-09-24 19:12:21','','2026-03-12 09:29:36',NULL);
/*!40000 ALTER TABLE `counselor_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course_like`
--

DROP TABLE IF EXISTS `course_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `course_id` bigint NOT NULL COMMENT '课程ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_course_user` (`course_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='课程点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course_like`
--

LOCK TABLES `course_like` WRITE;
/*!40000 ALTER TABLE `course_like` DISABLE KEYS */;
INSERT INTO `course_like` VALUES (4,3,100,'2026-03-18 19:35:52');
/*!40000 ALTER TABLE `course_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `evaluation_result`
--

DROP TABLE IF EXISTS `evaluation_result`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_result` (
  `result_id` bigint NOT NULL AUTO_INCREMENT COMMENT '测评结果ID',
  `student_id` bigint DEFAULT NULL COMMENT '学生ID',
  `questionnaire_id` bigint DEFAULT NULL COMMENT '问卷ID',
  `total_score` int DEFAULT NULL COMMENT '总得分',
  `risk_level` varchar(20) DEFAULT NULL COMMENT '风险等级（低/中/高）',
  `ai_analysis` json DEFAULT NULL COMMENT 'AI 分析结果（JSON格式）',
  `ai_status` char(1) DEFAULT '0' COMMENT 'AI分析状态（0未完成 1已完成）',
  `read_status` char(1) DEFAULT '0' COMMENT '已读标识（0未读 1已读）',
  `completion_status` char(1) DEFAULT '0' COMMENT '完成标识（0未完成 1已完成）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`result_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测评结果表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `evaluation_result`
--

LOCK TABLES `evaluation_result` WRITE;
/*!40000 ALTER TABLE `evaluation_result` DISABLE KEYS */;
INSERT INTO `evaluation_result` VALUES (34,9,18,25,'高','{\"indicators\": {\"sleep_score\": 35, \"social_score\": 40, \"stress_score\": 15, \"anxiety_score\": 30, \"emotion_score\": 25, \"depression_score\": 20, \"self_efficacy_score\": 30}, \"risk_level\": \"高\", \"main_issues\": [\"存在明确自杀意念，需立即危机干预\", \"重度抑郁情绪与无助感显著\", \"学习压力感知极强且缺乏应对资源\", \"情绪调节能力严重受损\", \"自我价值感与掌控感极度低下\"], \"suggestions\": [\"立即联系学校心理中心或拨打心理援助热线（如北京24小时希望热线：400-161-9995）\", \"避免独处，确保身边有可信赖的人陪伴并监督安全\", \"暂停学业相关决策，优先保障生命安全与基础生理需求\", \"尽快预约精神科医生进行专业评估，排除抑郁症等临床诊断\", \"尝试最简可行的自我安抚行为：深呼吸三次、握紧再松开双手、喝一口温水\"], \"total_score\": 25, \"detailed_analysis\": \"用户在简答题中直接表达‘我想自杀，我想去死啊’，属于明确自杀意念，构成最高优先级心理危机；选择题中多题呈现极端负向反应（如D选项常代表‘几乎总是’或‘非常严重’），结合‘情绪低落或无助（C）’‘明显焦虑紧张（D）’‘学习任务过重（D）’，显示抑郁、焦虑、压力三维度均处于临床关注水平；社交倾向选D（可能为‘完全倾向团队协作’或量表中高分项），但与主观报告的孤立绝望感矛盾，提示现实功能已严重解体；整体得分极低，非一般心理困扰，而是需要紧急临床介入的危机状态。\"}','1','1','1','','2026-03-21 01:29:16','','2026-03-21 01:29:16',NULL),(35,9,19,25,'高','{\"indicators\": {\"sleep_score\": 25, \"social_score\": 30, \"stress_score\": 15, \"anxiety_score\": 20, \"emotion_score\": 15, \"depression_score\": 10, \"self_efficacy_score\": 10}, \"risk_level\": \"高\", \"main_issues\": [\"重度抑郁情绪倾向\", \"自杀意念明确表达\", \"自我价值感严重崩塌\", \"压力应对机制完全失效\", \"情绪调节能力几近丧失\"], \"suggestions\": [\"立即联系学校心理中心或拨打心理援助热线（如北京24小时希望热线：010-82951332）\", \"避免独处，确保身边有可信任的人陪伴\", \"暂停所有求职相关活动，优先保障基本生活节律与安全\", \"尽快预约精神科医生进行专业评估，排除中重度抑郁症可能\", \"使用‘安全计划’技术：写下3个能让你暂停冲动的具体动作（如深呼吸10次、握冰块、给朋友发固定暗号）\"], \"total_score\": 25, \"detailed_analysis\": \"用户在全部5道选择题中均选择D选项（通常代表最消极/回避/无力的应对模式），简答题中出现\'失败了，想死\'和\'不管了\'等高度危险性表述，表明已处于急性心理危机状态。抑郁维度得分极低（10分），自我效能感（10分）与情绪稳定性（15分）接近临界值，显示核心信念系统受损；焦虑（20分）与压力（15分）得分同样极低，反映并非过度焦虑而是习得性无助导致的情绪耗竭。睡眠（25分）与社交功能（30分）得分低迷，印证生理及人际支持系统已显著退缩。当前首要风险为自杀行为，需启动紧急干预流程，而非常规心理咨询。\"}','1','1','1','','2026-03-21 09:25:19','','2026-03-21 09:26:08',NULL),(36,9,20,35,'高','{\"indicators\": {\"sleep_score\": 30, \"social_score\": 45, \"stress_score\": 15, \"anxiety_score\": 20, \"emotion_score\": 20, \"depression_score\": 25, \"self_efficacy_score\": 25}, \"risk_level\": \"高\", \"main_issues\": [\"重度焦虑与自杀意念\", \"显著的自我价值感崩塌\", \"行动力严重受阻\", \"情绪调节能力几近丧失\"], \"suggestions\": [\"立即联系学校心理中心或拨打心理援助热线（如北京24小时热线：010-82951332）\", \"避免独自承受压力，向可信赖的亲友或辅导员坦诚当前情绪状态\", \"暂停求职相关高强度任务，优先保障基本作息与安全\", \"考虑精神科评估以排除中重度抑郁障碍，必要时启动药物与心理联合干预\"], \"total_score\": 35, \"detailed_analysis\": \"用户在全部选择题中高频选择消极应对选项（D/C），尤其在简答题中直接出现‘我想死’的明确自杀意念表述，结合持续性焦虑、情绪麻木、躯体化反应缺失（未描述具体身体感受但用语极度简缩）、自我效能感全面瓦解（‘压力大。焦虑’的重复性无力表达），提示存在高危心理危机状态。焦虑、抑郁、情绪稳定性、自我效能感及睡眠维度得分均低于30分，压力维度低至15分，反映应对资源几近枯竭；社交功能虽相对稍高（45分），但仍显著低于健康阈值，显示社会支持系统尚未被有效激活。当前核心风险为急性自杀行为可能性，需优先启动危机干预，而非常规职业心理辅导。\"}','1','1','1','','2026-03-21 10:34:25','','2026-03-21 10:35:48',NULL);
/*!40000 ALTER TABLE `evaluation_result` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
INSERT INTO `gen_table` VALUES (4,'student_info','学生信息表',NULL,NULL,'StudentInfo','crud','element-plus','com.mc.student','student','info','学生信息','ruoyi','0','/','{}','admin','2025-09-21 19:11:12','','2025-09-21 19:12:35',NULL),(5,'questionnaire','心理测评问卷表',NULL,NULL,'Questionnaire','crud','element-plus','com.mc.questionnaire','questionnaire','questionnaireinfo','心理测评问卷管理','caidu','0','/','{\"parentMenuId\":2007}','admin','2025-09-22 19:18:16','','2025-09-22 20:01:25',NULL),(6,'question_bank','心理测评题库表',NULL,NULL,'QuestionBank','crud','element-plus','com.mc.questionnaire','questionnaire','questionnairebank','题库管理','caidu','0','/','{\"parentMenuId\":2007}','admin','2025-09-23 18:24:45','','2025-09-23 18:31:29',NULL),(7,'counselor_info','辅导员信息表',NULL,NULL,'CounselorInfo','crud','element-plus','com.mc.counselor','counselor','counselorinfo','辅导员管理','caidu','0','/','{\"parentMenuId\":2000}','admin','2025-09-24 18:47:37','','2025-09-24 18:55:33',NULL),(8,'evaluation_result','心理测评结果表',NULL,NULL,'EvaluationResult','crud','element-plus','com.mc.evaluation','evaluation','evaluationResult','心理测评结果','ruoyi','0','/','{\"parentMenuId\":2007}','admin','2025-09-25 19:54:11','','2025-09-25 20:04:13',NULL),(9,'questionnaire_answer','心理测评答题记录表',NULL,NULL,'QuestionnaireAnswer','crud','element-plus','com.mc.questionnaire','questionnaireAnswer','questionnaireAnswer','心理测评答题记录','caidu','0','/','{\"parentMenuId\":2007}','admin','2025-11-08 18:18:26','','2025-11-08 18:20:14',NULL),(10,'recommend_article','心理文章推荐表',NULL,NULL,'RecommendArticle','crud','element-plus','com.mc.recommend','RecommendArticle','RecommendArticle','心理文章推荐','caidu','0','/','{}','admin','2025-11-08 19:36:25','','2025-11-08 19:38:15',NULL),(11,'recommend_course','心理课程推荐表',NULL,NULL,'RecommendCourse','crud','element-plus','com.mc.recommend','RecommendCourse','RecommendCourse','心理课程推荐','caidu','0','/','{\"parentMenuId\":2052}','admin','2025-11-08 19:36:26','','2025-11-08 19:37:18',NULL),(12,'recommend_music','心理音乐推荐表',NULL,NULL,'RecommendMusic','crud','element-plus','com.mc.recommend','RecommendMusic','RecommendMusic','心理音乐推荐','caidu','0','/','{\"parentMenuId\":2052}','admin','2025-11-08 19:36:26','','2025-11-08 19:38:23',NULL),(13,'banner','轮播图表',NULL,NULL,'Banner','crud','element-plus','com.mc.banner','banner','banner','轮播图','caidu','0','/','{\"parentMenuId\":1}','admin','2025-11-09 17:20:08','','2025-11-09 17:23:12',NULL),(14,'community','社区帖子表',NULL,NULL,'Community','crud','element-plus','com.mc.community','community','community','帖子管理','caidu','0','/','{\"parentMenuId\":2077}','admin','2025-11-09 19:08:29','','2025-11-09 19:10:48',NULL);
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
INSERT INTO `gen_table_column` VALUES (48,4,'student_id','学生ID','bigint','Long','studentId','1','1','0','0',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(49,4,'user_id','用户id','bigint','Long','userId','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(50,4,'student_no','学号','varchar(50)','String','studentNo','0','0','1','1','1','1','1','EQ','input','',3,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(51,4,'name','姓名','varchar(50)','String','name','0','0','1','1','1','1','1','LIKE','input','',4,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(52,4,'gender','性别（0/1）','char(1)','String','gender','0','0','1','1','1','1','1','EQ','select','sys_user_sex',5,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(53,4,'grade','年级','varchar(20)','String','grade','0','0','0','1','1','1','0','EQ','input','',6,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(54,4,'major','专业','varchar(50)','String','major','0','0','0','1','1','1','0','EQ','input','',7,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(55,4,'class_name','班级','varchar(50)','String','className','0','0','0','1','1','1','0','LIKE','input','',8,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(56,4,'phone','联系电话','varchar(20)','String','phone','0','0','1','1','1','1','1','EQ','input','',9,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(57,4,'status','状态（0正常 1异常）','char(1)','String','status','0','0','1','1','1','1','1','EQ','select','student_status',10,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(58,4,'create_by','创建者','varchar(64)','String','createBy','0','0','0','0',NULL,NULL,NULL,'EQ','input','',11,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(59,4,'create_time','创建时间','datetime','Date','createTime','0','0','0','0',NULL,NULL,NULL,'EQ','datetime','',12,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(60,4,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','0','0',NULL,NULL,'EQ','input','',13,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(61,4,'update_time','更新时间','datetime','Date','updateTime','0','0','0','0','0',NULL,NULL,'EQ','datetime','',14,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(62,4,'remark','备注','varchar(200)','String','remark','0','0','0','0','0','0',NULL,'EQ','input','',15,'admin','2025-09-21 19:11:12','','2025-09-21 19:12:35'),(63,5,'questionnaire_id','问卷ID','bigint','Long','questionnaireId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(64,5,'title','问卷标题','varchar(200)','String','title','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(65,5,'description','问卷说明','varchar(500)','String','description','0','0','0','1','1','1','1','EQ','textarea','',3,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(66,5,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',4,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(67,5,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',5,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(68,5,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',6,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(69,5,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',7,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(70,5,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',8,'admin','2025-09-22 19:18:16','','2025-09-22 20:01:25'),(71,6,'bank_id','题库题目ID','bigint','Long','bankId','1','1','0','0',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(72,6,'type','题目类型（choice选择题/short_answer简答题）','varchar(20)','String','type','0','0','1','1','1','1','1','EQ','select','question_type',2,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(73,6,'content','题干内容','varchar(500)','String','content','0','0','1','1','1','1','1','LIKE','editor','',3,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(74,6,'options','选择题选项（A/B/C/D…），简答题为空','json','String','options','0','0','1','1','1','1','0','EQ','input','',4,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(75,6,'standard_answer','标准答案（仅选择题有效）','varchar(200)','String','standardAnswer','0','0','1','1','1','1','0','EQ','input','',5,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(76,6,'score','分值','int','Long','score','0','0','1','1','1','1','0','EQ','input','',6,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(77,6,'create_by','创建者','varchar(64)','String','createBy','0','0','0','0',NULL,NULL,NULL,'EQ','input','',7,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(78,6,'create_time','创建时间','datetime','Date','createTime','0','0','0','0',NULL,NULL,NULL,'EQ','datetime','',8,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(79,6,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','0','0',NULL,NULL,'EQ','input','',9,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(80,6,'update_time','更新时间','datetime','Date','updateTime','0','0','0','0','0',NULL,NULL,'EQ','datetime','',10,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(81,6,'remark','备注','varchar(200)','String','remark','0','0','0','0','0','0',NULL,'EQ','input','',11,'admin','2025-09-23 18:24:45','','2025-09-23 18:31:29'),(82,7,'counselor_id','辅导员ID','bigint','Long','counselorId','1','1','0','0',NULL,NULL,'0','EQ','input','',1,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(83,7,'user_id','用户id','bigint','Long','userId','0','0','1','1','1','1','0','EQ','input','',2,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(84,7,'name','姓名','varchar(50)','String','name','0','0','1','1','1','1','1','LIKE','input','',3,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(85,7,'phone','联系电话','varchar(20)','String','phone','0','0','1','1','1','1','1','EQ','input','',4,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(86,7,'email','邮箱','varchar(100)','String','email','0','0','1','1','1','1','1','EQ','input','',5,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(87,7,'office','办公室地址','varchar(100)','String','office','0','0','1','1','1','1','1','EQ','input','',6,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(88,7,'status','状态（0正常 1停用）','char(1)','String','status','0','0','1','1','1','1','1','EQ','select','counselor_status',7,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(89,7,'create_by','创建者','varchar(64)','String','createBy','0','0','0','0',NULL,NULL,NULL,'EQ','input','',8,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(90,7,'create_time','创建时间','datetime','Date','createTime','0','0','0','0',NULL,NULL,NULL,'EQ','datetime','',9,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(91,7,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','0','0',NULL,NULL,'EQ','input','',10,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(92,7,'update_time','更新时间','datetime','Date','updateTime','0','0','0','0','0',NULL,NULL,'EQ','datetime','',11,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(93,7,'remark','备注','varchar(200)','String','remark','0','0','0','0','0','0',NULL,'EQ','input','',12,'admin','2025-09-24 18:47:37','','2025-09-24 18:55:33'),(94,8,'result_id','测评结果ID','bigint','Long','resultId','1','1','0','0',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(95,8,'student_id','学生ID','bigint','Long','studentId','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(96,8,'questionnaire_id','问卷ID','bigint','Long','questionnaireId','0','0','1','1','1','1','1','EQ','input','',3,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(97,8,'total_score','总得分','int','Long','totalScore','0','0','1','1','1','1','0','EQ','input','',4,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(98,8,'risk_level','风险等级（低/中/高）','varchar(20)','String','riskLevel','0','0','1','1','1','1','1','EQ','select','risk_level',5,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(99,8,'ai_analysis','AI 分析结果（JSON格式）','json','String','aiAnalysis','0','0','1','1','1','1','0','EQ','input','',6,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(100,8,'ai_status','AI分析状态（0未完成 1已完成）','char(1)','String','aiStatus','0','0','1','1','1','1','1','EQ','select','ai_status',7,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(101,8,'read_status','已读标识（0未读 1已读）','char(1)','String','readStatus','0','0','1','1','1','1','1','EQ','select','read_status',8,'admin','2025-09-25 19:54:11','','2025-09-25 20:04:13'),(102,8,'completion_status','完成标识（0未完成 1已完成）','char(1)','String','completionStatus','0','0','1','1','1','1','1','EQ','select','completion_status',9,'admin','2025-09-25 19:54:12','','2025-09-25 20:04:13'),(103,8,'create_by','创建者','varchar(64)','String','createBy','0','0','0','0',NULL,NULL,NULL,'EQ','input','',10,'admin','2025-09-25 19:54:12','','2025-09-25 20:04:13'),(104,8,'create_time','创建时间','datetime','Date','createTime','0','0','0','0',NULL,NULL,NULL,'EQ','datetime','',11,'admin','2025-09-25 19:54:12','','2025-09-25 20:04:13'),(105,8,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','0','0',NULL,NULL,'EQ','input','',12,'admin','2025-09-25 19:54:12','','2025-09-25 20:04:14'),(106,8,'update_time','更新时间','datetime','Date','updateTime','0','0','0','0','0',NULL,NULL,'EQ','datetime','',13,'admin','2025-09-25 19:54:12','','2025-09-25 20:04:14'),(107,8,'remark','备注','varchar(200)','String','remark','0','0','0','0','0','0',NULL,'EQ','input','',14,'admin','2025-09-25 19:54:12','','2025-09-25 20:04:14'),(108,9,'answer_id','答题记录ID','bigint','Long','answerId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(109,9,'result_id','关联心理测评结果ID','bigint','Long','resultId','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(110,9,'questionnaire_id','问卷ID','bigint','Long','questionnaireId','0','0','1','1','1','1','1','EQ','input','',3,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(111,9,'question_id','题目ID','bigint','Long','questionId','0','0','1','1','1','1','1','EQ','input','',4,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(112,9,'type','题目类型（choice选择题/short_answer简答题）','varchar(20)','String','type','0','0','1','1','1','1','1','EQ','select','',5,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(113,9,'content','题干内容','varchar(500)','String','content','0','0','1','1','1','1','1','EQ','editor','',6,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(114,9,'options','选择题选项（A/B/C/D…），简答题为空','json','String','options','0','0','0','1','1','1','1','EQ',NULL,'',7,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(115,9,'standard_answer','标准答案（仅选择题有效）','varchar(200)','String','standardAnswer','0','0','0','1','1','1','1','EQ','input','',8,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(116,9,'score','分值','int','Long','score','0','0','0','1','1','1','1','EQ','input','',9,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(117,9,'user_answer','用户作答内容','varchar(500)','String','userAnswer','0','0','0','1','1','1','1','EQ','textarea','',10,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(118,9,'is_correct','是否答对（1正确/0错误/空表示简答题）','tinyint(1)','Integer','isCorrect','0','0','0','1','1','1','1','EQ','input','',11,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(119,9,'obtain_score','用户获得分数','int','Long','obtainScore','0','0','0','1','1','1','1','EQ','input','',12,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(120,9,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',13,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(121,9,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',14,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(122,9,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',15,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(123,9,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',16,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(124,9,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',17,'admin','2025-11-08 18:18:26','','2025-11-08 18:20:14'),(125,10,'article_id','文章ID','bigint','Long','articleId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(126,10,'title','文章标题','varchar(100)','String','title','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(127,10,'content','文章内容（Markdown格式）','text','String','content','0','0','1','1','1','1','1','EQ','editor','',3,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(128,10,'summary','文章摘要','varchar(500)','String','summary','0','0','0','1','1','1','1','EQ','textarea','',4,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(129,10,'author','作者','varchar(64)','String','author','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(130,10,'read_count','阅读量','int','Long','readCount','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(131,10,'category','文章分类','varchar(50)','String','category','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(132,10,'status','状态（0正常 1下架）','char(1)','String','status','0','0','0','1','1','1','1','EQ','radio','',8,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(133,10,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',9,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(134,10,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',10,'admin','2025-11-08 19:36:25','','2025-11-08 19:38:15'),(135,10,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',11,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:15'),(136,10,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',12,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:15'),(137,10,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',13,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:15'),(138,11,'course_id','课程ID','bigint','Long','courseId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(139,11,'title','课程标题','varchar(100)','String','title','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(140,11,'mp4_url','视频文件链接','varchar(255)','String','mp4Url','0','0','1','1','1','1','1','EQ','input','',3,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(141,11,'cover_url','封面图链接','varchar(255)','String','coverUrl','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(142,11,'lecturer','讲师','varchar(64)','String','lecturer','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(143,11,'duration','视频时长（秒）','int','Long','duration','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(144,11,'chapters','章节数','int','Long','chapters','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(145,11,'level','课程难度（0初级/1中级/2高级）','varchar(20)','String','level','0','0','0','1','1','1','1','EQ','input','',8,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(146,11,'description','课程简介','varchar(500)','String','description','0','0','0','1','1','1','1','EQ','textarea','',9,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(147,11,'status','状态（0正常 1下架）','char(1)','String','status','0','0','0','1','1','1','1','EQ','radio','',10,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(148,11,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',11,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(149,11,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',12,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(150,11,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',13,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(151,11,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',14,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(152,11,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',15,'admin','2025-11-08 19:36:26','','2025-11-08 19:37:18'),(153,12,'music_id','音乐ID','bigint','Long','musicId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(154,12,'title','音乐标题','varchar(100)','String','title','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(155,12,'mp3_url','音频文件链接','varchar(255)','String','mp3Url','0','0','1','1','1','1','1','EQ','input','',3,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(156,12,'cover_url','封面图链接','varchar(255)','String','coverUrl','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(157,12,'artist','演唱者/作者','varchar(64)','String','artist','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(158,12,'genre','音乐风格','varchar(50)','String','genre','0','0','0','1','1','1','1','EQ','input','',6,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(159,12,'duration','时长（秒）','int','Long','duration','0','0','0','1','1','1','1','EQ','input','',7,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(160,12,'description','音乐简介','varchar(500)','String','description','0','0','0','1','1','1','1','EQ','textarea','',8,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(161,12,'status','状态（0正常 1下架）','char(1)','String','status','0','0','0','1','1','1','1','EQ','radio','',9,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(162,12,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',10,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(163,12,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',11,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(164,12,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',12,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(165,12,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',13,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(166,12,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',14,'admin','2025-11-08 19:36:26','','2025-11-08 19:38:23'),(167,13,'banner_id','轮播图ID','bigint','Long','bannerId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(168,13,'title','轮播标题','varchar(100)','String','title','0','0','1','1','1','1','1','EQ','input','',2,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(169,13,'image_url','轮播图链接','varchar(255)','String','imageUrl','0','0','1','1','1','1','1','EQ','input','',3,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(170,13,'link_url','跳转链接（点击后访问的页面）','varchar(255)','String','linkUrl','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(171,13,'sort_order','显示顺序（越小越靠前）','int','Long','sortOrder','0','0','0','1','1','1','1','EQ','input','',5,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(172,13,'start_time','展示开始时间','datetime','Date','startTime','0','0','0','1','1','1','1','EQ','datetime','',6,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(173,13,'end_time','展示结束时间','datetime','Date','endTime','0','0','0','1','1','1','1','EQ','datetime','',7,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(174,13,'status','状态（0正常 1下架）','char(1)','String','status','0','0','0','1','1','1','1','EQ','radio','',8,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(175,13,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',9,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(176,13,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',10,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(177,13,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',11,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(178,13,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',12,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(179,13,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',13,'admin','2025-11-09 17:20:08','','2025-11-09 17:23:12'),(180,14,'post_id','帖子ID','bigint','Long','postId','1','1','0','1',NULL,NULL,NULL,'EQ','input','',1,'admin','2025-11-09 19:08:29','','2025-11-09 19:10:48'),(181,14,'student_id','学生ID','bigint','Long','studentId','0','0','0','1','1','1','1','EQ','input','',2,'admin','2025-11-09 19:08:29','','2025-11-09 19:10:48'),(182,14,'content','帖子内容','text','String','content','0','0','0','1','1','1','1','EQ','editor','',3,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(183,14,'is_anonymous','是否匿名（0否 1是）','char(1)','String','isAnonymous','0','0','0','1','1','1','1','EQ','input','',4,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(184,14,'status','状态（0正常 1屏蔽）','char(1)','String','status','0','0','0','1','1','1','1','EQ','radio','',5,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(185,14,'create_by','创建者','varchar(64)','String','createBy','0','0','0','1',NULL,NULL,NULL,'EQ','input','',6,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(186,14,'create_time','创建时间','datetime','Date','createTime','0','0','0','1',NULL,NULL,NULL,'EQ','datetime','',7,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(187,14,'update_by','更新者','varchar(64)','String','updateBy','0','0','0','1','1',NULL,NULL,'EQ','input','',8,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(188,14,'update_time','更新时间','datetime','Date','updateTime','0','0','0','1','1',NULL,NULL,'EQ','datetime','',9,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48'),(189,14,'remark','备注','varchar(200)','String','remark','0','0','0','1','1','1',NULL,'EQ','input','',10,'admin','2025-11-09 19:08:30','','2025-11-09 19:10:48');
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `intervention_notification`
--

DROP TABLE IF EXISTS `intervention_notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `intervention_notification` (
  `notification_id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `result_id` bigint DEFAULT NULL COMMENT '评测结果ID',
  `student_id` bigint DEFAULT NULL COMMENT '学生ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID（负责人）',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `notification_type` varchar(50) DEFAULT NULL COMMENT '通知类型',
  `notification_content` text COMMENT '通知内容',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `read_status` char(1) DEFAULT '0' COMMENT '阅读状态（0未读 1已读）',
  `process_status` char(1) DEFAULT '0' COMMENT '处理状态（0待处理 1已处理 2处理中）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notification_id`),
  KEY `idx_result_id` (`result_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_read_status` (`read_status`),
  KEY `idx_process_status` (`process_status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='干预通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intervention_notification`
--

LOCK TABLES `intervention_notification` WRITE;
/*!40000 ALTER TABLE `intervention_notification` DISABLE KEYS */;
INSERT INTO `intervention_notification` VALUES (4,34,9,101,103,'风险干预','学生001（22级软件工程）心理健康测评总分25分，风险等级为“高”，存在明确自杀意念（简答题直述“我想自杀，我想去死啊”），伴重度抑郁、显著焦虑、情绪调节严重受损及自我价值感极度低下。多项选择题显示其长期处于情绪低落（C）、高度紧张（D）、学习压力过载（D）状态，现实功能已明显解体。\n\n请立即启动危机干预：  \n① 即刻联系校心理中心或拨打北京24小时希望热线400-161-9995；  \n② 安排可信同学/室友陪伴，避免独处，确保人身安全；  \n③ 暂缓学业安排，优先保障睡眠、饮食与基本休息；  \n④ 协助预约精神科医生进行临床评估；  \n⑤ 引导其尝试最简安抚行为：深呼吸三次、握拳再松开、喝一口温水。\n\n生命至上，请即刻行动，后续及时反馈干预进展。','2026-03-13 20:43:53','1','0','','2026-03-13 20:43:53','',NULL,NULL),(5,35,9,101,103,'风险干预','学生001（22级软件工程）在《就业压力》问卷中总分25分，风险等级为“高”。AI分析显示其处于急性心理危机状态：抑郁维度仅10分，简答题中明确出现“失败了，想死”“不管了”等危险表述，且5道选择题全选最消极选项（D），提示习得性无助、自我价值感崩塌、情绪调节与压力应对能力几近丧失，存在明确自杀意念。当前首要风险为自杀行为，需立即启动紧急干预。请辅导员：①即刻联系学生，避免其独处，确保身边有可信任同伴或家人陪伴；②今日内协助其拨打北京24小时希望热线（010-82951332）或联系校心理中心启动危机响应；③暂缓所有求职安排，优先保障规律作息、基本饮食与人身安全；④同步建议尽快前往三甲医院精神科进行专业评估。生命至上，请务必高度重视、迅速行动。','2026-03-21 09:26:14','0','0','','2026-03-21 09:26:14','',NULL,NULL),(6,36,9,101,103,'风险干预','学生001（22级软件工程）在《就业压力AAA》测评中总分35分，风险等级为“高”。AI分析显示其存在**重度焦虑与明确自杀意念**（简答题直述“我想死”），情绪稳定性、自我效能感、睡眠质量等维度均低于30分，行动力严重受阻，社会支持未被有效激活。当前核心风险为急性心理危机，需立即干预。  \n请辅导员：  \n① **今日内主动、温和地联系学生**，表达关心，不评判、不施压，重点确认其当下安全；  \n② **同步对接校心理中心启动危机响应**（可拨打北京24小时热线010-82951332转介）；  \n③ 协助暂停求职任务，优先保障规律作息与人身安全；  \n④ 若学生有自伤/自杀念头或计划，请立即启动学院-学工部-心理中心三方应急联动。  \n生命至上，陪伴是最有力的干预。','2026-03-21 10:35:55','0','0','','2026-03-21 10:35:55','',NULL,NULL);
/*!40000 ALTER TABLE `intervention_notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `intervention_process_record`
--

DROP TABLE IF EXISTS `intervention_process_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `intervention_process_record` (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `notification_id` bigint DEFAULT NULL COMMENT '通知ID',
  `user_id` bigint DEFAULT NULL COMMENT '处理人用户ID',
  `process_content` text COMMENT '处理内容',
  `process_time` datetime DEFAULT NULL COMMENT '处理时间',
  `process_result` varchar(200) DEFAULT NULL COMMENT '处理结果',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1异常）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`record_id`),
  KEY `idx_notification_id` (`notification_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='干预处理记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intervention_process_record`
--

LOCK TABLES `intervention_process_record` WRITE;
/*!40000 ALTER TABLE `intervention_process_record` DISABLE KEYS */;
INSERT INTO `intervention_process_record` VALUES (3,4,101,NULL,NULL,NULL,'0','','2026-03-13 20:43:53','',NULL,NULL),(4,5,101,NULL,NULL,NULL,'0','','2026-03-21 09:26:14','',NULL,NULL),(5,6,101,NULL,NULL,NULL,'0','','2026-03-21 10:35:55','',NULL,NULL);
/*!40000 ALTER TABLE `intervention_process_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `intervention_risk_config`
--

DROP TABLE IF EXISTS `intervention_risk_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `intervention_risk_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `risk_level` varchar(20) DEFAULT NULL COMMENT '风险等级（低/中/高）',
  `min_score` int DEFAULT NULL COMMENT '最低分数',
  `max_score` int DEFAULT NULL COMMENT '最高分数',
  `notification_template` text COMMENT '通知模板',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_risk_level` (`risk_level`)
) ENGINE=InnoDB AUTO_INCREMENT=33334 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='风险等级配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intervention_risk_config`
--

LOCK TABLES `intervention_risk_config` WRITE;
/*!40000 ALTER TABLE `intervention_risk_config` DISABLE KEYS */;
INSERT INTO `intervention_risk_config` VALUES (1,'低',0,57,'学生测评分数为${score}分，风险等级为低，请关注学生心理健康。','0','','2026-03-11 23:19:05','','2026-03-21 11:11:05',NULL),(2,'中',58,81,'学生测评分数为${score}分，风险等级为中，建议关注并适时干预。','0','','2026-03-11 23:19:05','','2026-03-21 11:11:05',NULL),(3,'高',82,100,'学生测评分数为${score}分，风险等级为高，请及时采取干预措施！','0','','2026-03-11 23:19:05','','2026-03-21 11:11:06',NULL);
/*!40000 ALTER TABLE `intervention_risk_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `music_like`
--

DROP TABLE IF EXISTS `music_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `music_id` bigint NOT NULL COMMENT '音乐ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_music_user` (`music_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `music_like`
--

LOCK TABLES `music_like` WRITE;
/*!40000 ALTER TABLE `music_like` DISABLE KEYS */;
INSERT INTO `music_like` VALUES (4,9,100,'2026-03-18 19:36:30');
/*!40000 ALTER TABLE `music_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) NOT NULL COMMENT '状态',
  `job_name` varchar(200) DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) NOT NULL COMMENT '任务组名',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `question_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题目ID',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID（外键）',
  `type` varchar(20) NOT NULL COMMENT '题目类型（choice选择题/short_answer简答题）',
  `content` varchar(500) NOT NULL COMMENT '题干内容',
  `options` json DEFAULT NULL COMMENT '选择题选项（A/B/C/D…），简答题为空',
  `standard_answer` varchar(200) DEFAULT NULL COMMENT '标准答案（仅选择题有效）',
  `score` int DEFAULT '0' COMMENT '分值',
  `order_num` int DEFAULT '0' COMMENT '题目顺序',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`question_id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测评题目表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (31,18,'choice','在最近一周，你是否经常感到学习任务过重？','{\"A\": \"几乎没有\", \"B\": \"偶尔有\", \"C\": \"经常有\", \"D\": \"总是如此\"}',NULL,0,1,'admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(32,18,'choice','遇到学习困难时，你通常的反应是？','{\"A\": \"积极寻求帮助\", \"B\": \"拖延回避\", \"C\": \"情绪低落\", \"D\": \"继续坚持\"}',NULL,0,2,'admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(33,18,'choice','在过去两周，你是否经常感到情绪低落或无助？','{\"A\": \"从不\", \"B\": \"有时\", \"C\": \"经常\", \"D\": \"几乎每天\"}',NULL,0,3,'admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(34,18,'choice','当你遇到不顺心的事，你是否会出现明显的焦虑或紧张？','{\"A\": \"完全不会\", \"B\": \"偶尔\", \"C\": \"经常\", \"D\": \"非常频繁\"}',NULL,0,4,'admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(35,18,'choice','你更倾向于独立完成任务还是团队协作？','{\"A\": \"独立完成\", \"B\": \"团队协作\", \"C\": \"视情况而定\", \"D\": \"没有特别倾向\"}',NULL,0,5,'admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(36,18,'short_answer','你最近有什么压力吗？',NULL,NULL,0,6,'admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(37,19,'choice','面对即将到来的就业面试，你通常会感到：','{\"A\": \"轻松自信，认为自己已做好充分准备\", \"B\": \"略有紧张，但能通过深呼吸或复习缓解\", \"C\": \"明显焦虑，常失眠、注意力不集中，影响日常学习\", \"D\": \"极度恐惧，回避投递简历或取消面试安排\"}',NULL,0,1,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(38,19,'choice','当面试结果未如期公布时，你的典型反应是：','{\"A\": \"耐心等待，同时继续推进其他求职计划\", \"B\": \"稍感不安，会主动联系HR确认进度\", \"C\": \"反复猜测失败原因，自我否定感增强\", \"D\": \"陷入持续低落，质疑自身价值与职业前景\"}',NULL,0,2,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(39,19,'choice','在模拟面试或小组讨论中，你如何应对他人提出的尖锐问题或不同意见？','{\"A\": \"冷静倾听，理性回应，视其为提升机会\", \"B\": \"略感压力，但能组织语言完成表达\", \"C\": \"容易情绪波动，事后反复纠结对方评价\", \"D\": \"本能回避发言，或提前退出类似活动\"}',NULL,0,3,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(40,19,'choice','你如何看待‘面试失败=个人能力不足’这一观点？','{\"A\": \"完全不认同，认为面试结果受多重因素影响\", \"B\": \"部分认同，但也意识到岗位匹配度和临场状态的作用\", \"C\": \"基本认同，常将一次失利归因为自身缺陷\", \"D\": \"强烈认同，失败后长时间丧失求职动力\"}',NULL,0,4,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(41,19,'choice','为应对就业面试压力，你最常采用的调节方式是：','{\"A\": \"制定分阶段准备计划，并与朋辈开展互助演练\", \"B\": \"适度运动、听音乐或短暂放松，保持作息规律\", \"C\": \"靠熬夜刷题或反复修改简历来缓解焦虑感\", \"D\": \"回避准备、拖延投递，或用暴食/ binge-watching 等行为麻痹自己\"}',NULL,0,5,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(42,19,'short_answer','请回顾最近一次面试准备或实际经历，描述一个你曾感到‘真实做自己’的瞬间。当时发生了什么？你注意到了自己哪些未曾被平时关注的情绪、需求或优势？',NULL,NULL,0,6,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(43,19,'short_answer','如果可以给三个月前正在为面试焦虑的自己写一段话，你会强调什么？这段话是否也适用于现在的你？为什么？',NULL,NULL,0,7,'admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(44,20,'choice','面对即将到来的毕业与求职，你通常会感到：','{\"A\": \"充满期待和动力，已制定清晰的求职计划\", \"B\": \"有些紧张但总体可控，会主动了解招聘信息并尝试投递\", \"C\": \"经常感到焦虑、失眠或难以集中精力学习，担心找不到合适工作\", \"D\": \"持续感到绝望、无力，甚至怀疑自身价值，回避所有求职相关事务\"}',NULL,0,1,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL),(45,20,'choice','当收到简历石沉大海或面试被拒时，你的典型反应是：','{\"A\": \"冷静复盘原因，调整策略后继续投递\", \"B\": \"短暂低落，但1–2天内能自我调节并寻求朋友/老师建议\", \"C\": \"反复自责，质疑自己能力，较长时间情绪低落且影响日常学习\", \"D\": \"产生强烈羞耻感，回避社交与求职信息，出现躯体不适（如心悸、食欲骤降）\"}',NULL,0,2,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL),(46,20,'choice','在与同学讨论就业去向（如考公、考研、出国、就业）时，你常有的心理状态是：','{\"A\": \"尊重彼此选择，能坦然分享自己的规划与困惑\", \"B\": \"略有比较心理，但能理性看待差异，不因此动摇自我判断\", \"C\": \"频繁感到焦虑或自卑，担心落后于同龄人，反复追问‘我是不是选错了？’\", \"D\": \"因他人进展而持续性自我否定，出现逃避聚会、屏蔽社交平台相关信息等行为\"}',NULL,0,3,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL),(47,20,'choice','你如何看待‘第一份工作是否决定人生轨迹’这一说法？','{\"A\": \"认为职业发展是动态过程，首份工作是起点而非终点\", \"B\": \"承认其重要性，但相信可通过后续学习与调整改变方向\", \"C\": \"高度认同该说法，因此对求职结果过度苛求，难以接受‘备选方案’\", \"D\": \"将其视为不可逆的人生判决，一旦不如意便陷入长期抑郁或存在意义危机\"}',NULL,0,4,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL),(48,20,'choice','当你需要为求职做准备（如写简历、模拟面试、考公复习）时，你的行动状态通常是：','{\"A\": \"目标明确、时间管理良好，能平衡备考与日常生活\", \"B\": \"有时拖延但可自我督促完成，偶尔需外部提醒\", \"C\": \"长期拖延、启动困难，常以‘还没准备好’为由回避任务\", \"D\": \"完全无法开始，伴随强烈生理疲惫感或解离体验，持续两周以上无实质性准备行为\"}',NULL,0,5,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL),(49,20,'short_answer','请描述最近一次因就业压力引发情绪波动的具体情境。当时你的想法、身体感受、行为反应分别是什么？这一经历是否改变了你对‘职业成功’或‘自我价值’的理解？',NULL,NULL,0,6,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL),(50,20,'short_answer','如果可以给半年前正在焦虑求职的自己写一封信，你会写什么？这封信中，哪些话是你现在才真正理解、但当时最需要听到的？为什么？',NULL,NULL,0,7,'admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL);
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question_bank`
--

DROP TABLE IF EXISTS `question_bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question_bank` (
  `bank_id` bigint NOT NULL AUTO_INCREMENT COMMENT '题库题目ID',
  `type` varchar(20) NOT NULL COMMENT '题目类型（choice选择题/short_answer简答题）',
  `content` varchar(500) NOT NULL COMMENT '题干内容',
  `options` json DEFAULT NULL COMMENT '选择题选项（A/B/C/D…），简答题为空',
  `standard_answer` varchar(200) DEFAULT NULL COMMENT '标准答案（仅选择题有效）',
  `score` int DEFAULT '0' COMMENT '分值',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`bank_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测评题库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question_bank`
--

LOCK TABLES `question_bank` WRITE;
/*!40000 ALTER TABLE `question_bank` DISABLE KEYS */;
INSERT INTO `question_bank` VALUES (1,'choice','在最近一周，你是否经常感到学习任务过重？','{\"A\": \"几乎没有\", \"B\": \"偶尔有\", \"C\": \"经常有\", \"D\": \"总是如此\"}','B',5,'admin','2025-09-23 19:33:09','','2025-09-25 19:51:07','学业压力检测'),(2,'choice','遇到学习困难时，你通常的反应是？','{\"A\": \"积极寻求帮助\", \"B\": \"拖延回避\", \"C\": \"情绪低落\", \"D\": \"继续坚持\"}','A',5,'admin','2025-09-23 19:33:09','',NULL,'学习应对方式'),(3,'choice','在过去两周，你是否经常感到情绪低落或无助？','{\"A\": \"从不\", \"B\": \"有时\", \"C\": \"经常\", \"D\": \"几乎每天\"}','C',5,'admin','2025-09-23 19:33:09','','2025-09-23 20:15:16','情绪状态评估'),(4,'choice','当你遇到不顺心的事，你是否会出现明显的焦虑或紧张？','{\"A\": \"完全不会\", \"B\": \"偶尔\", \"C\": \"经常\", \"D\": \"非常频繁\"}','C',5,'admin','2025-09-23 19:33:09','',NULL,'焦虑检测'),(5,'choice','你更倾向于独立完成任务还是团队协作？','{\"A\": \"独立完成\", \"B\": \"团队协作\", \"C\": \"视情况而定\", \"D\": \"没有特别倾向\"}','C',3,'admin','2025-09-23 19:33:09','',NULL,'人格特质'),(12,'choice','当你感到学业压力特别大时，你通常会如何应对？','{\"A\": \"独自承受，不愿向他人倾诉\", \"B\": \"通过运动、听音乐等方式放松自己\", \"C\": \"暴饮暴食或熬夜打游戏来逃避压力\", \"D\": \"主动寻求老师、朋友或心理咨询师的帮助\"}',NULL,0,'','2026-03-21 01:45:55','',NULL,NULL),(13,'choice','在与同学相处时，以下哪种开玩笑的方式最有利于维护良好的人际关系？','{\"A\": \"拿对方的外貌缺陷或家庭背景开玩笑，认为‘熟人之间无伤大雅’\", \"B\": \"用自嘲的方式幽默表达，同时避免涉及他人隐私或敏感话题\", \"C\": \"在对方明显不开心时讲冷笑话试图‘活跃气氛’\", \"D\": \"经常模仿他人说话方式取乐，尤其针对口音或表达习惯\"}',NULL,0,'','2026-03-21 01:46:26','',NULL,NULL),(14,'choice','在成长过程中，你认为原生家庭对你的人际交往模式影响程度如何？','{\"A\": \"几乎没有影响，我的人际模式主要来自大学阶段的朋友和老师\", \"B\": \"有一定影响，比如我习惯回避冲突，可能与父母沟通方式有关\", \"C\": \"影响显著，我常不自觉地复制父母之间的互动模式（如过度讨好或控制）\", \"D\": \"影响深刻且持续，我经常因原生家庭经历而感到信任困难或情绪不稳定\"}',NULL,0,'','2026-03-21 01:50:44','',NULL,NULL),(15,'choice','面对即将毕业和就业的不确定性，你最近一个月内感到焦虑或压力的程度如何？','{\"A\": \"几乎没有，我能从容规划未来并积极准备求职\", \"B\": \"偶尔感到紧张，但通过自我调节能较快缓解\", \"C\": \"经常感到焦虑，担心找不到合适工作，影响睡眠或专注力\", \"D\": \"持续强烈焦虑，常有无助感、身体不适（如心慌、头痛），难以正常学习或求职\"}',NULL,0,'','2026-03-21 10:32:52','',NULL,NULL),(16,'choice','当你面临繁重的学业任务和多项截止日期临近时，你通常会如何应对？','{\"A\": \"立即制定详细计划，并分步骤执行，同时主动寻求老师或同学帮助\", \"B\": \"感到紧张但能自我调节，适当休息后继续专注完成任务\", \"C\": \"经常拖延，直到最后一刻才匆忙应付，事后感到疲惫和自责\", \"D\": \"持续感到心慌、失眠、注意力难以集中，甚至出现身体不适如头痛或胃痛\"}',NULL,0,'','2026-03-26 00:15:44','',NULL,NULL);
/*!40000 ALTER TABLE `question_bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questionnaire`
--

DROP TABLE IF EXISTS `questionnaire`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questionnaire` (
  `questionnaire_id` bigint NOT NULL AUTO_INCREMENT COMMENT '问卷ID',
  `title` varchar(200) DEFAULT NULL COMMENT '问卷标题',
  `description` varchar(500) DEFAULT NULL COMMENT '问卷说明',
  `status` char(1) DEFAULT '0' COMMENT '问卷状态（0正常 1下架）',
  `type` char(1) DEFAULT '0' COMMENT '问卷类型（0常规测评 1临时测评 2专项测评 3学业压力 4情绪状态 5人格特质）',
  `total_score` int DEFAULT '0' COMMENT '问卷总分',
  `start_time` datetime DEFAULT NULL COMMENT '问卷开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '问卷结束时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`questionnaire_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测评问卷表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questionnaire`
--

LOCK TABLES `questionnaire` WRITE;
/*!40000 ALTER TABLE `questionnaire` DISABLE KEYS */;
INSERT INTO `questionnaire` VALUES (18,'心理健康测试','心理健康是一个大问题','0','0',0,'2026-03-01 00:00:00','2026-03-31 00:00:00','admin','2026-03-13 19:27:59','admin','2026-03-13 19:27:59',NULL),(19,'就业压力','就业描述','0','3',0,'2026-03-01 09:25:02','2026-03-31 00:00:00','admin','2026-03-21 09:25:10','admin','2026-03-21 09:25:10',NULL),(20,'就业压力AAA','MIAOS','0','3',0,'2026-03-01 00:00:00','2026-03-31 00:00:00','admin','2026-03-21 10:34:13','admin','2026-03-21 10:34:13',NULL);
/*!40000 ALTER TABLE `questionnaire` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `questionnaire_answer`
--

DROP TABLE IF EXISTS `questionnaire_answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `questionnaire_answer` (
  `answer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '答题记录ID',
  `result_id` bigint NOT NULL COMMENT '关联心理测评结果ID',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `question_id` bigint NOT NULL COMMENT '题目ID',
  `type` varchar(20) NOT NULL COMMENT '题目类型（choice选择题/short_answer简答题）',
  `content` varchar(500) NOT NULL COMMENT '题干内容',
  `options` json DEFAULT NULL COMMENT '选择题选项（A/B/C/D…），简答题为空',
  `standard_answer` varchar(200) DEFAULT NULL COMMENT '标准答案（仅选择题有效）',
  `score` int DEFAULT '0' COMMENT '分值',
  `user_answer` varchar(500) DEFAULT NULL COMMENT '用户作答内容',
  `is_correct` tinyint(1) DEFAULT NULL COMMENT '是否答对（1正确/0错误/空表示简答题）',
  `obtain_score` int DEFAULT '0' COMMENT '用户获得分数',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`answer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理测评答题记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `questionnaire_answer`
--

LOCK TABLES `questionnaire_answer` WRITE;
/*!40000 ALTER TABLE `questionnaire_answer` DISABLE KEYS */;
INSERT INTO `questionnaire_answer` VALUES (25,34,18,31,'choice','在最近一周，你是否经常感到学习任务过重？','{\"A\": \"几乎没有\", \"B\": \"偶尔有\", \"C\": \"经常有\", \"D\": \"总是如此\"}',NULL,0,'D',NULL,0,'','2026-03-13 20:43:37','',NULL,NULL),(26,34,18,32,'choice','遇到学习困难时，你通常的反应是？','{\"A\": \"积极寻求帮助\", \"B\": \"拖延回避\", \"C\": \"情绪低落\", \"D\": \"继续坚持\"}',NULL,0,'B',NULL,0,'','2026-03-13 20:43:37','',NULL,NULL),(27,34,18,33,'choice','在过去两周，你是否经常感到情绪低落或无助？','{\"A\": \"从不\", \"B\": \"有时\", \"C\": \"经常\", \"D\": \"几乎每天\"}',NULL,0,'C',NULL,0,'','2026-03-13 20:43:37','',NULL,NULL),(28,34,18,34,'choice','当你遇到不顺心的事，你是否会出现明显的焦虑或紧张？','{\"A\": \"完全不会\", \"B\": \"偶尔\", \"C\": \"经常\", \"D\": \"非常频繁\"}',NULL,0,'D',NULL,0,'','2026-03-13 20:43:37','',NULL,NULL),(29,34,18,35,'choice','你更倾向于独立完成任务还是团队协作？','{\"A\": \"独立完成\", \"B\": \"团队协作\", \"C\": \"视情况而定\", \"D\": \"没有特别倾向\"}',NULL,0,'D',NULL,0,'','2026-03-13 20:43:37','',NULL,NULL),(30,34,18,36,'short_answer','你最近有什么压力吗？',NULL,NULL,0,'我最近压力好大，我想自杀，我想去死啊，为啥压力这么大啊啊啊啊',NULL,0,'','2026-03-13 20:43:37','',NULL,NULL),(31,35,19,37,'choice','面对即将到来的就业面试，你通常会感到：','{\"A\": \"轻松自信，认为自己已做好充分准备\", \"B\": \"略有紧张，但能通过深呼吸或复习缓解\", \"C\": \"明显焦虑，常失眠、注意力不集中，影响日常学习\", \"D\": \"极度恐惧，回避投递简历或取消面试安排\"}',NULL,0,'D',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(32,35,19,38,'choice','当面试结果未如期公布时，你的典型反应是：','{\"A\": \"耐心等待，同时继续推进其他求职计划\", \"B\": \"稍感不安，会主动联系HR确认进度\", \"C\": \"反复猜测失败原因，自我否定感增强\", \"D\": \"陷入持续低落，质疑自身价值与职业前景\"}',NULL,0,'D',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(33,35,19,39,'choice','在模拟面试或小组讨论中，你如何应对他人提出的尖锐问题或不同意见？','{\"A\": \"冷静倾听，理性回应，视其为提升机会\", \"B\": \"略感压力，但能组织语言完成表达\", \"C\": \"容易情绪波动，事后反复纠结对方评价\", \"D\": \"本能回避发言，或提前退出类似活动\"}',NULL,0,'D',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(34,35,19,40,'choice','你如何看待‘面试失败=个人能力不足’这一观点？','{\"A\": \"完全不认同，认为面试结果受多重因素影响\", \"B\": \"部分认同，但也意识到岗位匹配度和临场状态的作用\", \"C\": \"基本认同，常将一次失利归因为自身缺陷\", \"D\": \"强烈认同，失败后长时间丧失求职动力\"}',NULL,0,'D',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(35,35,19,41,'choice','为应对就业面试压力，你最常采用的调节方式是：','{\"A\": \"制定分阶段准备计划，并与朋辈开展互助演练\", \"B\": \"适度运动、听音乐或短暂放松，保持作息规律\", \"C\": \"靠熬夜刷题或反复修改简历来缓解焦虑感\", \"D\": \"回避准备、拖延投递，或用暴食/ binge-watching 等行为麻痹自己\"}',NULL,0,'D',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(36,35,19,42,'short_answer','请回顾最近一次面试准备或实际经历，描述一个你曾感到‘真实做自己’的瞬间。当时发生了什么？你注意到了自己哪些未曾被平时关注的情绪、需求或优势？',NULL,NULL,0,'失败了，想死',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(37,35,19,43,'short_answer','如果可以给三个月前正在为面试焦虑的自己写一段话，你会强调什么？这段话是否也适用于现在的你？为什么？',NULL,NULL,0,'不管了',NULL,0,'','2026-03-21 09:25:57','',NULL,NULL),(38,36,20,44,'choice','面对即将到来的毕业与求职，你通常会感到：','{\"A\": \"充满期待和动力，已制定清晰的求职计划\", \"B\": \"有些紧张但总体可控，会主动了解招聘信息并尝试投递\", \"C\": \"经常感到焦虑、失眠或难以集中精力学习，担心找不到合适工作\", \"D\": \"持续感到绝望、无力，甚至怀疑自身价值，回避所有求职相关事务\"}',NULL,0,'D',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL),(39,36,20,45,'choice','当收到简历石沉大海或面试被拒时，你的典型反应是：','{\"A\": \"冷静复盘原因，调整策略后继续投递\", \"B\": \"短暂低落，但1–2天内能自我调节并寻求朋友/老师建议\", \"C\": \"反复自责，质疑自己能力，较长时间情绪低落且影响日常学习\", \"D\": \"产生强烈羞耻感，回避社交与求职信息，出现躯体不适（如心悸、食欲骤降）\"}',NULL,0,'D',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL),(40,36,20,46,'choice','在与同学讨论就业去向（如考公、考研、出国、就业）时，你常有的心理状态是：','{\"A\": \"尊重彼此选择，能坦然分享自己的规划与困惑\", \"B\": \"略有比较心理，但能理性看待差异，不因此动摇自我判断\", \"C\": \"频繁感到焦虑或自卑，担心落后于同龄人，反复追问‘我是不是选错了？’\", \"D\": \"因他人进展而持续性自我否定，出现逃避聚会、屏蔽社交平台相关信息等行为\"}',NULL,0,'C',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL),(41,36,20,47,'choice','你如何看待‘第一份工作是否决定人生轨迹’这一说法？','{\"A\": \"认为职业发展是动态过程，首份工作是起点而非终点\", \"B\": \"承认其重要性，但相信可通过后续学习与调整改变方向\", \"C\": \"高度认同该说法，因此对求职结果过度苛求，难以接受‘备选方案’\", \"D\": \"将其视为不可逆的人生判决，一旦不如意便陷入长期抑郁或存在意义危机\"}',NULL,0,'C',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL),(42,36,20,48,'choice','当你需要为求职做准备（如写简历、模拟面试、考公复习）时，你的行动状态通常是：','{\"A\": \"目标明确、时间管理良好，能平衡备考与日常生活\", \"B\": \"有时拖延但可自我督促完成，偶尔需外部提醒\", \"C\": \"长期拖延、启动困难，常以‘还没准备好’为由回避任务\", \"D\": \"完全无法开始，伴随强烈生理疲惫感或解离体验，持续两周以上无实质性准备行为\"}',NULL,0,'D',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL),(43,36,20,49,'short_answer','请描述最近一次因就业压力引发情绪波动的具体情境。当时你的想法、身体感受、行为反应分别是什么？这一经历是否改变了你对‘职业成功’或‘自我价值’的理解？',NULL,NULL,0,'面试压力好大，我想死',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL),(44,36,20,50,'short_answer','如果可以给半年前正在焦虑求职的自己写一封信，你会写什么？这封信中，哪些话是你现在才真正理解、但当时最需要听到的？为什么？',NULL,NULL,0,'压力大。焦虑',NULL,0,'','2026-03-21 10:35:38','',NULL,NULL);
/*!40000 ALTER TABLE `questionnaire_answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommend_article`
--

DROP TABLE IF EXISTS `recommend_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recommend_article` (
  `article_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` varchar(100) NOT NULL COMMENT '文章标题',
  `content` text NOT NULL COMMENT '文章内容（Markdown格式）',
  `summary` varchar(500) DEFAULT NULL COMMENT '文章摘要',
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `read_count` int DEFAULT '0' COMMENT '阅读量',
  `category` varchar(50) DEFAULT NULL COMMENT '文章分类',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1下架）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`article_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理文章推荐表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommend_article`
--

LOCK TABLES `recommend_article` WRITE;
/*!40000 ALTER TABLE `recommend_article` DISABLE KEYS */;
INSERT INTO `recommend_article` VALUES (3,'在亲密关系里，允许自己 “不完美” 才是真正的成熟','# 在亲密关系里，允许自己“不完美”才是真正的成熟\n\n我们都曾对爱情有过美好的想象：希望找到一个“完美”的人，谈一场“完美”的恋爱，永远不吵架、不失望、不受伤。但现实是，每段亲密关系都会遇到摩擦，每个伴侣都有缺点，就连我们自己也常常犯错。\n\n## 一、“完美主义”正在悄悄消耗你的感情\n很多人在关系里习惯扮演“满分选手”：\n- 不敢表达真实的需求，怕被对方觉得“麻烦”\n- 努力维持“懂事”的形象，哪怕自己已经委屈\n- 对伴侣的小错误过度敏感，总想着“他应该做得更好”\n\n这种“完美滤镜”不仅让你活得很累，也会让对方感到巨大的压力。当你无法接受关系中的不完美时，小矛盾很容易升级成大问题，最终把彼此越推越远。\n\n## 二、真正的亲密，从“暴露脆弱”开始\n好的爱情不是没有争吵，而是争吵后依然愿意拥抱；不是从不犯错，而是犯错后依然选择信任。\n- **允许自己不完美**：承认自己会嫉妒、会偷懒、会偶尔情绪化，这些真实的情绪并不会破坏关系，反而能让对方更懂你。\n- **允许对方不完美**：接受他有丢三落四的小毛病，也有偶尔的沉默和逃避，没有人天生就会爱，大家都在学习中成长。\n\n## 三、用“成长型思维”经营关系\n成熟的亲密关系，不是“找到对的人”，而是“把彼此变成对的人”。\n1. **放下执念**：停止用“应该”来要求对方，多问“我们可以一起做些什么”。\n2. **小步修复**：每次矛盾后，不用急着“翻篇”，而是一起复盘：“刚才我那样说让你不舒服了，下次我们可以试试这样沟通……”\n3. **欣赏不完美**：试着把对方的“缺点”看作“特点”，比如他的“慢半拍”或许正是他的稳重，你的“碎碎念”或许正是你的温柔。\n\n## 结语\n爱情最动人的样子，不是两个完美的人在一起，而是两个不完美的人，愿意为了彼此变得更完整。当你不再执着于“完美关系”，反而能在真实的互动里，收获更踏实的幸福。','本文探讨了爱情中 “完美主义” 的隐形伤害，分享了如何放下对 “理想伴侣” 和 “完美关系” 的执念，通过接纳真实的彼此，建立更松弛、更长久的亲密关系。','豆包心理',465,'爱情','0','','2025-11-09 16:45:59','','2026-03-18 19:47:32',NULL),(4,'虚假自体：为什么你得到了一切，却依然不快乐？','# 虚假自体：为什么你得到了一切，却依然不快乐？\n\n你是否有过这样的体验：事业顺遂、生活安稳，身边的人都觉得你“人生圆满”，但你内心却始终空落落的，感受不到真正的快乐与满足？这种“拥有一切却不快乐”的矛盾状态，在心理学中被称为**虚假自体**（False Self）。\n\n## 一、什么是虚假自体？\n英国精神分析学家唐纳德·温尼科特（Donald Winnicott）将自我分为“真实自体”与“虚假自体”：\n- **真实自体**：围绕自身感受、需求与欲望构建，是我们最本真的生命状态，充满自发性与活力。\n- **虚假自体**：为适应外界环境、迎合他人期待而形成的防御性面具，本质是“为别人而活”。\n\n虚假自体的形成往往始于童年：当照料者只在孩子“听话、懂事、符合期待”时才给予关爱，孩子便会逐渐学会压抑真实情绪，用“讨好的表现”换取安全感。长大后，这种模式会延续到工作、人际关系中，让我们活成别人眼中的“完美范本”，却丢失了自我。\n\n## 二、虚假自体的典型表现\n1.  **过度在意他人评价**：做决定时优先考虑“别人会怎么看”，而非“我想要什么”。\n2.  **习惯性讨好**：习惯牺牲自身需求去满足他人，害怕冲突与拒绝。\n3.  **内心空虚感**：即便获得成功与认可，也感受不到持久的快乐，常觉得“活着没意思”。\n4.  **情绪钝化**：难以感知真实的喜怒哀乐，像“戴着面具生活”，与自我疏离。\n\n## 三、如何走出虚假自体，找回真实自我？\n1.  **觉察自我**：记录日常中“为迎合他人而做的选择”，识别虚假自体的触发场景。\n2.  **练习课题分离**：分清“自己的事”与“他人的事”，不必为他人的期待负责。\n3.  **微小行动**：从一件“只满足自己”的小事开始（比如吃喜欢的食物、做想做的事），逐步重建与真实自我的连接。\n4.  **接纳不完美**：允许自己“不够好”，真实的脆弱远比完美的伪装更有力量。\n\n虚假自体不是缺陷，而是我们为了生存而穿上的铠甲。但真正的成长，从来不是活成别人期待的样子，而是勇敢脱下铠甲，拥抱那个真实、鲜活的自己。','本文围绕精神分析学家温尼科特提出的 “虚假自体” 概念展开，剖析了当代人 “拥有一切却内心空虚” 的心理困境。虚假自体是个体为迎合外界期待、获得认可而构建的防御性人格面具，人们在满足他人期待的过程中，逐渐压抑了真实的情感与需求，即便获得了世俗意义上的成功，也因与真实自我疏离而陷入空虚与无意义感。文章旨在帮助读者识别虚假自体的表现，理解其形成根源，并探索回归真实自我、重建内在幸福感的路径。','壹心理',490,'自体心理学','0','','2026-03-18 19:40:17','','2026-03-18 19:47:38',NULL),(5,'总在反复想同一件事？你可能陷入了反刍思维','# 总在反复想同一件事？你可能陷入了反刍思维\n\n你是否常常陷入这种状态：睡前躺在床上，反复复盘白天的一句话、一个举动，甚至放大细节自我否定；明明事情已经过去很久，却控制不住一遍遍回想，越想越焦虑、低落？\n\n这种“总在反复想同一件事”的状态，心理学上称为**反刍思维**（Rumination）——像牛反刍一样，把已经消化的情绪和事件重新翻出来咀嚼，却难以走出困境。\n\n## 一、反刍思维≠正常反思，别混淆\n很多人分不清“反思”与“反刍”，二者核心差异在于目的：\n- **正常反思**：聚焦“解决问题”，比如“这次失误是因为准备不足，下次我要提前规划”，是指向未来的行动，能带来成长。\n- **反刍思维**：聚焦“情绪内耗”，比如“我当时为什么那么说？他是不是讨厌我？”，只停留在过去的痛苦和自责里，没有任何解决办法，只会加重焦虑、抑郁情绪。\n\n## 二、反刍思维的形成根源\n1. **高敏感人格特质**：对他人情绪、环境变化感知敏锐，易过度解读他人言行，陷入自我怀疑。\n2. **完美主义倾向**：无法接受自己“犯错”“不完美”，反复纠结细节，试图通过复盘达到“完美状态”。\n3. **安全感缺失**：童年时期未被充分接纳，成年后通过反复确认来寻求安全感，避免被否定、被抛弃的恐惧。\n\n## 三、反刍思维的危害\n长期陷入反刍思维，不仅会消耗心理能量，还会引发一系列身心问题：\n- 情绪上：持续焦虑、抑郁、自我否定，陷入情绪低谷。\n- 认知上：注意力不集中，记忆力下降，影响工作、学习效率。\n- 生理上：长期精神紧绷易导致失眠、食欲不振、免疫力下降等。\n\n## 四、3个实用方法，跳出反刍思维循环\n1. **“5分钟限定”法**：给自己设定5分钟专门用来“想这件事”，时间一到立刻做其他事（比如喝水、拉伸、听一首轻音乐）。用“主动结束”打破无意义的反复回想。\n2. **“事实vs想法”区分**：拿出一张纸，写下“事实”和“我脑补的想法”。比如事实是“我开会没发言”，想法是“他们肯定觉得我能力差”。用客观事实打破脑补的负面循环。\n3. **“当下锚定”练习**：当发现自己开始反刍时，立刻做**5-4-3-2-1感官练习**——说出5个看到的东西、4个摸到的东西、3个听到的声音、2个闻到的气味、1个尝到的味道。快速把注意力拉回当下，切断反刍的思绪链条。\n\n反刍思维不是“矫情”，而是心理发出的“信号”——它在提醒你：你需要停下来，照顾自己的情绪，停止无意义的内耗。真正的解脱，从来不是强迫自己“不想”，而是学会放下对过去的执念，活在当下。','本文揭示了 “反复回想同一件事” 的心理本质为反刍思维，区分正常反思与过度反刍的差异，剖析其形成根源与对情绪、生活的负面影响，并提供可落地的心理调节方法，帮助读者快速跳出思维反刍循环，减少内耗，回归当下。','心理咨询师',222,'自我疗愈','0','','2026-03-18 19:41:57','','2026-03-18 19:47:32',NULL),(6,'讨好型人格：为什么你总是不敢说 “不”？','# 讨好型人格：为什么你总是不敢说“不”？\n\n你是否总是习惯优先满足别人的需求，却忽略自己的感受？明明不想答应，却难以开口拒绝；害怕冲突，习惯用“讨好”换取他人的认可？这可能是**讨好型人格**在悄悄影响你。\n\n## 一、讨好型人格的典型表现\n- 习惯性说“好的”“没问题”，哪怕自己很累、很为难。\n- 害怕让别人失望，习惯隐藏真实情绪，不敢表达不满。\n- 过度在意他人评价，将自我价值感建立在“被别人喜欢”上。\n- 拒绝别人后会产生强烈的愧疚感，甚至主动补偿对方。\n\n## 二、讨好型人格的形成根源\n讨好型人格往往源于童年时期的“有条件的爱”：只有当孩子听话、懂事、满足大人期待时，才能获得关爱与认可。长大后，这种模式被延续，让人误以为“只有讨好别人，才能被爱、被接纳”。\n\n## 三、如何摆脱讨好型人格，找回自我？\n1.  **练习“温和拒绝”**：从微小的拒绝开始，比如“我现在不方便，下次吧”，不必为拒绝找过多借口。\n2.  **明确自我边界**：列出“我可以接受”和“我不能接受”的事，清晰划分自己的底线。\n3.  **重建价值感**：告诉自己“我的价值不需要通过讨好别人来证明”，关注自身感受与需求。\n4.  **允许关系有冲突**：健康的关系允许不同意见，真正的朋友不会因为你的一次拒绝而离开。\n\n讨好不是善良，而是一种自我消耗。学会爱自己，才是建立健康关系的起点。','本文深入剖析讨好型人格的核心特征与形成根源，拆解 “习惯性讨好” 背后的恐惧与自我价值感缺失，提供清晰可操作的边界建立方法，帮助读者学会拒绝、尊重自我需求，重建健康的人际关系。','林晓晨',885,'人格心理学','0','','2026-03-18 19:44:34','','2026-03-18 19:47:32',NULL),(7,'原生家庭：如何走出童年的心理阴影？','# 原生家庭：如何走出童年的心理阴影？\n\n“我性格里的敏感、自卑，都来自原生家庭”“我害怕亲密关系，因为见过父母失败的婚姻”……越来越多人开始意识到：原生家庭，是我们人生最初的底色，也可能是一生都要面对的课题。\n\n## 一、原生家庭如何影响我们？\n原生家庭的影响，藏在我们的思维模式、情绪反应与人际关系里：\n- **安全感缺失**：童年未被充分照顾的人，成年后易焦虑、敏感，害怕被抛弃。\n- **亲密关系模式**：会不自觉复制父母的相处模式，要么过度依赖，要么刻意疏离。\n- **自我认知偏差**：长期被否定的孩子，容易形成“我不够好”的核心信念。\n\n## 二、走出原生家庭的关键：不是原谅，而是看见\n很多人误以为“走出原生家庭”就是“原谅父母”，但真正的疗愈始于“看见”：\n1.  **看见创伤**：承认“我的痛苦是真实存在的”，不必强迫自己“释怀”。\n2.  **区分“过去”与“现在”**：告诉自己“我已经长大了，有能力保护自己，不再是那个无助的孩子”。\n3.  **停止代际传递**：意识到“我不必成为父母的翻版”，主动选择更健康的生活方式。\n\n## 三、3步自我疗愈法\n1.  **书写疗愈**：写下童年的委屈与愤怒，让情绪得到释放。\n2.  **建立新的客体关系**：在安全的友谊、恋爱中，体验被接纳、被爱的感觉，修正旧的认知。\n3.  **成为自己的“理想父母”**：学会照顾自己的情绪与需求，给自己足够的安全感与肯定。\n\n原生家庭是我们的起点，但绝不是终点。你有权利，也有能力，活出属于自己的人生。','本文解读原生家庭对人格、亲密关系的深远影响，区分 “原生家庭创伤” 与 “正常家庭影响”，提供接纳过去、自我疗愈的实用步骤，帮助读者摆脱原生家庭的束缚，活出独立的人生。','陈屿',666,' 自我成长','0','','2026-03-18 19:44:55','','2026-03-18 19:47:32',NULL),(8,'社交恐惧：害怕社交不是你的错','# 社交恐惧：害怕社交不是你的错\n\n“一想到要和陌生人说话就心跳加速”“聚会时总想躲在角落，害怕成为焦点”“发消息前要反复修改，怕说错话”——你是否也被这些“社交恐惧”的瞬间困扰？\n\n## 一、社恐≠内向，别再混淆了\n- **内向**：是一种性格偏好，喜欢独处，社交后需要充电，但不害怕社交。\n- **社交恐惧**：是一种焦虑障碍，核心是“害怕被评价、被否定”，会主动回避社交场景，甚至影响正常生活。\n\n## 二、社交恐惧的根源：认知偏差\n社交恐惧的本质，是我们对“社交结果”的灾难化想象：\n- 过度关注自己的表现，觉得“所有人都在盯着我看”。\n- 预设“我一定会搞砸，别人会讨厌我”，放大负面可能性。\n- 将“一次社交失误”等同于“我这个人很失败”。\n\n## 三、克服社交恐惧的实用技巧\n1.  **认知重构**：写下“我害怕的事”和“实际发生的概率”，比如“我怕说错话”，实际概率远低于想象。\n2.  **逐级暴露法**：从简单的社交任务开始（比如和店员打招呼），逐步挑战更难的场景，积累成功经验。\n3.  **注意力转移**：社交时将注意力从“自己”转移到“对方”，比如认真听对方说话，减少自我关注。\n4.  **接纳不完美**：告诉自己“没有人会记得你的小失误，大家更关注自己”。\n\n社交恐惧不是缺陷，而是你内心细腻、在意他人感受的证明。慢慢来，你不需要成为“社交达人”，只需要找到让自己舒服的人际距离。','本文科普社交恐惧的心理学定义，区分 “社恐” 与 “内向” 的差异，分析社交恐惧的认知根源，提供暴露疗法、认知重构等实用技巧，帮助读者逐步克服社交焦虑，从容面对人际场景。','苏晚',789,'社交焦虑','0','','2026-03-18 19:45:22','','2026-03-21 08:26:34',NULL),(9,'拖延症：不是懒，是你害怕开始','# 拖延症：不是懒，是你害怕开始\n\n“ deadline 是第一生产力”“明明知道要做，却刷着手机直到最后一刻”——拖延，是当代人最普遍的“顽疾”之一。但你知道吗？拖延，从来不是因为“懒”。\n\n## 一、拖延症的真实面目\n心理学研究发现，拖延的核心是**情绪调节失败**，而非时间管理问题：\n- **完美主义拖延**：害怕“做得不够好”，宁愿不开始，也不愿面对“不完美”的结果。\n- **恐惧失败拖延**：将“任务失败”等同于“我这个人失败”，用拖延逃避可能的挫败感。\n- **注意力分散拖延**：面对复杂任务时，大脑本能选择刷手机等轻松行为，逃避压力。\n\n## 二、告别拖延：从“最小行动”开始\n1.  **5分钟启动法**：告诉自己“我只做5分钟”，一旦开始，你会发现继续下去并不难。\n2.  **任务拆解法**：把大任务拆成极小的步骤，比如“写报告”拆成“打开文档”“写标题”“写第一段”，降低行动门槛。\n3.  **番茄工作法**：工作25分钟，休息5分钟，用短暂的专注对抗注意力分散。\n4.  **接纳“不完美”**：告诉自己“先完成，再完美”，完成比完美更重要。\n\n拖延不是你的错，也不是无法改变的习惯。从今天起，试着迈出第一步，你会发现：行动，才是治愈焦虑的最好良药。','本文拆解拖延症的心理本质，揭示 “拖延≠懒惰”，分析完美主义、恐惧失败、注意力分散等核心成因，提供番茄工作法、任务拆解等高效方法，帮助读者告别拖延，提升行动力。','周明远',366,'时间管理','0','','2026-03-18 19:45:52','','2026-04-11 00:09:22',NULL),(10,'反刍思维：别让反复回想困住你','# 反刍思维：别让反复回想困住你\n\n你是否常常躺在床上，一遍遍复盘白天的一句话、一个举动，越想越自责？明明事情已经过去，却控制不住翻来覆去地想，陷入焦虑与低落？这种状态，在心理学中被称为**反刍思维**。\n\n## 一、反刍思维≠反思，别再自我消耗\n- **反思**：聚焦“解决问题”，比如“这次失误是因为准备不足，下次要提前规划”，指向成长。\n- **反刍思维**：聚焦“情绪纠缠”，比如“我当时为什么那么做？他一定讨厌我了”，只停留在痛苦里，没有任何行动。\n\n## 二、反刍思维从何而来？\n- 高敏感人格：习惯过度解读他人言行，容易陷入自我怀疑。\n- 完美主义：无法接受“不完美”，反复纠结细节，试图通过复盘弥补遗憾。\n- 安全感缺失：通过反复回想确认“安全”，避免被否定、被抛弃的恐惧。\n\n## 三、3个方法，跳出反刍循环\n1.  **5分钟限定法**：给自己5分钟专门“想这件事”，时间一到立刻做其他事，主动切断思绪。\n2.  **事实vs想法区分**：写下“客观事实”和“我的脑补”，比如事实是“我没发言”，想法是“大家觉得我没用”，用事实打破负面想象。\n3.  **感官锚定练习**：当反刍开始时，说出5个看到的、4个摸到的、3个听到的事物，快速把注意力拉回当下。\n\n反刍思维不是你的错，而是心理在提醒你：该停下来照顾自己了。放下对过去的执念，才能轻装走向未来。','本文揭示 “反复回想同一件事” 的本质是反刍思维，区分正常反思与过度反刍，剖析其对情绪的消耗，并提供 5 分钟限定、感官锚定等实用技巧，帮助读者跳出思维内耗，回归当下。','许知意',568,'自我疗愈','0','','2026-03-18 19:46:14','','2026-03-26 00:20:28',NULL);
/*!40000 ALTER TABLE `recommend_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommend_course`
--

DROP TABLE IF EXISTS `recommend_course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recommend_course` (
  `course_id` bigint NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `title` varchar(100) NOT NULL COMMENT '课程标题',
  `mp4_url` varchar(255) NOT NULL COMMENT '视频文件链接',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面图链接',
  `duration` int DEFAULT NULL COMMENT '视频时长（秒）',
  `description` varchar(500) DEFAULT NULL COMMENT '课程简介',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1下架）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`course_id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理课程推荐表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommend_course`
--

LOCK TABLES `recommend_course` WRITE;
/*!40000 ALTER TABLE `recommend_course` DISABLE KEYS */;
INSERT INTO `recommend_course` VALUES (1,'抑郁了，也没关系','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418305601640349_1773832038119_7755.mp4','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418305607554345_1773832043033_7706.jpeg',274,'课程以温和治愈的视角，打破对抑郁症的误解与 stigma，告诉大家：抑郁并非 “矫情” 或 “软弱”，而是一种可以被理解、被照顾的情绪状态。它用通俗易懂的语言，讲解抑郁情绪的常见表现、自我觉察方法，以及温和的调节方式 —— 包括接纳情绪、寻求支持、微小行动等可落地的心理技巧，传递 “不必强迫自己立刻好起来，允许自己慢慢疗愈” 的温柔力量，帮助观众建立对抑郁的理性认知，学会与负面情绪共处，为心灵找到喘息的出口。','0','admin','2025-11-08 20:19:08','','2026-03-18 19:08:31',NULL),(2,'害！这烂泥一样的人生','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/ddffa462-abff-4c90-ac01-07569137a8f0.0.4b0eeab4-929d-483f-843c-8e4b742a5ba9_1773833146072_9742.mp4','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418306327677302_1773833158543_0754.jpeg',253,'课程用接地气的视角，直面 “人生像烂泥一样无力、停滞” 的挫败感，拆解这种情绪背后的自我否定与焦虑。它不灌鸡汤，而是引导观众接纳当下的 “不完美” 与 “停滞感”，告诉大家：烂泥也有扎根的力量，暂时的低谷不是终点，而是重新生长的契机。通过温和的心理引导，帮助观众放下对 “成功” 的执念，学会与不完美的自己和解，在看似泥泞的生活里找到重新出发的微光，传递 “允许自己烂泥一阵子，才能慢慢长出向上的根” 的治愈力量。','0','admin','2025-11-08 20:19:08','','2026-03-18 19:34:28',NULL),(3,'放过自己，停止内耗','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/c1be50db-9b7a-49cb-9ab1-9f235a1c0325.0.6e043581-3949-4806-91d1-53d3529e9aa2_1773833398442_8666.mp4','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418306923684477_1773833403067_0284.jpeg',73,'精准戳中当代人 “反复纠结、自我消耗” 的痛点，用直白易懂的语言，拆解内耗的本质 —— 过度在意他人评价、反复复盘过去、过度担忧未来。它分享了 “课题分离”“5 分钟行动法” 等可落地的止耗技巧，引导观众学会区分 “自己的事” 与 “他人的事”，放下完美主义，用微小行动打断内耗循环，传递 “不必逼自己面面俱到，放过自己才是成长的开始” 的治愈理念，帮助观众快速从精神内耗中抽离，找回内心的平静与力量。','0','admin','2025-11-08 20:19:08','','2026-03-18 19:34:43',NULL),(4,'突破自我向上的力量','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418306155044349_1773832635354_5406.mp4','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418306159899843_1773832651147_6343.jpeg',297,'课程从自我认知、目标拆解、行动突破三个维度出发，引导观众直面内心的恐惧与自我设限，挖掘潜藏的成长潜能。它通过可落地的心理方法，帮助观众建立积极的自我信念，学会在困境中保持韧性，将 “想改变” 的念头转化为 “能行动” 的力量，最终实现自我突破与向上生长。课程传递 “成长从来不是一蹴而就，每一步微小的前进都在积蓄向上的力量” 这一核心信念，为渴望进步的观众提供温暖而坚定的精神支撑。','0','admin','2025-11-08 20:19:08','','2026-03-18 19:34:43',NULL),(5,'压力之下，让情绪找到出口','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418305748669057_1773832589566_5645.mp4','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/13418305752516130_1773832581511_1217.jpeg',73,'课程聚焦现代生活中普遍的压力场景，用简洁高效的方式，分享实用的情绪释放技巧：从呼吸调节、正念觉察到合理宣泄，帮助观众快速识别压力信号，学会不压抑、不内耗，为紧绷的情绪找到安全的释放通道。它传递 “情绪无需隐藏，疏导才是解药” 的理念，让观众在短时间内掌握缓解焦虑、平复情绪的可操作方法，是适合随时观看的情绪急救小课堂。','0','admin','2025-11-08 20:19:08','','2026-03-18 19:34:43',NULL);
/*!40000 ALTER TABLE `recommend_course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommend_music`
--

DROP TABLE IF EXISTS `recommend_music`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recommend_music` (
  `music_id` bigint NOT NULL AUTO_INCREMENT COMMENT '音乐ID',
  `title` varchar(100) NOT NULL COMMENT '音乐标题',
  `mp3_url` varchar(255) NOT NULL COMMENT '音频文件链接',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '封面图链接',
  `artist` varchar(64) DEFAULT NULL COMMENT '演唱者/作者',
  `genre` varchar(50) DEFAULT NULL COMMENT '音乐风格',
  `duration` int DEFAULT NULL COMMENT '时长（秒）',
  `description` varchar(500) DEFAULT NULL COMMENT '音乐简介',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1下架）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`music_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='心理音乐推荐表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommend_music`
--

LOCK TABLES `recommend_music` WRITE;
/*!40000 ALTER TABLE `recommend_music` DISABLE KEYS */;
INSERT INTO `recommend_music` VALUES (5,'游京','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/M800001Q3Jex2UR8Ej_1773821126863_8894.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/886207628_1773821156004_4020.jpg','海伦','古典',214,'以第一人称的游历视角展开，旋律悠扬婉转，融合了细腻的戏腔元素，将古代京城的繁华景象与人文气息描绘得淋漓尽致。歌词中 “我走在长街中，听戏子唱京城”“朗朗夜色星空，望孩童放花灯” 等句子，生动勾勒出京城长街的市井烟火、古城的雅致韵味，以及孩童放花灯的浪漫场景，既有对繁华盛景的描摹，也暗含着对京城风物的深深眷恋与洒脱的游者情怀。','0','admin','2026-03-18 16:07:03','','2026-03-18 16:07:02',NULL),(6,'New Boy','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/1272951169_1773821322567_1684.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/1175442648_1773821318461_9812.jpg','房东的猫','民谣、治愈系、流行民谣',252,'以轻快清新的民谣曲风为基底，搭配房东的猫极具辨识度的温柔嗓音，传递出一种明媚又治愈的力量。歌词以少年的视角出发，用 “穿新衣吧，剪新发型呀，轻松一下，Windows 98” 等充满时代感的歌词，勾勒出对新生活的憧憬与向往，既有青春的懵懂鲜活，也藏着对未来的热忱与期许。','0','admin','2026-03-18 16:10:20','','2026-03-18 16:14:28',NULL),(7,'在你的身边','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/M800001sfoTk1wnSnC_1773821954659_5587.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/3115970446_1773821975871_7544.jpg','盛哲','流行、节奏流行、R&B、电子',262,'歌曲创作灵感源于盛哲的异国恋经历，以细腻笔触描绘了爱情里的思念、不舍与对陪伴的渴望。旋律融合了华语流行的温柔细腻与欧美流行的能量感，搭配电子与 R&B 元素，塑造出明朗饱满的节奏流行风格。温暖的声线与抓耳的旋律交织，既传递出对过往爱情的怀念，也藏着 “想在你的身边” 的纯粹心愿，是一首极具治愈感与共鸣力的抒情作品。','0','admin','2026-03-18 16:20:31','','2026-03-18 16:20:30',NULL),(8,'如果我们不曾相遇','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/M500004DDIUn0M5OPI_1773822144234_5361.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/3826466451_1773822147599_0975.jpg','五月天','华语流行、摇滚、抒情摇滚',201,'这首歌以 “相遇” 为核心主题，用诗意的歌词探讨了生命中偶然与必然的联结，既饱含对过往相遇的感恩，也藏着对未来重逢的期许。旋律从温柔的吉他铺垫逐步递进至激昂的摇滚副歌，五月天标志性的热血唱腔与细腻情感交织，将 “如果我们不曾相遇，我会是在哪里” 的追问与释然，转化为充满力量的生命告白，是一首兼具青春感与人生厚度的抒情摇滚代表作。','0','admin','2026-03-18 16:23:21','','2026-03-18 16:23:21',NULL),(9,'嘉禾望岗','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/M800004KWVDU3tkLA0_1773822246711_8027.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/1927528050_1773822250221_4652.jpg','海来阿木','民谣、流行民谣、治愈系',234,'歌名取自广州地铁嘉禾望岗站，这座站点因承载着无数年轻人的离别与奔赴，被赋予了 “青春分岔口” 的意象。歌曲以温柔的民谣旋律为基底，搭配海来阿木极具故事感的嗓音，将车站里的离别、遗憾与对未来的期许娓娓道来。歌词里 “嘉禾望岗的风，吹不散我的梦” 等句子，细腻刻画了异乡漂泊的迷茫与坚守，让车站成为青春与乡愁的具象载体，引发了大量听众的情感共鸣，是一首充满烟火气与治愈感的都市民谣作品。','0','admin','2026-03-18 16:24:49','','2026-03-18 16:24:48',NULL),(10,'简简单单安静做自己','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/M800003gqmWw1OXZS4_1773822349820_0134.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/3367201884_1773822345795_0450.jpg','烟嗓兄弟','流行、民谣、治愈系',239,'歌曲以质朴的民谣旋律为基底，搭配烟嗓兄弟极具沧桑感的嗓音，传递出一种返璞归真的生活态度。歌词围绕 “简单” 与 “自我” 展开，用直白的文字劝慰听众放下外界的纷扰与焦虑，在喧嚣中守住内心的平静，专注于做真实的自己。旋律舒缓治愈，没有华丽的编曲，却以最纯粹的情感直击人心，适合在独处时聆听，让人在歌声中获得内心的松弛与力量，是一首充满生活智慧的治愈系作品。','0','admin','2026-03-18 16:26:23','','2026-03-18 16:26:22',NULL),(11,'いつも何度でも','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/M800004HjpOw0gDLLb_1773822772073_4171.mp3','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/upload/151651826_1773822768579_7877.jpg','宗次郎','治愈系、纯音乐、陶笛演奏、日式轻音乐',198,'宗次郎以陶笛为核心乐器，将原曲的温柔与治愈感演绎得淋漓尽致。旋律清澈悠扬，如林间微风般舒缓，既保留了原作对 “成长与重逢” 的情感内核，又通过陶笛独特的音色，赋予了作品更纯粹的自然气息与东方禅意。它传递出 “无论经历多少次挫折，内心的光芒永远同在” 的治愈力量，是适合静心聆听、抚慰心灵的经典轻音乐作品。','0','admin','2026-03-18 16:33:57','','2026-03-18 16:33:56',NULL);
/*!40000 ALTER TABLE `recommend_music` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student_info`
--

DROP TABLE IF EXISTS `student_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student_info` (
  `student_id` bigint NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `student_no` varchar(50) DEFAULT NULL COMMENT '学号',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `gender` char(1) DEFAULT NULL COMMENT '性别（0/1）',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级',
  `major` varchar(50) DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) DEFAULT NULL COMMENT '班级',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1异常）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student_info`
--

LOCK TABLES `student_info` WRITE;
/*!40000 ALTER TABLE `student_info` DISABLE KEYS */;
INSERT INTO `student_info` VALUES (9,100,'2212040124','学生001','0','22级','软件工程','一班','13888886666','0','','2026-03-13 19:28:58','',NULL,NULL);
/*!40000 ALTER TABLE `student_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2025-09-15 19:23:23','admin','2025-09-26 11:38:13','蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2025-09-15 19:23:23','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2025-09-15 19:23:23','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','true','Y','admin','2025-09-15 19:23:23','',NULL,'是否开启验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2025-09-15 19:23:23','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2025-09-15 19:23:23','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2025-09-15 19:23:23','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2025-09-15 19:23:23','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=204 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','张家界学院',0,'蒋才渡','15888888888','caidu@qq.com','0','0','admin','2025-09-15 19:23:22','admin','2025-09-22 16:46:27'),(101,100,'0,100','理工农学院',1,'李四','15888888888','caidu@qq.com','0','0','admin','2025-09-15 19:23:22','admin','2026-03-12 10:09:47'),(102,100,'0,100','长沙分公司',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(103,101,'0,100,101','软件工程',1,'李四','17374668924','2364728886@qq.com','0','0','admin','2025-09-15 19:23:22','admin','2026-03-13 19:29:39'),(104,101,'0,100,101','市场部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(105,101,'0,100,101','测试部门',3,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(106,101,'0,100,101','财务部门',4,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(107,101,'0,100,101','运维部门',5,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(108,102,'0,100,102','市场部门',1,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(109,102,'0,100,102','财务部门',2,'若依','15888888888','ry@qq.com','0','2','admin','2025-09-15 19:23:22','',NULL),(200,103,'0,100,101,103','一班',0,NULL,NULL,NULL,'0','2','admin','2025-09-24 19:40:40','',NULL),(201,103,'0,100,101,103','软工一班',1,'李四','17374668924','2364728886@qq.com','0','0','admin','2025-09-24 19:42:19','admin','2026-03-12 10:09:48'),(202,103,'0,100,101,103','软工二班',2,NULL,NULL,NULL,'0','0','admin','2025-09-24 19:42:46','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2025-09-15 19:23:23','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2025-09-15 19:23:23','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2025-09-15 19:23:23','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2025-09-15 19:23:23','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2025-09-15 19:23:23','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2025-09-15 19:23:23','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2025-09-15 19:23:23','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2025-09-15 19:23:23','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2025-09-15 19:23:23','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2025-09-15 19:23:23','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2025-09-15 19:23:23','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2025-09-15 19:23:23','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2025-09-15 19:23:23','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2025-09-15 19:23:23','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2025-09-15 19:23:23','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2025-09-15 19:23:23','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2025-09-15 19:23:23','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2025-09-15 19:23:23','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2025-09-15 19:23:23','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2025-09-15 19:23:23','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2025-09-15 19:23:23','',NULL,'停用状态'),(100,0,'正常','0','student_status',NULL,'success','N','0','admin','2025-09-21 12:00:21','',NULL,NULL),(101,1,'异常','1','student_status',NULL,'danger','N','0','admin','2025-09-21 12:00:52','admin','2025-09-21 22:13:26',NULL),(102,0,'正常','0','questionnaire_status',NULL,'success','N','0','admin','2025-09-23 08:57:20','',NULL,'问卷正常状态'),(103,0,'下架','1','questionnaire_status',NULL,'danger','N','0','admin','2025-09-23 08:58:00','',NULL,'问卷下架状态'),(104,0,'常规测评','0','questionnaire_type',NULL,'primary','N','0','admin','2025-09-23 09:00:10','admin','2025-09-25 19:01:11','问卷类型-常规测评'),(105,1,'临时测评','1','questionnaire_type',NULL,'primary','N','0','admin','2025-09-23 09:00:45','admin','2025-09-25 19:01:13','问卷类型-临时测评'),(106,2,'专项测评','2','questionnaire_type',NULL,'primary','N','0','admin','2025-09-23 09:01:30','admin','2025-09-25 19:01:17','问卷类型-专项测评'),(107,3,'学业压力','3','questionnaire_type',NULL,'primary','N','0','admin','2025-09-23 09:01:55','admin','2025-09-25 19:01:20','问卷类型-学业压力'),(108,4,'情绪状态','4','questionnaire_type',NULL,'primary','N','0','admin','2025-09-23 09:02:19','admin','2025-09-25 19:01:23','问卷类型-情绪状态'),(109,5,'人格特质','5','questionnaire_type',NULL,'primary','N','0','admin','2025-09-23 09:02:44','admin','2025-09-25 19:01:26','问卷类型-人格特质'),(110,0,'选择题','choice','question_type',NULL,'primary','N','0','admin','2025-09-23 18:28:15','admin','2025-09-25 19:00:41','问卷题目-选择题'),(111,1,'简答题','short_answer','question_type',NULL,'primary','N','0','admin','2025-09-23 18:29:02','admin','2025-09-25 19:00:45','问卷题目-简答题'),(112,0,'系统用户','00','sys_user_type',NULL,'primary','N','0','admin','2025-09-24 16:01:17','admin','2025-09-25 19:02:39','系统用户-00'),(113,1,'学生用户','01','sys_user_type',NULL,'primary','N','0','admin','2025-09-24 16:01:54','admin','2025-09-25 19:02:44','学生用户-01'),(114,2,'辅导员用户','02','sys_user_type',NULL,'primary','N','0','admin','2025-09-24 16:02:24','admin','2025-09-25 19:02:49','辅导员用户-02'),(115,0,'正常','0','counselor_status',NULL,'success','N','0','admin','2025-09-24 18:52:44','admin','2025-09-24 18:53:24','辅导员正常状态'),(116,1,'停用','1','counselor_status',NULL,'danger','N','0','admin','2025-09-24 18:53:18','',NULL,'辅导员停用状态'),(117,0,'未完成','0','ai_status',NULL,'info','N','0','admin','2025-09-25 19:00:10','admin','2025-09-25 19:00:25',NULL),(118,1,'已完成','1','ai_status',NULL,'success','N','0','admin','2025-09-25 19:03:31','admin','2025-09-25 19:05:09',NULL),(119,0,'未完成','0','read_status',NULL,'info','N','0','admin','2025-09-25 19:04:52','',NULL,NULL),(120,1,'已完成','1','read_status',NULL,'success','N','0','admin','2025-09-25 19:05:32','',NULL,NULL),(121,0,'高','高','risk_level',NULL,'danger','N','0','admin','2025-09-25 19:58:16','',NULL,NULL),(122,1,'中','中','risk_level',NULL,'warning','N','0','admin','2025-09-25 19:58:30','',NULL,NULL),(123,3,'低','低','risk_level',NULL,'success','N','0','admin','2025-09-25 19:58:42','',NULL,NULL),(124,0,'未完成','0','completion_status',NULL,'info','N','0','admin','2025-09-25 19:59:51','',NULL,NULL),(125,1,'已完成','1','completion_status',NULL,'success','N','0','admin','2025-09-25 20:00:03','',NULL,NULL),(126,1,'正确','1','is_correct',NULL,'success','N','0','admin','2025-11-08 18:12:54','admin','2025-11-08 18:13:00',NULL),(127,2,'错误','2','is_correct',NULL,'danger','N','0','admin','2025-11-08 18:13:11','admin','2025-11-08 18:13:28',NULL),(128,3,'无','3','is_correct',NULL,'info','N','0','admin','2025-11-08 18:14:26','',NULL,NULL),(129,0,'正常','0','music_status',NULL,'success','N','0','admin','2025-11-08 19:28:02','',NULL,NULL),(130,1,'下架','1','music_status',NULL,'danger','N','0','admin','2025-11-08 19:28:21','',NULL,NULL),(131,0,'正常','0','article_status',NULL,'success','N','0','admin','2025-11-08 19:29:12','',NULL,NULL),(132,1,'下架','1','article_status',NULL,'danger','N','0','admin','2025-11-08 19:29:20','',NULL,NULL),(133,0,'正常','0','course_status',NULL,'success','N','0','admin','2025-11-08 19:30:12','',NULL,NULL),(134,1,'下架','1','course_status',NULL,'danger','N','0','admin','2025-11-08 19:30:20','',NULL,NULL),(135,0,'初级','0','course_level',NULL,'default','N','0','admin','2025-11-08 19:33:33','admin','2025-11-08 19:34:47',NULL),(136,1,'中级','1','course_level',NULL,'default','N','0','admin','2025-11-08 19:34:10','admin','2025-11-08 19:34:42',NULL),(137,2,'高级','2','course_level',NULL,'default','N','0','admin','2025-11-08 19:34:33','',NULL,NULL),(138,0,'正常','0','banner_status',NULL,'success','N','0','admin','2025-11-09 17:35:33','',NULL,NULL),(139,1,'下架','1','banner_status',NULL,'danger','N','0','admin','2025-11-09 17:35:48','',NULL,NULL),(140,0,'否','0','post_is_anonymous',NULL,'danger','N','0','admin','2025-11-09 19:23:00','admin','2025-11-09 19:23:09',NULL),(141,1,'是','1','post_is_anonymous',NULL,'success','N','0','admin','2025-11-09 19:23:19','',NULL,NULL),(142,0,'正常','0','post_status',NULL,'success','N','0','admin','2025-11-09 19:23:42','',NULL,NULL),(143,1,'屏蔽','1','post_status',NULL,'danger','N','0','admin','2025-11-09 19:28:21','',NULL,NULL),(144,0,'否','0','comment_is_anonymous',NULL,'danger','N','0','admin','2025-11-09 19:29:18','',NULL,NULL),(145,1,'是','1','comment_is_anonymous',NULL,'success','N','0','admin','2025-11-09 19:29:25','admin','2025-11-09 19:29:32',NULL),(146,0,'正常','0','comment_status',NULL,'success','N','0','admin','2025-11-09 19:30:23','admin','2025-11-09 19:30:35',NULL),(147,1,'屏蔽','1','comment_status',NULL,'danger','N','0','admin','2025-11-09 19:30:42','admin','2025-11-09 19:30:52',NULL),(148,0,'待处理','0','process_status',NULL,'danger','N','0','admin','2026-03-12 00:22:37','',NULL,'待处理'),(149,1,'已处理','1','process_status',NULL,'success','N','0','admin','2026-03-12 00:23:03','admin','2026-03-12 00:23:26','已处理'),(150,2,'处理中','2','process_status',NULL,'warning','N','0','admin','2026-03-12 00:23:20','admin','2026-03-12 00:23:33','处理中'),(151,0,'正常','0','record_status',NULL,'success','N','0','admin','2026-03-13 18:59:33','admin','2026-03-13 19:00:02',NULL),(152,0,'异常','1','record_status',NULL,'danger','N','0','admin','2026-03-13 18:59:56','admin','2026-03-13 19:00:06',NULL);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) DEFAULT '' COMMENT '字典类型',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2025-09-15 19:23:23','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2025-09-15 19:23:23','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2025-09-15 19:23:23','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2025-09-15 19:23:23','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2025-09-15 19:23:23','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2025-09-15 19:23:23','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2025-09-15 19:23:23','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2025-09-15 19:23:23','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2025-09-15 19:23:23','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2025-09-15 19:23:23','',NULL,'登录状态列表'),(100,'学生状态','student_status','0','admin','2025-09-21 11:56:14','admin','2025-09-21 11:57:06','学生状态（0正常 1异常）'),(101,'问卷状态','questionnaire_status','0','admin','2025-09-23 08:56:22','',NULL,'问卷状态（0正常 1下架）'),(102,'问卷类型','questionnaire_type','0','admin','2025-09-23 08:59:30','',NULL,'问卷类型（0常规测评 1临时测评 2专项测评 3学业压力 4情绪状态 5人格特质）'),(103,'题目类型','question_type','0','admin','2025-09-23 18:27:29','admin','2025-09-24 18:49:43','问卷题目类型（choice 选择题 short_answer 简答题）'),(104,'用户类型','sys_user_type','0','admin','2025-09-24 16:00:15','admin','2025-09-24 18:50:44','用户类型（00 系统用户 01学生用户 02辅导员用户）'),(105,'辅导员状态','counselor_status','0','admin','2025-09-24 18:52:07','',NULL,'辅导员状态（0正常 1停用）'),(106,'AI分析状态','ai_status','0','admin','2025-09-25 18:59:25','',NULL,'AI分析状态（0未完成 1已完成）'),(107,'问卷已未读标识','read_status','0','admin','2025-09-25 19:04:23','',NULL,'已读标识（0未读 1已读）'),(108,'风险等级','risk_level','0','admin','2025-09-25 19:57:58','',NULL,'风险等级（低/中/高）'),(109,'完成标识','completion_status','0','admin','2025-09-25 19:59:18','',NULL,'完成标识（0未完成 1已完成）'),(110,'学生是否答对题目','is_correct','0','admin','2025-11-08 18:12:32','admin','2025-11-08 19:26:21',NULL),(111,'音乐状态','music_status','0','admin','2025-11-08 19:27:35','',NULL,NULL),(112,'文章状态','article_status','0','admin','2025-11-08 19:28:58','',NULL,NULL),(113,'课程状态','course_status','0','admin','2025-11-08 19:29:52','',NULL,NULL),(114,'课程难度','course_level','0','admin','2025-11-08 19:33:15','',NULL,NULL),(115,'轮播图状态','banner_status','0','admin','2025-11-09 17:35:11','',NULL,NULL),(116,'帖子是否匿名','post_is_anonymous','0','admin','2025-11-09 19:20:52','',NULL,NULL),(117,'帖子状态','post_status','0','admin','2025-11-09 19:21:29','',NULL,NULL),(118,'评论是否匿名','comment_is_anonymous','0','admin','2025-11-09 19:22:31','',NULL,NULL),(119,'帖子状态','comment_status','0','admin','2025-11-09 19:30:06','',NULL,NULL),(120,'处理状态','process_status','0','admin','2026-03-12 00:21:58','',NULL,NULL),(121,'记录状态','record_status','0','admin','2026-03-13 18:59:11','',NULL,NULL);
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2025-09-15 19:23:23','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2025-09-15 19:23:23','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2025-09-15 19:23:23','',NULL,'');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) DEFAULT NULL COMMENT '日志信息',
  `status` char(1) DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) DEFAULT '' COMMENT '操作系统',
  `status` char(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=400 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;

/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2124 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,5,'system',NULL,'','',1,0,'M','0','0','','system','admin','2025-09-15 19:23:23','admin','2026-03-13 20:38:57','系统管理目录'),(2,'系统监控',0,5,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2025-09-15 19:23:23','admin','2025-11-08 19:22:16','系统监控目录'),(3,'系统工具',0,7,'tool',NULL,'','',1,0,'M','0','0','','tool','admin','2025-09-15 19:23:23','admin','2025-11-08 19:19:06','系统工具目录'),(4,'若依官网',0,99,'http://ruoyi.vip',NULL,'','',0,0,'M','0','0','','guide','admin','2025-09-15 19:23:23','admin','2025-11-08 19:18:15','若依官网地址'),(100,'用户管理',1,0,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2025-09-15 19:23:23','admin','2026-03-12 10:10:29','用户管理菜单'),(101,'角色管理',1,1,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2025-09-15 19:23:23','admin','2025-11-09 17:27:09','角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2025-09-15 19:23:23','admin','2025-11-09 17:27:48','菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2025-09-15 19:23:23','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2025-09-15 19:23:23','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2025-09-15 19:23:23','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2025-09-15 19:23:23','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2025-09-15 19:23:23','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2025-09-15 19:23:23','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2025-09-15 19:23:23','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2025-09-15 19:23:23','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2025-09-15 19:23:23','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2025-09-15 19:23:23','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2025-09-15 19:23:23','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2025-09-15 19:23:23','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2025-09-15 19:23:23','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2025-09-15 19:23:23','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2025-09-15 19:23:23','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2025-09-15 19:23:23','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2025-09-15 19:23:23','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2025-09-15 19:23:23','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2025-09-15 19:23:23','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2025-09-15 19:23:23','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2025-09-15 19:23:23','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2025-09-15 19:23:23','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2025-09-15 19:23:23','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2025-09-15 19:23:23','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2025-09-15 19:23:23','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2025-09-15 19:23:23','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2025-09-15 19:23:23','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2025-09-15 19:23:23','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2025-09-15 19:23:23','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2025-09-15 19:23:23','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2025-09-15 19:23:23','',NULL,''),(2000,'人员管理',0,1,'people',NULL,NULL,'',1,0,'M','0','0','','人员管理','admin','2025-09-21 11:38:51','admin','2026-03-19 10:31:01',''),(2001,'学生管理',2000,1,'info','student/info/index',NULL,'',1,0,'C','0','0','student:info:list','学生管理','admin','2025-09-21 12:08:36','admin','2026-03-19 10:32:23','学生信息菜单'),(2002,'学生信息查询',2001,1,'#','',NULL,'',1,0,'F','0','0','student:info:query','#','admin','2025-09-21 12:08:36','',NULL,''),(2003,'学生信息新增',2001,2,'#','',NULL,'',1,0,'F','0','0','student:info:add','#','admin','2025-09-21 12:08:36','',NULL,''),(2004,'学生信息修改',2001,3,'#','',NULL,'',1,0,'F','0','0','student:info:edit','#','admin','2025-09-21 12:08:36','',NULL,''),(2005,'学生信息删除',2001,4,'#','',NULL,'',1,0,'F','0','0','student:info:remove','#','admin','2025-09-21 12:08:36','',NULL,''),(2006,'学生信息导出',2001,5,'#','',NULL,'',1,0,'F','0','0','student:info:export','#','admin','2025-09-21 12:08:36','',NULL,''),(2007,'测评管理',0,2,'questionnaire',NULL,NULL,'',1,0,'M','0','0','','测评管理','admin','2025-09-22 19:16:53','admin','2026-03-19 10:40:04',''),(2020,'问卷管理',2007,2,'questionnaireinfo','questionnaire/questionnaireinfo/index',NULL,'',1,0,'C','0','0','questionnaire:questionnaireinfo:list','edit','admin','2025-09-22 20:03:33','admin','2026-03-13 20:40:53','心理测评问卷管理菜单'),(2021,'心理测评问卷管理查询',2020,1,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnaireinfo:query','#','admin','2025-09-22 20:03:33','',NULL,''),(2022,'心理测评问卷管理新增',2020,2,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnaireinfo:add','#','admin','2025-09-22 20:03:33','',NULL,''),(2023,'心理测评问卷管理修改',2020,3,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnaireinfo:edit','#','admin','2025-09-22 20:03:33','',NULL,''),(2024,'心理测评问卷管理删除',2020,4,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnaireinfo:remove','#','admin','2025-09-22 20:03:33','',NULL,''),(2025,'心理测评问卷管理导出',2020,5,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnaireinfo:export','#','admin','2025-09-22 20:03:33','',NULL,''),(2026,'题库管理',2007,1,'questionnairebank','questionnaire/questionnairebank/index',NULL,'',1,0,'C','0','0','questionnaire:questionnairebank:list','题库管理','admin','2025-09-23 18:31:59','admin','2026-03-19 10:40:15','题库管理菜单'),(2027,'题库管理查询',2026,1,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnairebank:query','#','admin','2025-09-23 18:31:59','',NULL,''),(2028,'题库管理新增',2026,2,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnairebank:add','#','admin','2025-09-23 18:32:00','',NULL,''),(2029,'题库管理修改',2026,3,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnairebank:edit','#','admin','2025-09-23 18:32:00','',NULL,''),(2030,'题库管理删除',2026,4,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnairebank:remove','#','admin','2025-09-23 18:32:00','',NULL,''),(2031,'题库管理导出',2026,5,'#','',NULL,'',1,0,'F','0','0','questionnaire:questionnairebank:export','#','admin','2025-09-23 18:32:00','',NULL,''),(2032,'辅导员管理',2000,2,'counselorinfo','counselor/counselorinfo/index',NULL,'',1,0,'C','0','0','counselor:counselorinfo:list','辅导员管理','admin','2025-09-24 19:02:42','admin','2026-03-19 10:37:38','辅导员管理菜单'),(2033,'辅导员管理查询',2032,1,'#','',NULL,'',1,0,'F','0','0','counselor:counselorinfo:query','#','admin','2025-09-24 19:02:42','',NULL,''),(2034,'辅导员管理新增',2032,2,'#','',NULL,'',1,0,'F','0','0','counselor:counselorinfo:add','#','admin','2025-09-24 19:02:42','',NULL,''),(2035,'辅导员管理修改',2032,3,'#','',NULL,'',1,0,'F','0','0','counselor:counselorinfo:edit','#','admin','2025-09-24 19:02:42','',NULL,''),(2036,'辅导员管理删除',2032,4,'#','',NULL,'',1,0,'F','0','0','counselor:counselorinfo:remove','#','admin','2025-09-24 19:02:42','',NULL,''),(2037,'辅导员管理导出',2032,5,'#','',NULL,'',1,0,'F','0','0','counselor:counselorinfo:export','#','admin','2025-09-24 19:02:42','',NULL,''),(2038,'查询未绑定的用户ID/昵称列表',2032,6,'#','',NULL,'',1,0,'F','0','0','counselor:counselorinfo:listUnbindUsers','#','admin','2025-09-24 19:02:42','',NULL,''),(2039,'结果管理',2007,4,'evaluationResult','evaluation/evaluationResult/index',NULL,'',1,0,'C','0','0','evaluation:evaluationResult:list','checkbox','admin','2025-09-25 20:05:33','admin','2026-03-13 20:41:56','心理测评结果菜单'),(2040,'心理测评结果查询',2039,1,'#','',NULL,'',1,0,'F','0','0','evaluation:evaluationResult:query','#','admin','2025-09-25 20:05:33','',NULL,''),(2041,'心理测评结果新增',2039,2,'#','',NULL,'',1,0,'F','0','0','evaluation:evaluationResult:add','#','admin','2025-09-25 20:05:33','',NULL,''),(2042,'心理测评结果修改',2039,3,'#','',NULL,'',1,0,'F','0','0','evaluation:evaluationResult:edit','#','admin','2025-09-25 20:05:33','',NULL,''),(2043,'心理测评结果删除',2039,4,'#','',NULL,'',1,0,'F','0','0','evaluation:evaluationResult:remove','#','admin','2025-09-25 20:05:33','',NULL,''),(2044,'心理测评结果导出',2039,5,'#','',NULL,'',1,0,'F','0','0','evaluation:evaluationResult:export','#','admin','2025-09-25 20:05:33','',NULL,''),(2045,'心理评测发送问卷',2020,6,'',NULL,NULL,'',1,0,'F','0','0','questionnaire:questionnaireinfo:send','#','admin','2025-09-26 11:34:28','admin','2025-09-26 11:35:04',''),(2046,'答题管理',2007,3,'questionnaireAnswer','questionnaireAnswer/questionnaireAnswer/index',NULL,'',1,0,'C','0','0','questionnaireAnswer:questionnaireAnswer:list','clipboard','admin','2025-11-08 18:23:54','admin','2026-03-13 20:41:01','心理测评答题记录菜单'),(2047,'心理测评答题记录查询',2046,1,'#','',NULL,'',1,0,'F','0','0','questionnaireAnswer:questionnaireAnswer:query','#','admin','2025-11-08 18:23:55','',NULL,''),(2048,'心理测评答题记录新增',2046,2,'#','',NULL,'',1,0,'F','0','0','questionnaireAnswer:questionnaireAnswer:add','#','admin','2025-11-08 18:23:55','',NULL,''),(2049,'心理测评答题记录修改',2046,3,'#','',NULL,'',1,0,'F','0','0','questionnaireAnswer:questionnaireAnswer:edit','#','admin','2025-11-08 18:23:55','',NULL,''),(2050,'心理测评答题记录删除',2046,4,'#','',NULL,'',1,0,'F','0','0','questionnaireAnswer:questionnaireAnswer:remove','#','admin','2025-11-08 18:23:55','',NULL,''),(2051,'心理测评答题记录导出',2046,5,'#','',NULL,'',1,0,'F','0','0','questionnaireAnswer:questionnaireAnswer:export','#','admin','2025-11-08 18:23:55','',NULL,''),(2052,'推荐管理',0,3,'Recommend',NULL,NULL,'',1,0,'M','0','0','','推荐管理','admin','2025-11-08 19:14:54','admin','2026-03-19 10:49:32',''),(2053,'心理文章推荐',2052,1,'RecommendArticle','recommend/RecommendArticle/index',NULL,'',1,0,'M','0','0','RecommendArticle:RecommendArticle:list','文章推荐','admin','2025-11-08 19:40:16','admin','2026-03-19 10:49:38','心理文章推荐菜单'),(2054,'心理文章推荐查询',2053,1,'#','',NULL,'',1,0,'F','0','0','RecommendArticle:RecommendArticle:query','#','admin','2025-11-08 19:40:16','',NULL,''),(2055,'心理文章推荐新增',2053,2,'#','',NULL,'',1,0,'F','0','0','RecommendArticle:RecommendArticle:add','#','admin','2025-11-08 19:40:16','',NULL,''),(2056,'心理文章推荐修改',2053,3,'#','',NULL,'',1,0,'F','0','0','RecommendArticle:RecommendArticle:edit','#','admin','2025-11-08 19:40:16','',NULL,''),(2057,'心理文章推荐删除',2053,4,'#','',NULL,'',1,0,'F','0','0','RecommendArticle:RecommendArticle:remove','#','admin','2025-11-08 19:40:16','',NULL,''),(2058,'心理文章推荐导出',2053,5,'#','',NULL,'',1,0,'F','0','0','RecommendArticle:RecommendArticle:export','#','admin','2025-11-08 19:40:16','',NULL,''),(2059,'心理课程推荐',2052,1,'RecommendCourse','recommend/RecommendCourse/index',NULL,'',1,0,'C','0','0','RecommendCourse:RecommendCourse:list','课程推荐','admin','2025-11-08 19:40:16','admin','2026-03-19 10:50:18','心理课程推荐菜单'),(2060,'心理课程推荐查询',2059,1,'#','',NULL,'',1,0,'F','0','0','RecommendCourse:RecommendCourse:query','#','admin','2025-11-08 19:40:17','',NULL,''),(2061,'心理课程推荐新增',2059,2,'#','',NULL,'',1,0,'F','0','0','RecommendCourse:RecommendCourse:add','#','admin','2025-11-08 19:40:17','',NULL,''),(2062,'心理课程推荐修改',2059,3,'#','',NULL,'',1,0,'F','0','0','RecommendCourse:RecommendCourse:edit','#','admin','2025-11-08 19:40:17','',NULL,''),(2063,'心理课程推荐删除',2059,4,'#','',NULL,'',1,0,'F','0','0','RecommendCourse:RecommendCourse:remove','#','admin','2025-11-08 19:40:17','',NULL,''),(2064,'心理课程推荐导出',2059,5,'#','',NULL,'',1,0,'F','0','0','RecommendCourse:RecommendCourse:export','#','admin','2025-11-08 19:40:17','',NULL,''),(2065,'心理音乐推荐',2052,1,'RecommendMusic','recommend/RecommendMusic/index',NULL,'',1,0,'C','0','0','RecommendMusic:RecommendMusic:list','音乐推荐','admin','2025-11-08 19:40:17','admin','2026-03-19 10:49:47','心理音乐推荐菜单'),(2066,'心理音乐推荐查询',2065,1,'#','',NULL,'',1,0,'F','0','0','RecommendMusic:RecommendMusic:query','#','admin','2025-11-08 19:40:17','',NULL,''),(2067,'心理音乐推荐新增',2065,2,'#','',NULL,'',1,0,'F','0','0','RecommendMusic:RecommendMusic:add','#','admin','2025-11-08 19:40:17','',NULL,''),(2068,'心理音乐推荐修改',2065,3,'#','',NULL,'',1,0,'F','0','0','RecommendMusic:RecommendMusic:edit','#','admin','2025-11-08 19:40:17','',NULL,''),(2069,'心理音乐推荐删除',2065,4,'#','',NULL,'',1,0,'F','0','0','RecommendMusic:RecommendMusic:remove','#','admin','2025-11-08 19:40:17','',NULL,''),(2070,'心理音乐推荐导出',2065,5,'#','',NULL,'',1,0,'F','0','0','RecommendMusic:RecommendMusic:export','#','admin','2025-11-08 19:40:17','',NULL,''),(2071,'轮播图推荐',2052,4,'banner','banner/index',NULL,'',1,0,'C','0','0','banner:banner:list','轮播图推荐','admin','2025-11-09 17:26:02','admin','2026-03-19 10:50:25','轮播图菜单'),(2072,'轮播图查询',2071,1,'#','',NULL,'',1,0,'F','0','0','banner:banner:query','#','admin','2025-11-09 17:26:03','',NULL,''),(2073,'轮播图新增',2071,2,'#','',NULL,'',1,0,'F','0','0','banner:banner:add','#','admin','2025-11-09 17:26:03','',NULL,''),(2074,'轮播图修改',2071,3,'#','',NULL,'',1,0,'F','0','0','banner:banner:edit','#','admin','2025-11-09 17:26:03','',NULL,''),(2075,'轮播图删除',2071,4,'#','',NULL,'',1,0,'F','0','0','banner:banner:remove','#','admin','2025-11-09 17:26:03','',NULL,''),(2076,'轮播图导出',2071,5,'#','',NULL,'',1,0,'F','0','0','banner:banner:export','#','admin','2025-11-09 17:26:03','',NULL,''),(2077,'内容管理',0,4,'content',NULL,NULL,'',1,0,'M','0','0','','内容管理','admin','2025-11-09 19:00:11','admin','2026-03-19 10:55:24',''),(2078,'帖子管理',2077,1,'community','community/index',NULL,'',1,0,'C','0','0','community:community:list','帖子管理','admin','2025-11-09 20:31:11','admin','2026-03-19 10:55:40','帖子管理菜单'),(2079,'帖子管理查询',2078,1,'#','',NULL,'',1,0,'F','0','0','community:community:query','#','admin','2025-11-09 20:31:11','',NULL,''),(2080,'帖子管理新增',2078,2,'#','',NULL,'',1,0,'F','0','0','community:community:add','#','admin','2025-11-09 20:31:11','',NULL,''),(2081,'帖子管理修改',2078,3,'#','',NULL,'',1,0,'F','0','0','community:community:edit','#','admin','2025-11-09 20:31:11','',NULL,''),(2082,'帖子管理删除',2078,4,'#','',NULL,'',1,0,'F','0','0','community:community:remove','#','admin','2025-11-09 20:31:11','',NULL,''),(2083,'帖子管理导出',2078,5,'#','',NULL,'',1,0,'F','0','0','community:community:export','#','admin','2025-11-09 20:31:11','',NULL,''),(2084,'评论管理',2077,2,'comment','community/comment/index','','',1,0,'C','0','0','community:comment:list','评论管理','admin','2026-03-14 13:13:35','admin','2026-03-19 10:55:47','评论管理菜单'),(2085,'评论管理查询',2084,1,'#','','','',1,0,'F','0','0','community:comment:query','#','admin','2026-03-14 13:13:35','',NULL,''),(2086,'评论管理删除',2084,2,'#','','','',1,0,'F','0','0','community:comment:remove','#','admin','2026-03-14 13:13:35','',NULL,''),(2087,'评论管理导出',2084,3,'#','','','',1,0,'F','0','0','community:comment:export','#','admin','2026-03-14 13:13:35','',NULL,''),(2105,'干预管理',0,3,'intervention',NULL,'','',1,0,'M','0','0','','干预管理','admin','2026-03-13 20:09:01','admin','2026-03-19 10:52:43','干预管理目录'),(2106,'干预通知',2105,1,'notification','intervention/notification/index','','',1,0,'C','0','0','intervention:notification:list','message','admin','2026-03-13 20:09:01','',NULL,'干预通知菜单'),(2107,'干预通知查询',2106,1,'',NULL,'','',1,0,'F','0','0','intervention:notification:query','#','admin','2026-03-13 20:09:01','',NULL,''),(2108,'干预通知新增',2106,2,'',NULL,'','',1,0,'F','0','0','intervention:notification:add','#','admin','2026-03-13 20:09:02','',NULL,''),(2109,'干预通知修改',2106,3,'',NULL,'','',1,0,'F','0','0','intervention:notification:edit','#','admin','2026-03-13 20:09:02','',NULL,''),(2110,'干预通知删除',2106,4,'',NULL,'','',1,0,'F','0','0','intervention:notification:remove','#','admin','2026-03-13 20:09:02','',NULL,''),(2111,'干预通知导出',2106,5,'',NULL,'','',1,0,'F','0','0','intervention:notification:export','#','admin','2026-03-13 20:09:02','',NULL,''),(2112,'干预记录',2105,2,'process','intervention/processRecord/index','','',1,0,'C','0','0','intervention:process:list','edit','admin','2026-03-13 20:09:02','admin','2026-03-13 20:57:21','干预处理记录菜单'),(2113,'干预处理记录查询',2112,1,'',NULL,'','',1,0,'F','0','0','intervention:process:query','#','admin','2026-03-13 20:09:02','',NULL,''),(2114,'干预处理记录新增',2112,2,'',NULL,'','',1,0,'F','0','0','intervention:process:add','#','admin','2026-03-13 20:09:02','',NULL,''),(2115,'干预处理记录修改',2112,3,'',NULL,'','',1,0,'F','0','0','intervention:process:edit','#','admin','2026-03-13 20:09:02','',NULL,''),(2116,'干预处理记录删除',2112,4,'',NULL,'','',1,0,'F','0','0','intervention:process:remove','#','admin','2026-03-13 20:09:02','',NULL,''),(2117,'干预处理记录导出',2112,5,'',NULL,'','',1,0,'F','0','0','intervention:process:export','#','admin','2026-03-13 20:09:02','',NULL,''),(2118,'风险配置',2105,3,'risk','intervention/riskConfig/index','','',1,0,'C','0','0','intervention:riskConfig:list','风险配置','admin','2026-03-13 20:09:02','admin','2026-03-19 10:52:53','风险等级配置菜单'),(2119,'风险等级配置查询',2118,1,'',NULL,'','',1,0,'F','0','0','intervention:riskConfig:query','#','admin','2026-03-13 20:09:03','',NULL,''),(2120,'风险等级配置新增',2118,2,'',NULL,'','',1,0,'F','0','0','intervention:riskConfig:add','#','admin','2026-03-13 20:09:03','',NULL,''),(2121,'风险等级配置修改',2118,3,'',NULL,'','',1,0,'F','0','0','intervention:riskConfig:edit','#','admin','2026-03-13 20:09:03','',NULL,''),(2122,'风险等级配置删除',2118,4,'',NULL,'','',1,0,'F','0','0','intervention:riskConfig:remove','#','admin','2026-03-13 20:09:03','',NULL,''),(2123,'风险等级配置导出',2118,5,'',NULL,'','',1,0,'F','0','0','intervention:riskConfig:export','#','admin','2026-03-13 20:09:03','',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) NOT NULL COMMENT '公告标题',
  `notice_type` char(1) NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 若依新版本发布啦','2',_binary '新版本内容','0','admin','2025-09-15 19:23:23','',NULL,'管理员'),(2,'维护通知：2018-07-01 若依系统凌晨维护','1',_binary '维护内容','0','admin','2025-09-15 19:23:23','',NULL,'管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=811 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;

/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2025-09-15 19:23:22','',NULL,''),(2,'lassMonitor','班长',2,'0','admin','2025-09-15 19:23:22','admin','2025-09-24 19:35:43',''),(3,'LeputyMonitor','副班长',3,'0','admin','2025-09-15 19:23:22','admin','2025-09-24 19:38:18',''),(4,'StudyRep','学习委员',4,'0','admin','2025-09-15 19:23:22','admin','2025-09-24 19:36:15',''),(5,'LifeRep','生活委员',5,'0','admin','2025-09-24 19:36:49','',NULL,NULL),(6,'SportsRep','体育委员',6,'0','admin','2025-09-24 19:37:04','admin','2025-09-24 19:38:10',NULL),(7,'OrgRep','组织委员',7,'0','admin','2025-09-24 19:37:21','',NULL,NULL),(8,'PubRep','宣传委员',8,'0','admin','2025-09-24 19:37:36','',NULL,NULL),(9,'PsychRep','心理委员',9,'0','admin','2025-09-24 19:38:01','',NULL,NULL),(10,'InfoRep','信息员',10,'0','admin','2025-09-24 19:38:55','',NULL,NULL);
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2025-09-15 19:23:23','',NULL,'超级管理员'),(2,'辅导员','common',2,'2',1,1,'0','0','admin','2025-09-15 19:23:23','admin','2026-03-13 21:00:33','辅导员'),(100,'学生','student',3,'1',1,1,'0','0','admin','2026-03-12 10:28:52','',NULL,'学生');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,2000),(2,2001),(2,2002),(2,2003),(2,2004),(2,2005),(2,2006),(2,2105),(2,2112),(2,2113),(2,2114),(2,2115),(2,2117);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` char(1) DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) DEFAULT '' COMMENT '密码',
  `status` char(1) DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','才渡','00','caidu@163.com','15888888888','0','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/avatar/1_1773510341879.jpg','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-04-09 21:43:28','2025-09-15 19:23:22','admin','2025-09-15 19:23:22','','2026-03-15 01:45:42','管理员'),(2,105,'ry','若依','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','2','127.0.0.1','2025-09-15 19:23:22','2025-09-15 19:23:22','admin','2025-09-15 19:23:22','',NULL,'测试员'),(100,103,'xiaodu','学生001','01','','','0','https://sky-take-out-jcd.oss-cn-beijing.aliyuncs.com/avatar/100_1773510008410.png','$2a$10$QKfOEIUVaQddwzaslKNXwudVvOPohzpCCAxfT4Ye4OaOFOKqGGlrC','0','0','127.0.0.1','2026-04-18 04:19:27',NULL,'admin','2025-09-21 18:36:08','admin','2026-03-15 01:40:09',NULL),(101,100,'xxx','辅导员001','02','2364728886@qq.com','','0','','$2a$10$GTsqA5Pme4jOTA3o5l9K7.lNcZIEHoluVj5BxPMFdBWwjFTnEeIxG','0','0','127.0.0.1','2026-03-13 20:46:15',NULL,'admin','2025-09-21 19:39:40','admin','2026-03-21 09:38:58',NULL),(102,103,'www','www','01','2364728886@qq.com','13746688888','0','','$2a$10$OooFE47Min3rmgNGkF5Vwe9E38O6ANbnNBtv97Lc4ffPv2rrIbN2i','0','2','',NULL,NULL,'admin','2025-09-24 15:54:17','admin','2025-09-24 16:56:04',NULL),(103,101,'ccc','辅导员002','02','236472666@qq.com','13788886666','0','','$2a$10$.h4zhyUxEmAg/tKEj14Rqu1xD6mHnEqjdOB/9hMc4eWsK9JiFyotO','0','0','127.0.0.1','2026-03-13 19:55:58','2026-03-13 19:04:03','admin','2025-09-24 16:04:06','admin','2026-03-13 19:24:41',NULL),(104,100,'sss','辅导员003','02','3174120025@qq.com','17355556666','0','','$2a$10$osq8xBU3FxaZQYxVCiQwJ.1.yEcLG.M93CFLdTF1wHB1d0Ek6poFa','0','0','',NULL,NULL,'admin','2025-09-24 16:13:16','admin','2026-03-13 19:24:48',NULL),(105,103,'xiaodu002','学生002','01','caidu520@qq.com','17388886666','0','','$2a$10$zhXwXjCzhb7xkRyRQDHRte4PyEcDFHDh2501sSvVJ1P.4idK5CN2m','0','0','',NULL,NULL,'admin','2025-11-08 17:52:41','admin','2026-03-13 19:25:00',NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(100,4),(104,4),(105,4);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(100,100),(101,2),(103,2),(104,2),(105,100);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-26 14:57:36
