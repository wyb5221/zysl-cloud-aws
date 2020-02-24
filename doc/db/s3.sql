/*
创建数据库
*/
CREATE DATABASE 's3' CHARACTER SET 'utf8' COLLATE 'utf8_general_ci';



/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 50727
 Source Host           : localhost:3306
 Source Schema         : s3

 Target Server Type    : MySQL
 Target Server Version : 50727
 File Encoding         : 65001

 Date: 20/02/2020 21:33:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for s3_file
-- ----------------------------
DROP TABLE IF EXISTS `s3_file`;
CREATE TABLE `s3_file`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `service_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器编号',
  `file_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
  `content_md5` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件内容md5',
  `folder_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件夹名称',
  `down_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件下载url',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `max_amount` int(11) NULL DEFAULT NULL COMMENT '最大可下载次数',
  `down_amount` int(11) NULL DEFAULT 0 COMMENT '已下载次数',
  `validity_time` datetime(0) NULL DEFAULT NULL COMMENT '下载有效截至时间',
  `upload_time` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `source_file_id` bigint(20) NULL DEFAULT NULL COMMENT '源文件ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `index_content_md5`(`content_md5`) USING BTREE,
  INDEX `index_folder_name_file_name`(`folder_name`, `file_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 198 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s3_file
-- ----------------------------
INSERT INTO `s3_file` VALUES (193, '001', '1.txt', '554u0/vjYBTu5OZmidBe6w==', 'test-yy01', NULL, 8, NULL, NULL, NULL, NULL, '2020-02-19 16:34:40', NULL);
INSERT INTO `s3_file` VALUES (194, '001', '1_1582101280504.txt', '554u0/vjYBTu5OZmidBe6w==', 'test-yy01', NULL, 8, 3, NULL, '2020-02-19 17:34:41', NULL, '2020-02-19 16:34:41', 193);
INSERT INTO `s3_file` VALUES (195, '001', '1_1582167291664.txt', '554u0/vjYBTu5OZmidBe6w==', 'test-yy01', NULL, 8, 3, NULL, '2020-02-20 11:54:52', NULL, '2020-02-20 10:54:52', 193);
INSERT INTO `s3_file` VALUES (197, '001', '7e289b28-35c7-11ea-a2ac-005056b630a1.pdf', 'IFOGhARULZ5xvitPpF8MYg==', 'test-yy01', NULL, 67818, NULL, NULL, NULL, '2020-02-20 13:32:26', '2020-02-20 13:32:26', NULL);

-- ----------------------------
-- Table structure for s3_file_temp
-- ----------------------------
DROP TABLE IF EXISTS `s3_file_temp`;
CREATE TABLE `s3_file_temp`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `service_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器编号',
  `file_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件名称',
  `content_md5` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件内容md5',
  `folder_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件夹名称',
  `down_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件下载url',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小',
  `max_amount` int(11) NULL DEFAULT NULL COMMENT '最大可下载次数',
  `down_amount` int(11) NULL DEFAULT 0 COMMENT '已下载次数',
  `validity_time` datetime(0) NULL DEFAULT NULL COMMENT '下载有效截至时间',
  `upload_time` datetime(0) NULL DEFAULT NULL COMMENT '上传时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `source_file_id` bigint(20) NULL DEFAULT NULL COMMENT '源文件ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s3_folder
-- ----------------------------
DROP TABLE IF EXISTS `s3_folder`;
CREATE TABLE `s3_folder`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `service_no` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器编号',
  `folder_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '文件夹名称',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `unique_folder_name`(`folder_name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s3_folder
-- ----------------------------
INSERT INTO `s3_folder` VALUES (1, '001', 'test-yy01', '2020-02-12 19:07:34');
INSERT INTO `s3_folder` VALUES (3, '001', 'test-w02', '2020-02-12 19:39:06');
INSERT INTO `s3_folder` VALUES (6, '1', 'temp-001', '2020-02-14 15:17:15');
INSERT INTO `s3_folder` VALUES (7, '1', 'backups', '2020-02-14 15:17:19');
INSERT INTO `s3_folder` VALUES (8, '1', 'announce', '2020-02-14 15:17:21');
INSERT INTO `s3_folder` VALUES (9, '1', 'legalfolder', '2020-02-14 15:17:25');
INSERT INTO `s3_folder` VALUES (10, '1', 'periodreport', '2020-02-14 15:17:27');
INSERT INTO `s3_folder` VALUES (11, '1', 'disclosure', '2020-02-14 15:17:31');
INSERT INTO `s3_folder` VALUES (12, '2', 'tem-p002', '2020-02-14 15:17:43');

-- ----------------------------
-- Table structure for s3_service
-- ----------------------------
DROP TABLE IF EXISTS `s3_service`;
CREATE TABLE `s3_service`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `service_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器编号',
  `service_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器别名',
  `accessKey` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 's3服务器登陆accessKey',
  `secretKey` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 's3服务器登陆secretKey',
  `endpoint` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 's3服务器登陆地址',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of s3_service
-- ----------------------------
INSERT INTO `s3_service` VALUES (1, '001', 'aws0', 'AKIASLXIN4C6CZQ2UGOE', 'WQqKO9mX0xkWqHgAFXy3hcgg6AfhsUVJSmB/auj1', 'http://s3.amazonaws.com');
INSERT INTO `s3_service` VALUES (2, '1', 'aws1', 'idDyk1eqeu4jbbL1AW5V', 'ISRq73JiZPVA5DFGFnFBsfBSS1in2du4vwuVgLxv', 'https://172.21.235.124:443');
INSERT INTO `s3_service` VALUES (3, '2', 'aws2', 'SwkkNP9BKeJkkfJ4e2fK', '3xcACDu0basofZTmjMI3xwV4zSQs0Qq6t1A0EXuY', 'https://172.21.235.124:443');

SET FOREIGN_KEY_CHECKS = 1;
