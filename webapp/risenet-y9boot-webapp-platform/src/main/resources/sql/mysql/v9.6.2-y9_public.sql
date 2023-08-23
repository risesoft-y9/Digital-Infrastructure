-- MySQL dump 10.13  Distrib 8.0.33, for macos13.3 (arm64)
--
-- Host: 127.0.0.1    Database: y9_public
-- ------------------------------------------------------
-- Server version	8.0.33

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
-- Table structure for table `jv_commit`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jv_commit` (
  `commit_pk` bigint NOT NULL AUTO_INCREMENT,
  `author` varchar(200) DEFAULT NULL,
  `commit_date` timestamp(3) NULL DEFAULT NULL,
  `commit_date_instant` varchar(30) DEFAULT NULL,
  `commit_id` decimal(22,2) DEFAULT NULL,
  PRIMARY KEY (`commit_pk`),
  KEY `jv_commit_commit_id_idx` (`commit_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jv_commit`
--

/*!40000 ALTER TABLE `jv_commit` DISABLE KEYS */;
/*!40000 ALTER TABLE `jv_commit` ENABLE KEYS */;

--
-- Table structure for table `jv_commit_property`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jv_commit_property` (
  `property_name` varchar(191) NOT NULL,
  `property_value` varchar(600) DEFAULT NULL,
  `commit_fk` bigint NOT NULL,
  PRIMARY KEY (`commit_fk`,`property_name`),
  KEY `jv_commit_property_commit_fk_idx` (`commit_fk`),
  KEY `jv_commit_property_property_name_property_value_idx` (`property_name`,`property_value`(191)),
  CONSTRAINT `jv_commit_property_commit_fk` FOREIGN KEY (`commit_fk`) REFERENCES `jv_commit` (`commit_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jv_commit_property`
--

/*!40000 ALTER TABLE `jv_commit_property` DISABLE KEYS */;
/*!40000 ALTER TABLE `jv_commit_property` ENABLE KEYS */;

--
-- Table structure for table `jv_global_id`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jv_global_id` (
  `global_id_pk` bigint NOT NULL AUTO_INCREMENT,
  `local_id` varchar(191) DEFAULT NULL,
  `fragment` varchar(200) DEFAULT NULL,
  `type_name` varchar(200) DEFAULT NULL,
  `owner_id_fk` bigint DEFAULT NULL,
  PRIMARY KEY (`global_id_pk`),
  KEY `jv_global_id_local_id_idx` (`local_id`),
  KEY `jv_global_id_owner_id_fk_idx` (`owner_id_fk`),
  CONSTRAINT `jv_global_id_owner_id_fk` FOREIGN KEY (`owner_id_fk`) REFERENCES `jv_global_id` (`global_id_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jv_global_id`
--

/*!40000 ALTER TABLE `jv_global_id` DISABLE KEYS */;
/*!40000 ALTER TABLE `jv_global_id` ENABLE KEYS */;

--
-- Table structure for table `jv_snapshot`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `jv_snapshot` (
  `snapshot_pk` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(200) DEFAULT NULL,
  `version` bigint DEFAULT NULL,
  `state` text,
  `changed_properties` text,
  `managed_type` varchar(200) DEFAULT NULL,
  `global_id_fk` bigint DEFAULT NULL,
  `commit_fk` bigint DEFAULT NULL,
  PRIMARY KEY (`snapshot_pk`),
  KEY `jv_snapshot_global_id_fk_idx` (`global_id_fk`),
  KEY `jv_snapshot_commit_fk_idx` (`commit_fk`),
  CONSTRAINT `jv_snapshot_commit_fk` FOREIGN KEY (`commit_fk`) REFERENCES `jv_commit` (`commit_pk`),
  CONSTRAINT `jv_snapshot_global_id_fk` FOREIGN KEY (`global_id_fk`) REFERENCES `jv_global_id` (`global_id_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jv_snapshot`
--

/*!40000 ALTER TABLE `jv_snapshot` DISABLE KEYS */;
/*!40000 ALTER TABLE `jv_snapshot` ENABLE KEYS */;

--
-- Table structure for table `Registered_Services`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Registered_Services` (
  `id` bigint NOT NULL,
  `body` varchar(8000) NOT NULL,
  `evaluation_Order` int NOT NULL,
  `evaluation_Priority` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `service_Id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Registered_Services`
--

/*!40000 ALTER TABLE `Registered_Services` DISABLE KEYS */;
INSERT INTO `Registered_Services` (`id`, `body`, `evaluation_Order`, `evaluation_Priority`, `name`, `service_Id`) VALUES (1,'\n{\n  \"@class\": \"org.apereo.cas.support.oauth.services.OAuthRegisteredService\",\n  \"serviceId\": \"^(https?)://.*\",\n  \"name\": \"oauthServiceDemo\",\n  \"theme\": \"y9-apereo\",\n  \"id\": 1002,\n  \"description\": \"oauth Authentication app demo\",\n  \"evaluationOrder\": 1002,\n  \"attributeReleasePolicy\":\n  {\n    \"@class\": \"org.apereo.cas.services.ReturnAllAttributeReleasePolicy\",\n    \"authorizedToReleaseCredentialPassword\": true,\n    \"authorizedToReleaseProxyGrantingTicket\": true\n  },\n  \"logoutUrl\": \"http://localhost:7055/oauth/public/oauth/callback\",\n  \"clientSecret\": \"secret\",\n  \"clientId\": \"clientid\",\n  \"bypassApprovalPrompt\": true,\n  \"generateRefreshToken\": true,\n  \"renewRefreshToken\": true,\n  \"supportedGrantTypes\":\n  [\n    \"java.util.HashSet\",\n    [\n      \"refresh_token\",\n      \"password\",\n      \"client_credentials\",\n      \"authorization_code\"\n    ]\n  ],\n  \"supportedResponseTypes\":\n  [\n    \"java.util.HashSet\",\n    [\n      \"code\",\n      \"token\"\n    ]\n  ]\n}',1002,0,'oauthServiceDemo','^(https?)://.*');
INSERT INTO `Registered_Services` (`id`, `body`, `evaluation_Order`, `evaluation_Priority`, `name`, `service_Id`) VALUES (2,'\n{\n  \"@class\": \"org.apereo.cas.services.CasRegisteredService\",\n  \"serviceId\": \"^(https?)://localhost:7055/test.*\",\n  \"name\": \"cas3ServiceDemo\",\n  \"theme\": \"y9-apereo\",\n  \"id\": 1000,\n  \"description\": \"cas3 Authentication protocols app\",\n  \"evaluationOrder\": 1000,\n  \"attributeReleasePolicy\":\n  {\n    \"@class\": \"org.apereo.cas.services.ReturnAllAttributeReleasePolicy\",\n    \"authorizedToReleaseCredentialPassword\": true,\n    \"authorizedToReleaseProxyGrantingTicket\": true\n  },\n  \"multifactorPolicy\":\n  {\n    \"@class\": \"org.apereo.cas.services.DefaultRegisteredServiceMultifactorPolicy\",\n    \"failureMode\": \"CLOSED\"\n  }\n}',1000,0,'cas3ServiceDemo','^(https?)://localhost:7055/test.*');
INSERT INTO `Registered_Services` (`id`, `body`, `evaluation_Order`, `evaluation_Priority`, `name`, `service_Id`) VALUES (3,'\n{\n  \"@class\": \"org.apereo.cas.services.CasRegisteredService\",\n  \"serviceId\": \"^(https?)://localhost:7055/jwt.*\",\n  \"name\": \"jwtServiceDemo\",\n  \"theme\": \"y9-apereo\",\n  \"id\": 1001,\n  \"description\": \"jwt Authentication app\",\n  \"evaluationOrder\": 1001,\n  \"attributeReleasePolicy\":\n  {\n    \"@class\": \"org.apereo.cas.services.ReturnAllAttributeReleasePolicy\",\n    \"authorizedToReleaseCredentialPassword\": true,\n    \"authorizedToReleaseProxyGrantingTicket\": true\n  },\n  \"properties\":\n  {\n    \"@class\": \"java.util.HashMap\",\n    \"jwtAsServiceTicket\":\n    {\n      \"@class\": \"org.apereo.cas.services.DefaultRegisteredServiceProperty\",\n      \"values\":\n      [\n        \"java.util.LinkedHashSet\",\n        [\n          \"true\"\n        ]\n      ]\n    }\n  }\n}',1001,0,'jwtServiceDemo','^(https?)://localhost:7055/jwt.*');
/*!40000 ALTER TABLE `Registered_Services` ENABLE KEYS */;

--
-- Table structure for table `service_sequence`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_sequence`
--

/*!40000 ALTER TABLE `service_sequence` DISABLE KEYS */;
INSERT INTO `service_sequence` (`next_val`) VALUES (201);
/*!40000 ALTER TABLE `service_sequence` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_ACCOUNT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_ACCOUNT` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `AVATOR` varchar(255) DEFAULT NULL COMMENT '头像',
  `CAID` varchar(255) DEFAULT NULL COMMENT 'ca认证号',
  `DN` varchar(2000) DEFAULT NULL COMMENT '承继关系',
  `EMAIL` varchar(255) DEFAULT NULL COMMENT '邮箱',
  `GLOBAL_MANAGER` int NOT NULL DEFAULT '0' COMMENT '是否全局管理员',
  `GUID_PATH` varchar(800) DEFAULT NULL COMMENT '由ID组成的父子关系列表，之间用逗号分隔,40*20',
  `ID_NUM` varchar(255) DEFAULT NULL COMMENT '证件号码',
  `JSON_STR` longtext COMMENT 'jsonStr',
  `LOGIN_NAME` varchar(255) NOT NULL COMMENT '登录名',
  `MANAGER_LEVEL` int NOT NULL DEFAULT '0' COMMENT '是否三员管理员',
  `MOBILE` varchar(255) DEFAULT NULL COMMENT '移动电话',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORDERED_PATH` varchar(500) DEFAULT NULL COMMENT '排序',
  `ORIGINAL` int NOT NULL DEFAULT '1' COMMENT '原始人员',
  `ORIGINAL_ID` varchar(38) DEFAULT NULL COMMENT '原始人员id',
  `PARENT_ID` varchar(255) DEFAULT NULL COMMENT '父节点id',
  `PASSWORD` varchar(255) DEFAULT NULL COMMENT '密码',
  `PERSON_ID` varchar(38) NOT NULL COMMENT '人员id',
  `PERSON_TYPE` varchar(255) DEFAULT NULL COMMENT '人员类型',
  `POSITIONS` longtext COMMENT '拥有的岗位列表',
  `ROLES` longtext COMMENT '拥有的角色列表',
  `SEX` int DEFAULT NULL COMMENT '性别：1为男，0为女',
  `TENANT_ID` varchar(38) NOT NULL COMMENT '租户id',
  `TENANT_NAME` varchar(255) NOT NULL COMMENT '租户名称，冗余字段，为了显示用',
  `TENANT_SHORT_NAME` varchar(255) NOT NULL COMMENT '租户英文名称，冗余字段，为了显示用',
  `VERSION` int DEFAULT NULL COMMENT '乐观锁',
  `WEIXIN_ID` varchar(255) DEFAULT NULL COMMENT '用户绑定的微信id',
  PRIMARY KEY (`ID`),
  KEY `IDX7d3kas0oqvn2o3sv6m7wjecr2` (`LOGIN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_ACCOUNT`
--

/*!40000 ALTER TABLE `Y9_COMMON_ACCOUNT` DISABLE KEYS */;
INSERT INTO `Y9_COMMON_ACCOUNT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `AVATOR`, `CAID`, `DN`, `EMAIL`, `GLOBAL_MANAGER`, `GUID_PATH`, `ID_NUM`, `JSON_STR`, `LOGIN_NAME`, `MANAGER_LEVEL`, `MOBILE`, `NAME`, `ORDERED_PATH`, `ORIGINAL`, `ORIGINAL_ID`, `PARENT_ID`, `PASSWORD`, `PERSON_ID`, `PERSON_TYPE`, `POSITIONS`, `ROLES`, `SEX`, `TENANT_ID`, `TENANT_NAME`, `TENANT_SHORT_NAME`, `VERSION`, `WEIXIN_ID`) VALUES ('1563254091038068736','2023-08-23 18:13:39.178000','2023-08-23 18:13:39.178000',NULL,NULL,'cn=业务用户,o=业务组织',NULL,0,'11111111-1111-1111-1111-111111111123,11111111-1111-1111-1111-111111111116',NULL,NULL,'user',0,'13551111111','业务用户','10000,10003',1,NULL,'11111111-1111-1111-1111-111111111123','$2a$10$kHk36jmxcw6wAwopLcGOneXpfqByC.vUHQ334HRifEThanad5ltgW','11111111-1111-1111-1111-111111111116','单位用户','','',1,'11111111-1111-1111-1111-111111111113','default','default',0,NULL);
INSERT INTO `Y9_COMMON_ACCOUNT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `AVATOR`, `CAID`, `DN`, `EMAIL`, `GLOBAL_MANAGER`, `GUID_PATH`, `ID_NUM`, `JSON_STR`, `LOGIN_NAME`, `MANAGER_LEVEL`, `MOBILE`, `NAME`, `ORDERED_PATH`, `ORIGINAL`, `ORIGINAL_ID`, `PARENT_ID`, `PASSWORD`, `PERSON_ID`, `PERSON_TYPE`, `POSITIONS`, `ROLES`, `SEX`, `TENANT_ID`, `TENANT_NAME`, `TENANT_SHORT_NAME`, `VERSION`, `WEIXIN_ID`) VALUES ('1563254091742711808','2023-08-23 18:13:39.329000','2023-08-23 18:13:39.329000',NULL,NULL,'cn=系统管理员,o=组织',NULL,1,'11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111117',NULL,NULL,'systemManager',1,NULL,'系统管理员',NULL,1,NULL,'11111111-1111-1111-1111-111111111115','$2a$10$Hl7pEZI8y0pAWGlMz/7uTOMHP8WUy/vzQEliHMxHQSZLUGHfHRgFS','11111111-1111-1111-1111-111111111117','deptPerson',NULL,NULL,1,'11111111-1111-1111-1111-111111111113','default','default',0,NULL);
INSERT INTO `Y9_COMMON_ACCOUNT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `AVATOR`, `CAID`, `DN`, `EMAIL`, `GLOBAL_MANAGER`, `GUID_PATH`, `ID_NUM`, `JSON_STR`, `LOGIN_NAME`, `MANAGER_LEVEL`, `MOBILE`, `NAME`, `ORDERED_PATH`, `ORIGINAL`, `ORIGINAL_ID`, `PARENT_ID`, `PASSWORD`, `PERSON_ID`, `PERSON_TYPE`, `POSITIONS`, `ROLES`, `SEX`, `TENANT_ID`, `TENANT_NAME`, `TENANT_SHORT_NAME`, `VERSION`, `WEIXIN_ID`) VALUES ('1563254092430577664','2023-08-23 18:13:39.494000','2023-08-23 18:13:39.494000',NULL,NULL,'cn=安全保密员,o=组织',NULL,1,'11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111118',NULL,NULL,'securityManager',2,NULL,'安全保密员',NULL,1,NULL,'11111111-1111-1111-1111-111111111115','$2a$10$j8ayJlfaOAnEIw.bntz2gu.5prfJ8ELvhGwmSD5xhIabl9hLhBG/C','11111111-1111-1111-1111-111111111118','deptPerson',NULL,NULL,1,'11111111-1111-1111-1111-111111111113','default','default',0,NULL);
INSERT INTO `Y9_COMMON_ACCOUNT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `AVATOR`, `CAID`, `DN`, `EMAIL`, `GLOBAL_MANAGER`, `GUID_PATH`, `ID_NUM`, `JSON_STR`, `LOGIN_NAME`, `MANAGER_LEVEL`, `MOBILE`, `NAME`, `ORDERED_PATH`, `ORIGINAL`, `ORIGINAL_ID`, `PARENT_ID`, `PASSWORD`, `PERSON_ID`, `PERSON_TYPE`, `POSITIONS`, `ROLES`, `SEX`, `TENANT_ID`, `TENANT_NAME`, `TENANT_SHORT_NAME`, `VERSION`, `WEIXIN_ID`) VALUES ('1563254093235884032','2023-08-23 18:13:39.686000','2023-08-23 18:13:39.686000',NULL,NULL,'cn=安全审计员,o=组织',NULL,1,'11111111-1111-1111-1111-111111111115,11111111-1111-1111-1111-111111111119',NULL,NULL,'auditManager',3,NULL,'安全审计员',NULL,1,NULL,'11111111-1111-1111-1111-111111111115','$2a$10$Iw0Jh4MK4fqo.kMYZPiMteeoC4x8QanQVxXzRgq5SbZZxYQIYhV4e','11111111-1111-1111-1111-111111111119','deptPerson',NULL,NULL,1,'11111111-1111-1111-1111-111111111113','default','default',0,NULL);
/*!40000 ALTER TABLE `Y9_COMMON_ACCOUNT` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_APP_ICON`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_APP_ICON` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `ICON_DATA` longtext COMMENT '图标图片的base64',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `PATH` varchar(20) DEFAULT NULL COMMENT '地址：Y9FileStore 的 id',
  `REMARK` varchar(200) DEFAULT NULL COMMENT '备注',
  `ICON_TYPE` varchar(38) DEFAULT NULL COMMENT '类型',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图标实体表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_APP_ICON`
--

/*!40000 ALTER TABLE `Y9_COMMON_APP_ICON` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_COMMON_APP_ICON` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_APP_STORE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_APP_STORE` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(500) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ENABLED` int NOT NULL DEFAULT '1' COMMENT '启用状态:1=启用,0=禁用',
  `HIDDEN` int NOT NULL DEFAULT '0' COMMENT '是否隐藏:1=隐藏,0=显示',
  `ICON_URL` varchar(400) DEFAULT NULL COMMENT '图标地址',
  `INHERIT` int NOT NULL DEFAULT '0' COMMENT '是否为继承上级节点的权限:1=继承,0=不继承',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点ID',
  `RESOURCE_TYPE` int NOT NULL DEFAULT '0' COMMENT '资源类型：0=应用，1=菜单，2=操作',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统id',
  `TAB_INDEX` int NOT NULL COMMENT '排序',
  `URL` varchar(400) DEFAULT NULL COMMENT '链接地址',
  `URL2` varchar(400) DEFAULT NULL COMMENT '链接地址2',
  `ALIAS_NAME` varchar(255) DEFAULT NULL COMMENT '资源别名',
  `AUTO_INIT` int NOT NULL DEFAULT '0' COMMENT '是否自动租用应用',
  `CHECKED` int NOT NULL DEFAULT '0' COMMENT '是否审核通过',
  `ICON_DATA` longtext COMMENT '图标图片的base64',
  `NUMBER_URL` varchar(255) DEFAULT NULL COMMENT '获取数字的URL',
  `OPEN_TYPE` int DEFAULT NULL COMMENT '应用打开方式:0在桌面窗口打开；1在新浏览器窗口打开',
  `RESOURCE_ADMIN_URL` varchar(255) DEFAULT NULL COMMENT '资源管理的URL',
  `ROLE_ADMIN_URL` varchar(255) DEFAULT NULL COMMENT '角色管理的URL',
  `SHOW_NUMBER` int NOT NULL DEFAULT '0' COMMENT '是否显示右上角数字，0=不显示，1=显示',
  `TYPE` int DEFAULT NULL COMMENT '分类',
  `VERIFY_USER_NAME` varchar(30) DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='应用市场表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_APP_STORE`
--

/*!40000 ALTER TABLE `Y9_COMMON_APP_STORE` DISABLE KEYS */;
INSERT INTO `Y9_COMMON_APP_STORE` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `CUSTOM_ID`, `DESCRIPTION`, `ENABLED`, `HIDDEN`, `ICON_URL`, `INHERIT`, `NAME`, `PARENT_ID`, `RESOURCE_TYPE`, `SYSTEM_ID`, `TAB_INDEX`, `URL`, `URL2`, `ALIAS_NAME`, `AUTO_INIT`, `CHECKED`, `ICON_DATA`, `NUMBER_URL`, `OPEN_TYPE`, `RESOURCE_ADMIN_URL`, `ROLE_ADMIN_URL`, `SHOW_NUMBER`, `TYPE`, `VERIFY_USER_NAME`) VALUES ('11111111-1111-1111-1111-111111111112','2023-08-23 18:13:38.323000','2023-08-23 18:13:38.323000',NULL,NULL,1,0,NULL,0,'部门管理',NULL,0,'11111111-1111-1111-1111-111111111111',0,'http://localhost:7055/platform',NULL,'部门管理',0,0,NULL,NULL,1,NULL,NULL,0,2,NULL);
/*!40000 ALTER TABLE `Y9_COMMON_APP_STORE` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_DATASOURCE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_DATASOURCE` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `DRIVER` varchar(100) DEFAULT NULL COMMENT '驱动',
  `INITIAL_SIZE` int DEFAULT NULL COMMENT '数据库初始化大小',
  `JNDI_NAME` varchar(100) NOT NULL COMMENT '数据源名称',
  `MAX_ACTIVE` int DEFAULT NULL COMMENT '参数maxActive',
  `MIN_IDLE` int DEFAULT NULL COMMENT '参数minIdle',
  `PASSWORD` varchar(20) DEFAULT NULL COMMENT '密码',
  `TYPE` int DEFAULT NULL COMMENT '数据源类型1=jndi; 2=druid',
  `URL` varchar(300) DEFAULT NULL COMMENT '路径',
  `USERNAME` varchar(50) DEFAULT NULL COMMENT '用户名',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据源基本信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_DATASOURCE`
--

/*!40000 ALTER TABLE `Y9_COMMON_DATASOURCE` DISABLE KEYS */;
INSERT INTO `Y9_COMMON_DATASOURCE` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `DRIVER`, `INITIAL_SIZE`, `JNDI_NAME`, `MAX_ACTIVE`, `MIN_IDLE`, `PASSWORD`, `TYPE`, `URL`, `USERNAME`) VALUES ('11111111-1111-1111-1111-111111111114','2023-08-23 18:13:38.389000','2023-08-23 18:13:38.389000','com.mysql.cj.jdbc.Driver',1,'y9DefaultDs',20,1,'MTExMTEx',2,'jdbc:mysql://localhost:3306/y9_default?serverTimezone=GMT%2B8&nullCatalogMeansCurrent=true&useUnicode=true&characterEncoding=utf-8&rewriteBatchedStatements=true&useCompression=true&useSSL=false&allowPublicKeyRetrieval=true','root');
/*!40000 ALTER TABLE `Y9_COMMON_DATASOURCE` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_FILE_STORE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_FILE_STORE` (
  `STOREID` varchar(38) NOT NULL COMMENT '主键',
  `FILEENVELOPE` varchar(200) DEFAULT NULL COMMENT '文件数字信封: 即AES的随机密钥，然后进行RSA加密后的结果',
  `FILEEXT` varchar(50) DEFAULT NULL COMMENT '文件扩展名称',
  `FILENAME` varchar(600) DEFAULT NULL COMMENT '上传的文件名称',
  `FILESHA` varchar(200) DEFAULT NULL COMMENT '文件SHA值',
  `FILESIZE` bigint DEFAULT '0' COMMENT '文件长度',
  `FULLPATH` varchar(300) DEFAULT NULL COMMENT '绝对路径',
  `PREFIX` varchar(50) DEFAULT NULL COMMENT '根目录前缀',
  `REALFILENAME` varchar(100) DEFAULT NULL COMMENT '存放的文件名称',
  `STORETYPE` int DEFAULT NULL COMMENT '仓库类型',
  `SYSTEMNAME` varchar(50) DEFAULT NULL COMMENT '系统名称',
  `TENANTID` varchar(38) DEFAULT NULL COMMENT '租户Id',
  `UPLOADTIME` datetime(6) DEFAULT NULL COMMENT '上传时间',
  `UPLOADER` varchar(100) DEFAULT NULL COMMENT '上传人',
  `FILEURL` varchar(500) DEFAULT NULL COMMENT '完整的文件地址',
  PRIMARY KEY (`STOREID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文件仓库表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_FILE_STORE`
--

/*!40000 ALTER TABLE `Y9_COMMON_FILE_STORE` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_COMMON_FILE_STORE` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_MENU`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_MENU` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(500) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ENABLED` int NOT NULL DEFAULT '1' COMMENT '启用状态:1=启用,0=禁用',
  `HIDDEN` int NOT NULL DEFAULT '0' COMMENT '是否隐藏:1=隐藏,0=显示',
  `ICON_URL` varchar(400) DEFAULT NULL COMMENT '图标地址',
  `INHERIT` int NOT NULL DEFAULT '0' COMMENT '是否为继承上级节点的权限:1=继承,0=不继承',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点ID',
  `RESOURCE_TYPE` int NOT NULL DEFAULT '0' COMMENT '资源类型：0=应用，1=菜单，2=操作',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统id',
  `TAB_INDEX` int NOT NULL COMMENT '排序',
  `URL` varchar(400) DEFAULT NULL COMMENT '链接地址',
  `URL2` varchar(400) DEFAULT NULL COMMENT '链接地址2',
  `APP_ID` varchar(38) NOT NULL COMMENT '应用id',
  `COMPONENT` varchar(50) DEFAULT NULL COMMENT '菜单部件',
  `META` varchar(500) DEFAULT NULL COMMENT '元信息',
  `REDIRECT` varchar(50) DEFAULT NULL COMMENT '重定向',
  `TARGET` varchar(255) DEFAULT NULL COMMENT '打开模式',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='应用的菜单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_MENU`
--

/*!40000 ALTER TABLE `Y9_COMMON_MENU` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_COMMON_MENU` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_OPERATION`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_OPERATION` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(500) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ENABLED` int NOT NULL DEFAULT '1' COMMENT '启用状态:1=启用,0=禁用',
  `HIDDEN` int NOT NULL DEFAULT '0' COMMENT '是否隐藏:1=隐藏,0=显示',
  `ICON_URL` varchar(400) DEFAULT NULL COMMENT '图标地址',
  `INHERIT` int NOT NULL DEFAULT '0' COMMENT '是否为继承上级节点的权限:1=继承,0=不继承',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点ID',
  `RESOURCE_TYPE` int NOT NULL DEFAULT '0' COMMENT '资源类型：0=应用，1=菜单，2=操作',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统id',
  `TAB_INDEX` int NOT NULL COMMENT '排序',
  `URL` varchar(400) DEFAULT NULL COMMENT '链接地址',
  `URL2` varchar(400) DEFAULT NULL COMMENT '链接地址2',
  `APP_ID` varchar(38) NOT NULL COMMENT '应用id',
  `DISPLAY_TYPE` int DEFAULT '0' COMMENT '按钮展示方式',
  `EVENT_NAME` varchar(50) DEFAULT NULL COMMENT '按钮事件',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='页面按钮操作表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_OPERATION`
--

/*!40000 ALTER TABLE `Y9_COMMON_OPERATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_COMMON_OPERATION` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_SYSTEM`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_SYSTEM` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `AUTO_INIT` int NOT NULL DEFAULT '0' COMMENT '是否自动租用系统',
  `CN_NAME` varchar(50) NOT NULL COMMENT '系统中文名称',
  `CONTEXT_PATH` varchar(50) DEFAULT NULL COMMENT '系统程序上下文',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ENABLED` int DEFAULT NULL COMMENT '是否启用',
  `ISV_GUID` varchar(38) DEFAULT NULL COMMENT '开发商GUID',
  `NAME` varchar(50) NOT NULL COMMENT '系统名称',
  `SINGE_DATASOURCE` int NOT NULL DEFAULT '0' COMMENT '是否启用独立数据源',
  `TAB_INDEX` int NOT NULL COMMENT '排序',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_boc14m1ikormbxu58208b58gh` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_SYSTEM`
--

/*!40000 ALTER TABLE `Y9_COMMON_SYSTEM` DISABLE KEYS */;
INSERT INTO `Y9_COMMON_SYSTEM` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `AUTO_INIT`, `CN_NAME`, `CONTEXT_PATH`, `DESCRIPTION`, `ENABLED`, `ISV_GUID`, `NAME`, `SINGE_DATASOURCE`, `TAB_INDEX`) VALUES ('11111111-1111-1111-1111-111111111111','2023-08-23 18:13:38.175000','2023-08-23 18:13:38.175000',1,'开源内核','platform',NULL,1,NULL,'riseplatform',0,10000);
/*!40000 ALTER TABLE `Y9_COMMON_SYSTEM` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_TENANT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_TENANT` (
  `ID` varchar(38) NOT NULL COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `DEFAULT_DATA_SOURCE_ID` varchar(38) DEFAULT NULL COMMENT '默认的租户数据源id，对应Y9_COMMON_DATASOURCE表的id字段',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ENABLED` int NOT NULL DEFAULT '1' COMMENT '是否启用',
  `FOOTER` varchar(150) DEFAULT NULL COMMENT '门户页尾显示信息',
  `GUID_PATH` varchar(800) DEFAULT NULL COMMENT '由ID组成的父子关系列表，之间用逗号分隔',
  `LOGO_ICON` varchar(255) DEFAULT NULL COMMENT '租户logo',
  `NAME` varchar(200) NOT NULL COMMENT '租户中文名称',
  `NAME_PATH` varchar(800) DEFAULT NULL COMMENT '由shortName组成的父子关系列表，之间用逗号分隔',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `SERIAL` int DEFAULT NULL COMMENT '租户顺序号，自增字段',
  `SHORT_NAME` varchar(200) NOT NULL COMMENT '租户英文名称',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_TYPE` int DEFAULT NULL COMMENT '租户类型： 0=超级用户，1=运维团队，2=开发商，3=普通租户',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_TENANT`
--

/*!40000 ALTER TABLE `Y9_COMMON_TENANT` DISABLE KEYS */;
INSERT INTO `Y9_COMMON_TENANT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `DEFAULT_DATA_SOURCE_ID`, `DESCRIPTION`, `ENABLED`, `FOOTER`, `GUID_PATH`, `LOGO_ICON`, `NAME`, `NAME_PATH`, `PARENT_ID`, `SERIAL`, `SHORT_NAME`, `TAB_INDEX`, `TENANT_TYPE`) VALUES ('11111111-1111-1111-1111-111111111113','2023-08-23 18:13:38.413000','2023-08-23 18:13:38.413000','11111111-1111-1111-1111-111111111114',NULL,1,NULL,'11111111-1111-1111-1111-111111111113',NULL,'default','default',NULL,NULL,'default',10000,3);
/*!40000 ALTER TABLE `Y9_COMMON_TENANT` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_TENANT_APP`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_TENANT_APP` (
  `ID` varchar(38) NOT NULL COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) NOT NULL COMMENT '应用id',
  `APP_NAME` varchar(200) DEFAULT NULL COMMENT '应用名称',
  `APPLY_ID` varchar(38) DEFAULT NULL COMMENT '申请人Id',
  `APPLY_NAME` varchar(200) DEFAULT NULL COMMENT '申请人',
  `APPLY_REASON` varchar(355) DEFAULT NULL COMMENT '申请理由',
  `DELETED_NAME` varchar(255) DEFAULT NULL COMMENT '删除人',
  `DELETED_TIME` datetime(6) DEFAULT NULL COMMENT '删除时间',
  `REASON` varchar(255) DEFAULT NULL COMMENT '未通过缘由',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统Id',
  `TENANCY` int NOT NULL DEFAULT '0' COMMENT '租户是否租用状态。用于判断有效或失效的状态',
  `TENANT_ID` varchar(38) NOT NULL COMMENT '租户id',
  `TENANT_NAME` varchar(200) DEFAULT NULL COMMENT '租户名称',
  `VERIFY_STATUS` bit(1) DEFAULT NULL COMMENT '审核状态，0：未审核；1：通过',
  `VERIFY_TIME` varchar(50) DEFAULT NULL COMMENT '审核时间',
  `VERIFY_USER_NAME` varchar(200) DEFAULT NULL COMMENT '审核人',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户应用信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_TENANT_APP`
--

/*!40000 ALTER TABLE `Y9_COMMON_TENANT_APP` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_COMMON_TENANT_APP` ENABLE KEYS */;

--
-- Table structure for table `Y9_COMMON_TENANT_SYSTEM`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_COMMON_TENANT_SYSTEM` (
  `ID` varchar(38) NOT NULL COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `INITIALIZED` int NOT NULL DEFAULT '0' COMMENT '租户数据已经初始化',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统id',
  `TENANT_DATA_SOURCE` varchar(38) NOT NULL COMMENT '数据源id',
  `TENANT_ID` varchar(38) NOT NULL COMMENT '租户id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='租户系统表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_COMMON_TENANT_SYSTEM`
--

/*!40000 ALTER TABLE `Y9_COMMON_TENANT_SYSTEM` DISABLE KEYS */;
INSERT INTO `Y9_COMMON_TENANT_SYSTEM` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `INITIALIZED`, `SYSTEM_ID`, `TENANT_DATA_SOURCE`, `TENANT_ID`) VALUES ('1563254088001392640','2023-08-23 18:13:38.441000','2023-08-23 18:13:38.441000',1,'11111111-1111-1111-1111-111111111111','11111111-1111-1111-1111-111111111114','11111111-1111-1111-1111-111111111113');
/*!40000 ALTER TABLE `Y9_COMMON_TENANT_SYSTEM` ENABLE KEYS */;

--
-- Table structure for table `Y9_EVENT_PUBLISHEDEVENT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_EVENT_PUBLISHEDEVENT` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CLIENT_IP` varchar(255) NOT NULL COMMENT '操作者的客户端ip',
  `ENTITY_JSON` longtext COMMENT '事件处理对象实体类信息',
  `EVENT_DESCRIPTION` varchar(255) NOT NULL COMMENT '具体事件描述',
  `EVENT_NAME` varchar(255) NOT NULL COMMENT '事件类型名称',
  `EVENT_TYPE` varchar(255) NOT NULL COMMENT '事件类型',
  `OBJ_ID` varchar(255) DEFAULT NULL COMMENT '事件处理对象id',
  `OPERATOR` varchar(255) NOT NULL COMMENT '事件操作者',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事件信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_EVENT_PUBLISHEDEVENT`
--

/*!40000 ALTER TABLE `Y9_EVENT_PUBLISHEDEVENT` DISABLE KEYS */;
INSERT INTO `Y9_EVENT_PUBLISHEDEVENT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `CLIENT_IP`, `ENTITY_JSON`, `EVENT_DESCRIPTION`, `EVENT_NAME`, `EVENT_TYPE`, `OBJ_ID`, `OPERATOR`, `TENANT_ID`) VALUES ('1563254090077573120','2023-08-23 18:13:38.932000',NULL,'192.168.3.3','{\"orgType\":\"Organization\",\"id\":\"11111111-1111-1111-1111-111111111115\",\"parentId\":null,\"tenantId\":\"11111111-1111-1111-1111-111111111113\",\"createTime\":\"2023-08-23 10:11:04\",\"updateTime\":\"2023-08-23 10:11:04\",\"disabled\":false,\"description\":null,\"customId\":null,\"dn\":\"o=组织\",\"name\":\"组织\",\"orgType\":\"Organization\",\"properties\":null,\"tabIndex\":10000,\"guidPath\":\"11111111-1111-1111-1111-111111111115\",\"enName\":\"org\",\"organizationCode\":null,\"organizationType\":null,\"virtual\":true}','更新组织','更新组织机构','RISEORGEVENT_TYPE_UPDATE_ORGANIZATION','11111111-1111-1111-1111-111111111115','系统','11111111-1111-1111-1111-111111111113');
INSERT INTO `Y9_EVENT_PUBLISHEDEVENT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `CLIENT_IP`, `ENTITY_JSON`, `EVENT_DESCRIPTION`, `EVENT_NAME`, `EVENT_TYPE`, `OBJ_ID`, `OPERATOR`, `TENANT_ID`) VALUES ('1563254090157264896','2023-08-23 18:13:38.950000',NULL,'192.168.3.3','{\"orgType\":\"Organization\",\"id\":\"11111111-1111-1111-1111-111111111123\",\"parentId\":null,\"tenantId\":\"11111111-1111-1111-1111-111111111113\",\"createTime\":\"2023-08-23 10:11:04\",\"updateTime\":\"2023-08-23 10:11:04\",\"disabled\":false,\"description\":null,\"customId\":null,\"dn\":\"o=业务组织\",\"name\":\"业务组织\",\"orgType\":\"Organization\",\"properties\":null,\"tabIndex\":10000,\"guidPath\":\"11111111-1111-1111-1111-111111111123\",\"enName\":\"org\",\"organizationCode\":null,\"organizationType\":null,\"virtual\":false}','更新业务组织','更新组织机构','RISEORGEVENT_TYPE_UPDATE_ORGANIZATION','11111111-1111-1111-1111-111111111123','系统','11111111-1111-1111-1111-111111111113');
INSERT INTO `Y9_EVENT_PUBLISHEDEVENT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `CLIENT_IP`, `ENTITY_JSON`, `EVENT_DESCRIPTION`, `EVENT_NAME`, `EVENT_TYPE`, `OBJ_ID`, `OPERATOR`, `TENANT_ID`) VALUES ('1563254090257928192','2023-08-23 18:13:38.974000',NULL,'192.168.3.3','{\"id\":\"11111111-1111-1111-1111-111111111122\",\"code\":\"001\",\"name\":\"普通职位\",\"tabIndex\":1}','新增职位普通职位','新增职位信息','RISEORGEVENT_TYPE_ADD_JOB','','系统','11111111-1111-1111-1111-111111111113');
INSERT INTO `Y9_EVENT_PUBLISHEDEVENT` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `CLIENT_IP`, `ENTITY_JSON`, `EVENT_DESCRIPTION`, `EVENT_NAME`, `EVENT_TYPE`, `OBJ_ID`, `OPERATOR`, `TENANT_ID`) VALUES ('1563254090845130752','2023-08-23 18:13:39.115000',NULL,'192.168.3.3','{\"orgType\":\"Person\",\"id\":\"11111111-1111-1111-1111-111111111116\",\"parentId\":\"11111111-1111-1111-1111-111111111123\",\"tenantId\":\"11111111-1111-1111-1111-111111111113\",\"createTime\":null,\"updateTime\":null,\"disabled\":false,\"description\":null,\"customId\":null,\"dn\":\"cn=user,o=业务组织\",\"name\":\"业务用户\",\"orgType\":\"Person\",\"properties\":null,\"tabIndex\":10003,\"guidPath\":\"11111111-1111-1111-1111-111111111123,11111111-1111-1111-1111-111111111116\",\"loginName\":\"user\",\"password\":null,\"avator\":null,\"official\":null,\"officialType\":null,\"duty\":null,\"dutyLevel\":null,\"dutyLevelName\":null,\"caid\":null,\"email\":null,\"sex\":1,\"province\":null,\"officeAddress\":null,\"officePhone\":null,\"officeFax\":null,\"mobile\":\"13551111111\",\"roles\":null,\"positions\":null,\"positionId\":null,\"personType\":\"单位用户\",\"weixinId\":null,\"orderedPath\":null,\"original\":true,\"originalId\":null}','更新业务用户','更新人员信息','RISEORGEVENT_TYPE_UPDATE_PERSON','11111111-1111-1111-1111-111111111116','系统','11111111-1111-1111-1111-111111111113');
/*!40000 ALTER TABLE `Y9_EVENT_PUBLISHEDEVENT` ENABLE KEYS */;

--
-- Table structure for table `Y9_EVENT_PUBLISHEDEVENTLISTENER`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_EVENT_PUBLISHEDEVENTLISTENER` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CLIENT_IP` varchar(255) NOT NULL COMMENT '事件消费者ip',
  `EVENT_TYPE` varchar(255) NOT NULL COMMENT '事件类型',
  `SYSTEM_NAME` varchar(255) NOT NULL COMMENT '应用名称',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事件监听信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_EVENT_PUBLISHEDEVENTLISTENER`
--

/*!40000 ALTER TABLE `Y9_EVENT_PUBLISHEDEVENTLISTENER` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_EVENT_PUBLISHEDEVENTLISTENER` ENABLE KEYS */;

--
-- Table structure for table `Y9_EVENT_SYNC_HISTORY`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_EVENT_SYNC_HISTORY` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_NAME` varchar(255) DEFAULT NULL COMMENT '应用名称事件操作者',
  `LAST_SYNC_TIME` datetime(6) DEFAULT NULL COMMENT '上一次同步时间',
  `TENANT_ID` varchar(38) NOT NULL COMMENT '租户id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事件监听api获取记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_EVENT_SYNC_HISTORY`
--

/*!40000 ALTER TABLE `Y9_EVENT_SYNC_HISTORY` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_EVENT_SYNC_HISTORY` ENABLE KEYS */;

--
-- Table structure for table `Y9_LOGIN_USER`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_LOGIN_USER` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `BROWSER_NAME` varchar(255) DEFAULT NULL COMMENT '浏览器名称',
  `BROWSER_VERSION` varchar(255) DEFAULT NULL COMMENT '浏览器版本',
  `CLIENT_IP_SECTION` varchar(50) DEFAULT NULL COMMENT 'IP地址区间段',
  `LOG_MESSAGE` varchar(255) DEFAULT NULL COMMENT '日志信息',
  `LOGIN_TIME` datetime(6) DEFAULT NULL COMMENT '登录日期',
  `LOGIN_TYPE` varchar(255) DEFAULT NULL COMMENT '登录类型',
  `MANAGER_LEVEL` varchar(255) NOT NULL DEFAULT '0' COMMENT '管理员类型',
  `OS_NAME` varchar(255) DEFAULT NULL COMMENT 'OS名称',
  `SCREEN_RESOLUTION` varchar(255) DEFAULT NULL COMMENT '用户主机屏幕分辨率',
  `SERVER_IP` varchar(255) DEFAULT NULL COMMENT '服务器IP',
  `SUCCESS` varchar(255) DEFAULT NULL COMMENT '成功标志',
  `TENANT_ID` varchar(255) DEFAULT NULL COMMENT '租户ID',
  `TENANT_NAME` varchar(255) DEFAULT NULL COMMENT '租户名称',
  `USER_HOST_DISKID` varchar(255) DEFAULT NULL COMMENT '用户主机磁盘ID',
  `USER_HOST_IP` varchar(255) DEFAULT NULL COMMENT '用户主机IP',
  `USER_HOST_MAC` varchar(255) DEFAULT NULL COMMENT '用户主机MAC',
  `USER_HOST_NAME` varchar(255) DEFAULT NULL COMMENT '用户主机名称',
  `USER_ID` varchar(255) DEFAULT NULL COMMENT '用户ID',
  `USER_LOGIN_NAME` varchar(255) DEFAULT NULL COMMENT '用户登录名称',
  `USER_NAME` varchar(255) DEFAULT NULL COMMENT '用户名称',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登录历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_LOGIN_USER`
--

/*!40000 ALTER TABLE `Y9_LOGIN_USER` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_LOGIN_USER` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_AUTHORIZATION`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_AUTHORIZATION` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `AUTHORITY` int NOT NULL COMMENT '权限类型',
  `AUTHORIZER` varchar(30) DEFAULT NULL COMMENT '授权人',
  `PRINCIPAL_ID` varchar(38) NOT NULL COMMENT '授权主体的唯一标识',
  `PRINCIPAL_NAME` varchar(255) DEFAULT NULL COMMENT '授权主体的名称。冗余字段，纯显示用',
  `PRINCIPAL_TYPE` int NOT NULL DEFAULT '0' COMMENT '授权主体类型:0=角色；1=岗位；2=人员,3=组，4=部门，5=组织',
  `RESOURCE_ID` varchar(38) NOT NULL COMMENT '资源唯一标识符',
  `RESOURCE_NAME` varchar(255) DEFAULT NULL COMMENT '资源名称。冗余字段，纯显示用',
  `RESOURCE_TYPE` int DEFAULT NULL COMMENT '资源类型。冗余字段，纯显示用',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKpv5gy69bjyoflhhexyix9d8gt` (`PRINCIPAL_ID`,`RESOURCE_ID`,`AUTHORITY`),
  KEY `IDX53q7nmihwr3yfeu8ribdd6sb` (`TENANT_ID`,`PRINCIPAL_ID`),
  KEY `IDXrgw1eknlb0a7kg5xonypd52ag` (`TENANT_ID`,`RESOURCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='权限配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_AUTHORIZATION`
--

/*!40000 ALTER TABLE `Y9_ORG_AUTHORIZATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_AUTHORIZATION` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_DEPARTMENT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_DEPARTMENT` (
  `ID` varchar(38) NOT NULL COMMENT 'UUID字段',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DISABLED` int NOT NULL DEFAULT '0' COMMENT '是否可用',
  `DN` varchar(2000) DEFAULT NULL COMMENT '由name组成的父子关系列表(倒序)，之间用逗号分隔',
  `GUID_PATH` varchar(400) DEFAULT NULL COMMENT '由ID组成的父子关系列表(正序)，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `VERSION` int DEFAULT NULL COMMENT '版本号,乐观锁',
  `ALIAS_NAME` varchar(255) DEFAULT NULL COMMENT '部门简称',
  `BUREAU` int NOT NULL DEFAULT '0' COMMENT '是否委办局',
  `DEPT_ADDRESS` varchar(255) DEFAULT NULL COMMENT '部门地址',
  `DEPT_FAX` varchar(255) DEFAULT NULL COMMENT '传真号码',
  `DEPT_GIVEN_NAME` varchar(255) DEFAULT NULL COMMENT '特定名称',
  `DEPT_OFFICE` varchar(255) DEFAULT NULL COMMENT '办公室',
  `DEPT_PHONE` varchar(255) DEFAULT NULL COMMENT '电话号码',
  `DEPT_TYPE` varchar(255) DEFAULT NULL COMMENT '部门类型',
  `DEPT_TYPE_NAME` varchar(255) DEFAULT NULL COMMENT '部门类型名称',
  `DIVISION_SCODE` varchar(255) DEFAULT NULL COMMENT '区域代码',
  `EN_NAME` varchar(255) DEFAULT NULL COMMENT '英文名称',
  `ESTABLISH_DATE` date DEFAULT NULL COMMENT '成立时间',
  `GRADE_CODE` varchar(255) DEFAULT NULL COMMENT '等级编码',
  `GRADE_CODE_NAME` varchar(255) DEFAULT NULL COMMENT '等级名称',
  `ZIP_CODE` varchar(255) DEFAULT NULL COMMENT '邮政编码',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_3rame1rxiq6j18b20rqfj8eyn` (`GUID_PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门实体表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_DEPARTMENT`
--

/*!40000 ALTER TABLE `Y9_ORG_DEPARTMENT` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_DEPARTMENT` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_DEPARTMENT_PROP`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_DEPARTMENT_PROP` (
  `ID` varchar(38) NOT NULL COMMENT '唯一标示',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CATEGORY` int NOT NULL COMMENT '类别',
  `DEPT_ID` varchar(50) NOT NULL COMMENT '部门唯一标示',
  `ORG_BASE_ID` varchar(50) NOT NULL COMMENT '组织唯一标示',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门信息配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_DEPARTMENT_PROP`
--

/*!40000 ALTER TABLE `Y9_ORG_DEPARTMENT_PROP` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_DEPARTMENT_PROP` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_GROUP`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_GROUP` (
  `ID` varchar(38) NOT NULL COMMENT 'UUID字段',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DISABLED` int NOT NULL DEFAULT '0' COMMENT '是否可用',
  `DN` varchar(2000) DEFAULT NULL COMMENT '由name组成的父子关系列表(倒序)，之间用逗号分隔',
  `GUID_PATH` varchar(400) DEFAULT NULL COMMENT '由ID组成的父子关系列表(正序)，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `VERSION` int DEFAULT NULL COMMENT '版本号,乐观锁',
  `TYPE` varchar(10) NOT NULL DEFAULT 'person' COMMENT '类型：position、person',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_j3g3vc1ffuwb0l25xiwc0gue3` (`GUID_PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户组表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_GROUP`
--

/*!40000 ALTER TABLE `Y9_ORG_GROUP` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_GROUP` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_JOB`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_JOB` (
  `ID` varchar(38) NOT NULL COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CODE` varchar(255) NOT NULL COMMENT '数据代码',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='职位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_JOB`
--

/*!40000 ALTER TABLE `Y9_ORG_JOB` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_JOB` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_MANAGER`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_MANAGER` (
  `ID` varchar(38) NOT NULL COMMENT 'UUID字段',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DISABLED` int NOT NULL DEFAULT '0' COMMENT '是否可用',
  `DN` varchar(2000) DEFAULT NULL COMMENT '由name组成的父子关系列表(倒序)，之间用逗号分隔',
  `GUID_PATH` varchar(400) DEFAULT NULL COMMENT '由ID组成的父子关系列表(正序)，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `VERSION` int DEFAULT NULL COMMENT '版本号,乐观锁',
  `AVATOR` varchar(100) DEFAULT NULL COMMENT '头像',
  `CHECK_CYCLE` int DEFAULT NULL COMMENT '审查周期',
  `CHECK_TIME` varchar(50) DEFAULT NULL COMMENT '审查时间',
  `EMAIL` varchar(255) DEFAULT NULL COMMENT '电子邮箱',
  `GLOBAL_MANAGER` int NOT NULL DEFAULT '0' COMMENT '是否全局管理员',
  `LOGIN_NAME` varchar(255) NOT NULL COMMENT '登录名',
  `MANAGER_LEVEL` int NOT NULL DEFAULT '0' COMMENT '管理员类型',
  `MOBILE` varchar(255) DEFAULT NULL COMMENT '手机号码',
  `MODIFY_PWD_TIME` varchar(50) DEFAULT NULL COMMENT '修改密码时间',
  `ORDERED_PATH` varchar(500) DEFAULT NULL COMMENT '排序序列号',
  `PASSWORD` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `PWD_CYCLE` int DEFAULT NULL COMMENT '修改密码周期（天）',
  `SEX` int NOT NULL DEFAULT '1' COMMENT '性别',
  `USER_HOST_IP` varchar(150) DEFAULT NULL COMMENT '允许访问的客户端IP',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_cmcf8n9689uc3cxmj0ji2xdqf` (`GUID_PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='三员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_MANAGER`
--

/*!40000 ALTER TABLE `Y9_ORG_MANAGER` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_MANAGER` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_OPTIONCLASS`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_OPTIONCLASS` (
  `TYPE` varchar(255) NOT NULL COMMENT '主键，类型名称',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `NAME` varchar(255) NOT NULL COMMENT '中文名称',
  PRIMARY KEY (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_OPTIONCLASS`
--

/*!40000 ALTER TABLE `Y9_ORG_OPTIONCLASS` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_OPTIONCLASS` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_OPTIONVALUE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_OPTIONVALUE` (
  `ID` varchar(38) NOT NULL COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CODE` varchar(255) NOT NULL COMMENT '数据代码',
  `NAME` varchar(255) NOT NULL COMMENT '主键名称',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TYPE` varchar(255) NOT NULL COMMENT '字典类型',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_OPTIONVALUE`
--

/*!40000 ALTER TABLE `Y9_ORG_OPTIONVALUE` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_OPTIONVALUE` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_ORGANIZATION`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_ORGANIZATION` (
  `ID` varchar(38) NOT NULL COMMENT 'UUID字段',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DISABLED` int NOT NULL DEFAULT '0' COMMENT '是否可用',
  `DN` varchar(2000) DEFAULT NULL COMMENT '由name组成的父子关系列表(倒序)，之间用逗号分隔',
  `GUID_PATH` varchar(400) DEFAULT NULL COMMENT '由ID组成的父子关系列表(正序)，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `VERSION` int DEFAULT NULL COMMENT '版本号,乐观锁',
  `EN_NAME` varchar(255) DEFAULT NULL COMMENT '英文名称',
  `ORGANIZATION_CODE` varchar(255) DEFAULT NULL COMMENT '组织机构代码',
  `ORGANIZATION_TYPE` varchar(255) DEFAULT NULL COMMENT '组织机构类型',
  `VIRTUALIZED` int NOT NULL DEFAULT '0' COMMENT '类型:0=实体组织，1=虚拟组织',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_jlnficawh29fkypg7dj2cf4bg` (`GUID_PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织机构实体表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_ORGANIZATION`
--

/*!40000 ALTER TABLE `Y9_ORG_ORGANIZATION` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_ORGANIZATION` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_ORGBASES_ROLES`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_ORGBASES_ROLES` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `NEGATIVE` int NOT NULL DEFAULT '0' COMMENT '是否为负角色关联',
  `ORG_ID` varchar(38) NOT NULL COMMENT '人员或部门组织机构等唯一标识',
  `ORG_ORDER` int NOT NULL COMMENT '关联排序号',
  `ORG_TYPE` varchar(255) DEFAULT NULL COMMENT '组织类型',
  `ORG_PARENT_ID` varchar(255) DEFAULT NULL COMMENT '父节点唯一标识',
  `ROLE_ID` varchar(38) NOT NULL COMMENT '角色id',
  PRIMARY KEY (`ID`),
  KEY `IDX1q0y1dxaypjqxoal65vshj214` (`ROLE_ID`,`ORG_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='组织节点与角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_ORGBASES_ROLES`
--

/*!40000 ALTER TABLE `Y9_ORG_ORGBASES_ROLES` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_ORGBASES_ROLES` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERMISSION_DATAFIELD`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERMISSION_DATAFIELD` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) DEFAULT NULL COMMENT '应用id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `HIDDEN_FIELDS` varchar(1000) DEFAULT NULL COMMENT '不可见字段列表（之间用逗号隔开）',
  `NAME` varchar(255) DEFAULT NULL COMMENT '名称',
  `READONLY_FIELDS` varchar(1000) DEFAULT NULL COMMENT '只读字段列表（之间用逗号隔开）',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据列权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERMISSION_DATAFIELD`
--

/*!40000 ALTER TABLE `Y9_ORG_PERMISSION_DATAFIELD` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERMISSION_DATAFIELD` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERMISSION_DATAROW`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERMISSION_DATAROW` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) DEFAULT NULL COMMENT '应用id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `LIMIT_CLAUSE` varchar(2000) DEFAULT NULL COMMENT '数据范围',
  `NAME` varchar(255) DEFAULT NULL COMMENT '名称',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='数据行权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERMISSION_DATAROW`
--

/*!40000 ALTER TABLE `Y9_ORG_PERMISSION_DATAROW` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERMISSION_DATAROW` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSON`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSON` (
  `ID` varchar(38) NOT NULL COMMENT 'UUID字段',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DISABLED` int NOT NULL DEFAULT '0' COMMENT '是否可用',
  `DN` varchar(2000) DEFAULT NULL COMMENT '由name组成的父子关系列表(倒序)，之间用逗号分隔',
  `GUID_PATH` varchar(400) DEFAULT NULL COMMENT '由ID组成的父子关系列表(正序)，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `VERSION` int DEFAULT NULL COMMENT '版本号,乐观锁',
  `AVATOR` varchar(100) DEFAULT NULL COMMENT '头像',
  `CAID` varchar(255) DEFAULT NULL COMMENT 'CA认证码',
  `EMAIL` varchar(255) DEFAULT NULL COMMENT '电子邮箱',
  `LOGIN_NAME` varchar(255) NOT NULL COMMENT '登录名',
  `MOBILE` varchar(255) NOT NULL COMMENT '手机号码',
  `OFFICE_ADDRESS` varchar(255) DEFAULT NULL COMMENT '办公地址',
  `OFFICE_FAX` varchar(255) DEFAULT NULL COMMENT '办公传真',
  `OFFICE_PHONE` varchar(255) DEFAULT NULL COMMENT '办公电话',
  `OFFICIAL` int DEFAULT NULL COMMENT '是否在编',
  `OFFICIAL_TYPE` varchar(255) DEFAULT NULL COMMENT '编制类型',
  `ORDERED_PATH` varchar(500) DEFAULT NULL COMMENT '排序序列号',
  `ORIGINAL` int NOT NULL DEFAULT '1' COMMENT '0:添加的人员，1：新增的人员',
  `ORIGINAL_ID` varchar(255) DEFAULT NULL COMMENT '原始人员id',
  `PASSWORD` varchar(255) DEFAULT NULL COMMENT '登录密码',
  `PERSON_TYPE` varchar(255) NOT NULL DEFAULT 'deptPerson' COMMENT '人员类型',
  `SEX` int NOT NULL DEFAULT '1' COMMENT '性别',
  `WEIXIN_ID` varchar(255) DEFAULT NULL COMMENT '人员绑定微信的唯一标识',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_8k5uhooq866n687vvcv37ufrh` (`GUID_PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSON`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSON` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSON` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSON_CUSTOMGROUP`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSON_CUSTOMGROUP` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `GROUP_NAME` varchar(50) NOT NULL COMMENT '群组名称',
  `USER_ID` varchar(38) DEFAULT NULL COMMENT '用户id',
  `USER_NAME` varchar(255) DEFAULT NULL COMMENT '用户名称',
  `SHARE_ID` varchar(38) DEFAULT NULL COMMENT '分享人Id',
  `SHARE_NAME` varchar(255) DEFAULT NULL COMMENT '分享人',
  `TAB_INDEX` int NOT NULL COMMENT '排序字段',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='自定义群组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSON_CUSTOMGROUP`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSON_CUSTOMGROUP` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSON_CUSTOMGROUP` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSON_CUSTOMGROUP_MEMBER`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSON_CUSTOMGROUP_MEMBER` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `GROUP_ID` varchar(38) DEFAULT NULL COMMENT '所在群组id',
  `MEMBER_ID` varchar(38) NOT NULL COMMENT '成员id',
  `MEMBER_NAME` varchar(255) NOT NULL COMMENT '成员名称',
  `MEMBER_TYPE` varchar(255) DEFAULT NULL COMMENT '成员类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '所在组织架构父节点id',
  `SEX` int DEFAULT NULL COMMENT '性别',
  `TAB_INDEX` int NOT NULL COMMENT '排序',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='自定义群组成员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSON_CUSTOMGROUP_MEMBER`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSON_CUSTOMGROUP_MEMBER` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSON_CUSTOMGROUP_MEMBER` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSON_EXT`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSON_EXT` (
  `PERSON_ID` varchar(38) NOT NULL COMMENT '人员ID',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `BIRTHDAY` date DEFAULT NULL COMMENT '出生年月',
  `CITY` varchar(255) DEFAULT NULL COMMENT '居住城市',
  `COUNTRY` varchar(255) DEFAULT NULL COMMENT '居住国家',
  `EDUCATION` varchar(255) DEFAULT NULL COMMENT '学历',
  `HOME_ADDRESS` varchar(255) DEFAULT NULL COMMENT '家庭地址',
  `HOME_PHONE` varchar(255) DEFAULT NULL COMMENT '家庭电话',
  `ID_NUM` varchar(255) DEFAULT NULL COMMENT '证件号码',
  `ID_TYPE` varchar(255) DEFAULT NULL COMMENT '证件类型',
  `MARITAL_STATUS` int NOT NULL DEFAULT '0' COMMENT '婚姻状况',
  `NAME` varchar(255) NOT NULL COMMENT '登录名，拥于查找',
  `PHOTO` longblob COMMENT '照片',
  `POLITICAL_STATUS` varchar(255) DEFAULT NULL COMMENT '政治面貌',
  `PROFESSIONAL` varchar(255) DEFAULT NULL COMMENT '专业',
  `PROVINCE` varchar(255) DEFAULT NULL COMMENT '人员籍贯',
  `SIGN` longblob COMMENT '签名',
  `WORK_TIME` date DEFAULT NULL COMMENT '入职时间',
  PRIMARY KEY (`PERSON_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员信息扩展表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSON_EXT`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSON_EXT` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSON_EXT` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSONS_GROUPS`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSONS_GROUPS` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `GROUP_ID` varchar(38) NOT NULL COMMENT '用户组id',
  `GROUP_ORDER` int NOT NULL COMMENT '用户组排序号',
  `PERSON_ID` varchar(38) NOT NULL COMMENT '人员ID',
  `PERSON_ORDER` int NOT NULL COMMENT '人员排序号',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKbk6jbj6n5gui7fd6e2ljak95k` (`GROUP_ID`,`PERSON_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员用户组关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSONS_GROUPS`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSONS_GROUPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSONS_GROUPS` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSONS_POSITIONS`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSONS_POSITIONS` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `PERSON_ID` varchar(38) NOT NULL COMMENT '人员ID',
  `PERSON_ORDER` int NOT NULL DEFAULT '0' COMMENT '人员排序号',
  `POSITION_ID` varchar(38) NOT NULL COMMENT '岗位ID',
  `POSITION_ORDER` int NOT NULL DEFAULT '0' COMMENT '岗位排序号',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKrty6axgf79bgsw4vxtki54i3c` (`POSITION_ID`,`PERSON_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSONS_POSITIONS`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSONS_POSITIONS` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSONS_POSITIONS` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSONS_RESOURCES`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSONS_RESOURCES` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) NOT NULL COMMENT '应用id',
  `APP_NAME` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `AUTHORITY` int NOT NULL COMMENT '权限类型',
  `AUTHORIZATION_ID` varchar(38) DEFAULT NULL COMMENT '权限配置id',
  `INHERIT` int NOT NULL COMMENT '资源是否为继承上级节点的权限。冗余字段，纯显示用',
  `PARENT_RESOURCE_ID` varchar(38) DEFAULT NULL COMMENT '父资源id',
  `RESOURCE_CUSTOM_ID` varchar(38) DEFAULT NULL COMMENT '资源自定义id',
  `RESOURCE_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `RESOURCE_ID` varchar(38) NOT NULL COMMENT '资源id',
  `RESOURCE_NAME` varchar(255) DEFAULT NULL COMMENT '资源名称',
  `RESOURCE_TAB_INDEX` int NOT NULL COMMENT '排序',
  `RESOURCE_TYPE` int NOT NULL COMMENT '资源类型：0=应用，1=菜单，2=操作',
  `RESOURCE_URL` varchar(255) DEFAULT NULL COMMENT '资源URL',
  `SYSTEM_CN_NAME` varchar(255) DEFAULT NULL COMMENT '系统中文名称',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统id',
  `SYSTEM_NAME` varchar(255) DEFAULT NULL COMMENT '系统英文名称',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `PERSON_ID` varchar(38) NOT NULL COMMENT '身份(人员)唯一标识',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKn2p2pe8jbhlmtlxv8ixn8cw7d` (`PERSON_ID`,`RESOURCE_ID`,`AUTHORIZATION_ID`,`AUTHORITY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员与（资源、权限）关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSONS_RESOURCES`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSONS_RESOURCES` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSONS_RESOURCES` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_PERSONS_ROLES`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_PERSONS_ROLES` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) DEFAULT NULL COMMENT '应用id',
  `APP_NAME` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `DEPARTMENT_ID` varchar(38) NOT NULL COMMENT '部门id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ROLE_CUSTOM_ID` varchar(38) DEFAULT NULL COMMENT '角色自定义id',
  `ROLE_ID` varchar(38) NOT NULL COMMENT '角色id',
  `ROLE_NAME` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `SYSTEM_CN_NAME` varchar(255) DEFAULT NULL COMMENT '系统中文名称',
  `SYSTEM_ID` varchar(38) DEFAULT NULL COMMENT '系统id',
  `SYSTEM_NAME` varchar(255) DEFAULT NULL COMMENT '系统英文名称',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `PERSON_ID` varchar(38) NOT NULL COMMENT '身份(人员)唯一标识',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKa1ns3b527lyr0sn45sk036ts9` (`PERSON_ID`,`ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='人员与角色关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_PERSONS_ROLES`
--

/*!40000 ALTER TABLE `Y9_ORG_PERSONS_ROLES` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_PERSONS_ROLES` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_POSITION`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_POSITION` (
  `ID` varchar(38) NOT NULL COMMENT 'UUID字段',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT '自定义id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DISABLED` int NOT NULL DEFAULT '0' COMMENT '是否可用',
  `DN` varchar(2000) DEFAULT NULL COMMENT '由name组成的父子关系列表(倒序)，之间用逗号分隔',
  `GUID_PATH` varchar(400) DEFAULT NULL COMMENT '由ID组成的父子关系列表(正序)，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '名称',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点id',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `TAB_INDEX` int NOT NULL COMMENT '排序号',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `VERSION` int DEFAULT NULL COMMENT '版本号,乐观锁',
  `CAPACITY` int NOT NULL DEFAULT '1' COMMENT '岗位容量，默认容量为1，即一人一岗',
  `DUTY` varchar(255) DEFAULT NULL COMMENT '职务',
  `DUTY_LEVEL` int NOT NULL DEFAULT '0' COMMENT '职务级别',
  `DUTY_LEVEL_NAME` varchar(255) DEFAULT NULL COMMENT '职级名称',
  `DUTY_TYPE` varchar(255) DEFAULT NULL COMMENT '职务类型',
  `EXCLUSIVE_IDS` varchar(500) DEFAULT NULL COMMENT '互斥的岗位Id列表，之间用逗号分割',
  `HEAD_COUNT` int NOT NULL DEFAULT '0' COMMENT '岗位当前人数，小于或等于岗位容量',
  `JOB_ID` varchar(38) NOT NULL COMMENT '职位id',
  `ORDERED_PATH` varchar(500) DEFAULT NULL COMMENT '排序序列号',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK_dkllqwv7eq7trb5394pg0tm6g` (`GUID_PATH`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_POSITION`
--

/*!40000 ALTER TABLE `Y9_ORG_POSITION` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_POSITION` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_POSITIONS_GROUPS`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_POSITIONS_GROUPS` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `GROUP_ID` varchar(38) NOT NULL COMMENT '用户组id',
  `GROUP_ORDER` int NOT NULL COMMENT '用户组排序号',
  `POSITION_ID` varchar(38) NOT NULL COMMENT '岗位ID',
  `POSITION_ORDER` int NOT NULL COMMENT '人员排序号',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UKtjxh3p9qk2s8we8mtrg2mxc9y` (`GROUP_ID`,`POSITION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位与岗位组关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_POSITIONS_GROUPS`
--

/*!40000 ALTER TABLE `Y9_ORG_POSITIONS_GROUPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_POSITIONS_GROUPS` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_POSITIONS_RESOURCES`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_POSITIONS_RESOURCES` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) NOT NULL COMMENT '应用id',
  `APP_NAME` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `AUTHORITY` int NOT NULL COMMENT '权限类型',
  `AUTHORIZATION_ID` varchar(38) DEFAULT NULL COMMENT '权限配置id',
  `INHERIT` int NOT NULL COMMENT '资源是否为继承上级节点的权限。冗余字段，纯显示用',
  `PARENT_RESOURCE_ID` varchar(38) DEFAULT NULL COMMENT '父资源id',
  `RESOURCE_CUSTOM_ID` varchar(38) DEFAULT NULL COMMENT '资源自定义id',
  `RESOURCE_DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `RESOURCE_ID` varchar(38) NOT NULL COMMENT '资源id',
  `RESOURCE_NAME` varchar(255) DEFAULT NULL COMMENT '资源名称',
  `RESOURCE_TAB_INDEX` int NOT NULL COMMENT '排序',
  `RESOURCE_TYPE` int NOT NULL COMMENT '资源类型：0=应用，1=菜单，2=操作',
  `RESOURCE_URL` varchar(255) DEFAULT NULL COMMENT '资源URL',
  `SYSTEM_CN_NAME` varchar(255) DEFAULT NULL COMMENT '系统中文名称',
  `SYSTEM_ID` varchar(38) NOT NULL COMMENT '系统id',
  `SYSTEM_NAME` varchar(255) DEFAULT NULL COMMENT '系统英文名称',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `POSITION_ID` varchar(38) NOT NULL COMMENT '身份(岗位)唯一标识',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK5cps1b1fq199989ni2hcrlwmd` (`POSITION_ID`,`RESOURCE_ID`,`AUTHORIZATION_ID`,`AUTHORITY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位与（资源、权限）关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_POSITIONS_RESOURCES`
--

/*!40000 ALTER TABLE `Y9_ORG_POSITIONS_RESOURCES` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_POSITIONS_RESOURCES` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_POSITIONS_ROLES`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_POSITIONS_ROLES` (
  `ID` int NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_ID` varchar(38) DEFAULT NULL COMMENT '应用id',
  `APP_NAME` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `DEPARTMENT_ID` varchar(38) NOT NULL COMMENT '部门id',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `ROLE_CUSTOM_ID` varchar(38) DEFAULT NULL COMMENT '角色自定义id',
  `ROLE_ID` varchar(38) NOT NULL COMMENT '角色id',
  `ROLE_NAME` varchar(255) DEFAULT NULL COMMENT '角色名称',
  `SYSTEM_CN_NAME` varchar(255) DEFAULT NULL COMMENT '系统中文名称',
  `SYSTEM_ID` varchar(38) DEFAULT NULL COMMENT '系统id',
  `SYSTEM_NAME` varchar(255) DEFAULT NULL COMMENT '系统英文名称',
  `TENANT_ID` varchar(38) DEFAULT NULL COMMENT '租户id',
  `POSITION_ID` varchar(38) NOT NULL COMMENT '身份(岗位)唯一标识',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UK8w7r8ookrjq6rcgiy98k56aba` (`POSITION_ID`,`ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='岗位与角色关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_POSITIONS_ROLES`
--

/*!40000 ALTER TABLE `Y9_ORG_POSITIONS_ROLES` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORG_POSITIONS_ROLES` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORG_ROLE`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORG_ROLE` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `APP_CN_NAME` varchar(255) NOT NULL COMMENT '应用中文名称，冗余字段，仅用于显示',
  `APP_ID` varchar(38) NOT NULL COMMENT '应用id',
  `CUSTOM_ID` varchar(255) DEFAULT NULL COMMENT 'customId',
  `DESCRIPTION` varchar(255) DEFAULT NULL COMMENT '描述',
  `DN` varchar(2000) DEFAULT NULL COMMENT '名称组成的父子关系列表，之间用逗号分隔',
  `DYNAMIC` int NOT NULL DEFAULT '0' COMMENT '动态角色',
  `GUID_PATH` varchar(1200) DEFAULT NULL COMMENT '由ID组成的父子关系列表，之间用逗号分隔',
  `NAME` varchar(255) NOT NULL COMMENT '角色名称',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点ID',
  `PROPERTIES` varchar(500) DEFAULT NULL COMMENT '扩展属性',
  `SYSTEM_CN_NAME` varchar(255) NOT NULL COMMENT '系统中文名称，冗余字段，仅用于显示',
  `SYSTEM_NAME` varchar(255) NOT NULL COMMENT '系统名称，冗余字段，仅用于显示',
  `TAB_INDEX` int NOT NULL COMMENT '序列号',
  `TENANT_CUSTOM` int NOT NULL DEFAULT '0' COMMENT '租户自定义',
  `TENANT_ID` varchar(255) DEFAULT NULL COMMENT 'tenantCustom=true时的租户id',
  `TYPE` varchar(255) NOT NULL DEFAULT 'role' COMMENT '类型：role、folder',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORG_ROLE`
--

/*!40000 ALTER TABLE `Y9_ORG_ROLE` DISABLE KEYS */;
INSERT INTO `Y9_ORG_ROLE` (`ID`, `CREATE_TIME`, `UPDATE_TIME`, `APP_CN_NAME`, `APP_ID`, `CUSTOM_ID`, `DESCRIPTION`, `DN`, `DYNAMIC`, `GUID_PATH`, `NAME`, `PARENT_ID`, `PROPERTIES`, `SYSTEM_CN_NAME`, `SYSTEM_NAME`, `TAB_INDEX`, `TENANT_CUSTOM`, `TENANT_ID`, `TYPE`) VALUES ('11111111-1111-1111-1111-111111111121','2023-08-23 18:13:38.455000','2023-08-23 18:13:38.455000','公共角色','11111111-1111-1111-1111-111111111121',NULL,NULL,'cn=公共角色列表',0,'11111111-1111-1111-1111-111111111121','公共角色列表',NULL,NULL,'公共角色顶节点','Y9OrgHierarchyManagement',0,0,NULL,'folder');
/*!40000 ALTER TABLE `Y9_ORG_ROLE` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORGBASE_DELETED`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORGBASE_DELETED` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `DN` varchar(2000) DEFAULT NULL COMMENT '名称组成的父子关系，之间以逗号分割',
  `JSON_CONTENT` longtext COMMENT '内容',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '操作者',
  `ORG_ID` varchar(38) DEFAULT NULL COMMENT '组织id',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID` varchar(38) DEFAULT NULL COMMENT '父节点的Id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='删除的组织表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORGBASE_DELETED`
--

/*!40000 ALTER TABLE `Y9_ORGBASE_DELETED` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORGBASE_DELETED` ENABLE KEYS */;

--
-- Table structure for table `Y9_ORGBASE_MOVED`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Y9_ORGBASE_MOVED` (
  `ID` varchar(38) NOT NULL COMMENT '主键',
  `CREATE_TIME` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `UPDATE_TIME` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `FINISHED` int NOT NULL DEFAULT '0' COMMENT '工作交接是否完成',
  `OPERATOR` varchar(30) DEFAULT NULL COMMENT '操作者',
  `ORG_ID` varchar(38) DEFAULT NULL COMMENT '组织id',
  `ORG_TYPE` varchar(255) NOT NULL COMMENT '组织类型',
  `PARENT_ID_FROM` varchar(38) DEFAULT NULL COMMENT '原来的父节点Id',
  `PARENT_ID_TO` varchar(38) DEFAULT NULL COMMENT '目标父节点Id',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='移动的组织表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Y9_ORGBASE_MOVED`
--

/*!40000 ALTER TABLE `Y9_ORGBASE_MOVED` DISABLE KEYS */;
/*!40000 ALTER TABLE `Y9_ORGBASE_MOVED` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-08-23 18:16:46
