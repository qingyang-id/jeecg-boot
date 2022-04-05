
-- ----------------------------
-- Table structure for sys_quartz_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_quartz_job`;
CREATE TABLE `sys_quartz_job`  (
                                   `id` varchar(32) NOT NULL,
                                   `create_by` varchar(32) NULL DEFAULT NULL COMMENT '创建人',
                                   `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
                                   `del_flag` int(1) NULL DEFAULT NULL COMMENT '删除状态',
                                   `update_by` varchar(32) NULL DEFAULT NULL COMMENT '修改人',
                                   `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
                                   `job_class_name` varchar(255) NULL DEFAULT NULL COMMENT '任务类名',
                                   `cron_expression` varchar(255) NULL DEFAULT NULL COMMENT 'cron表达式',
                                   `parameter` varchar(255) NULL DEFAULT NULL COMMENT '参数',
                                   `description` varchar(255) NULL DEFAULT NULL COMMENT '描述',
                                   `status` int(1) NULL DEFAULT NULL COMMENT '状态 0正常 -1停止',
                                   PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Records of sys_quartz_job
-- ----------------------------
INSERT INTO `sys_quartz_job` VALUES ('5b3d2c087ad41aa755fc4f89697b01e7', 'admin', '2019-04-11 19:04:21', 0, 'admin', '2020-05-02 15:48:48', 'org.jeecg.modules.message.job.SendMsgJob', '0/50 * * * * ? *', NULL, NULL, -1);
INSERT INTO `sys_quartz_job` VALUES ('a253cdfc811d69fa0efc70d052bc8128', 'admin', '2019-03-30 12:44:48', 0, 'admin', '2020-05-02 15:48:49', 'org.jeecg.modules.quartz.job.SampleJob', '0/1 * * * * ?', NULL, NULL, -1);
INSERT INTO `sys_quartz_job` VALUES ('df26ecacf0f75d219d746750fe84bbee', NULL, NULL, 0, 'admin', '2020-05-02 15:40:35', 'org.jeecg.modules.quartz.job.SampleParamJob', '0/1 * * * * ?', 'scott', '带参测试 后台将每隔1秒执行输出日志', -1);

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
                                     `SCHED_NAME` varchar(120) NOT NULL,
                                     `JOB_NAME` varchar(200) NOT NULL,
                                     `JOB_GROUP` varchar(200) NOT NULL,
                                     `DESCRIPTION` varchar(250) NULL DEFAULT NULL,
                                     `JOB_CLASS_NAME` varchar(250) NOT NULL,
                                     `IS_DURABLE` varchar(1) NOT NULL,
                                     `IS_NONCONCURRENT` varchar(1) NOT NULL,
                                     `IS_UPDATE_DATA` varchar(1) NOT NULL,
                                     `REQUESTS_RECOVERY` varchar(1) NOT NULL,
                                     `JOB_DATA` blob NULL,
                                     PRIMARY KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
                                  `SCHED_NAME` varchar(120) NOT NULL,
                                  `TRIGGER_NAME` varchar(200) NOT NULL,
                                  `TRIGGER_GROUP` varchar(200) NOT NULL,
                                  `JOB_NAME` varchar(200) NOT NULL,
                                  `JOB_GROUP` varchar(200) NOT NULL,
                                  `DESCRIPTION` varchar(250) NULL DEFAULT NULL,
                                  `NEXT_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
                                  `PREV_FIRE_TIME` bigint(13) NULL DEFAULT NULL,
                                  `PRIORITY` int(11) NULL DEFAULT NULL,
                                  `TRIGGER_STATE` varchar(16) NOT NULL,
                                  `TRIGGER_TYPE` varchar(8) NOT NULL,
                                  `START_TIME` bigint(13) NOT NULL,
                                  `END_TIME` bigint(13) NULL DEFAULT NULL,
                                  `CALENDAR_NAME` varchar(200) NULL DEFAULT NULL,
                                  `MISFIRE_INSTR` smallint(2) NULL DEFAULT NULL,
                                  `JOB_DATA` blob NULL,
                                  PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                  INDEX `SCHED_NAME`(`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) USING BTREE,
                                  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB COMMENT = 'InnoDB free: 504832 kB; (`SCHED_NAME` `JOB_NAME` `JOB_GROUP`';


-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
                                       `SCHED_NAME` varchar(120) NOT NULL,
                                       `TRIGGER_NAME` varchar(200) NOT NULL,
                                       `TRIGGER_GROUP` varchar(200) NOT NULL,
                                       `BLOB_DATA` blob NULL,
                                       PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                       CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB COMMENT = 'InnoDB free: 504832 kB; (`SCHED_NAME` `TRIGGER_NAME` `TRIGGE';

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
                                   `SCHED_NAME` varchar(120) NOT NULL,
                                   `CALENDAR_NAME` varchar(200) NOT NULL,
                                   `CALENDAR` blob NOT NULL,
                                   PRIMARY KEY (`SCHED_NAME`, `CALENDAR_NAME`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
                                       `SCHED_NAME` varchar(120) NOT NULL,
                                       `TRIGGER_NAME` varchar(200) NOT NULL,
                                       `TRIGGER_GROUP` varchar(200) NOT NULL,
                                       `CRON_EXPRESSION` varchar(200) NOT NULL,
                                       `TIME_ZONE_ID` varchar(80) NULL DEFAULT NULL,
                                       PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                       CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB COMMENT = 'InnoDB free: 504832 kB; (`SCHED_NAME` `TRIGGER_NAME` `TRIGGE';

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
                                        `SCHED_NAME` varchar(120) NOT NULL,
                                        `ENTRY_ID` varchar(95) NOT NULL,
                                        `TRIGGER_NAME` varchar(200) NOT NULL,
                                        `TRIGGER_GROUP` varchar(200) NOT NULL,
                                        `INSTANCE_NAME` varchar(200) NOT NULL,
                                        `FIRED_TIME` bigint(13) NOT NULL,
                                        `SCHED_TIME` bigint(13) NOT NULL,
                                        `PRIORITY` int(11) NOT NULL,
                                        `STATE` varchar(16) NOT NULL,
                                        `JOB_NAME` varchar(200) NULL DEFAULT NULL,
                                        `JOB_GROUP` varchar(200) NULL DEFAULT NULL,
                                        `IS_NONCONCURRENT` varchar(1) NULL DEFAULT NULL,
                                        `REQUESTS_RECOVERY` varchar(1) NULL DEFAULT NULL,
                                        PRIMARY KEY (`SCHED_NAME`, `ENTRY_ID`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
                               `SCHED_NAME` varchar(120) NOT NULL,
                               `LOCK_NAME` varchar(40) NOT NULL,
                               PRIMARY KEY (`SCHED_NAME`, `LOCK_NAME`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------
INSERT INTO `qrtz_locks` VALUES ('MyScheduler', 'STATE_ACCESS');
INSERT INTO `qrtz_locks` VALUES ('MyScheduler', 'TRIGGER_ACCESS');
INSERT INTO `qrtz_locks` VALUES ('quartzScheduler', 'TRIGGER_ACCESS');

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
                                             `SCHED_NAME` varchar(120) NOT NULL,
                                             `TRIGGER_GROUP` varchar(200) NOT NULL,
                                             PRIMARY KEY (`SCHED_NAME`, `TRIGGER_GROUP`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
                                         `SCHED_NAME` varchar(120) NOT NULL,
                                         `INSTANCE_NAME` varchar(200) NOT NULL,
                                         `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
                                         `CHECKIN_INTERVAL` bigint(13) NOT NULL,
                                         PRIMARY KEY (`SCHED_NAME`, `INSTANCE_NAME`) USING BTREE
) ENGINE = InnoDB;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------
INSERT INTO `qrtz_scheduler_state` VALUES ('MyScheduler', 'DESKTOP-32NS8B11635249082475', 1635254473243, 10000);

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
                                         `SCHED_NAME` varchar(120) NOT NULL,
                                         `TRIGGER_NAME` varchar(200) NOT NULL,
                                         `TRIGGER_GROUP` varchar(200) NOT NULL,
                                         `REPEAT_COUNT` bigint(7) NOT NULL,
                                         `REPEAT_INTERVAL` bigint(12) NOT NULL,
                                         `TIMES_TRIGGERED` bigint(10) NOT NULL,
                                         PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                         CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB COMMENT = 'InnoDB free: 504832 kB; (`SCHED_NAME` `TRIGGER_NAME` `TRIGGE';

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
                                          `SCHED_NAME` varchar(120) NOT NULL,
                                          `TRIGGER_NAME` varchar(200) NOT NULL,
                                          `TRIGGER_GROUP` varchar(200) NOT NULL,
                                          `STR_PROP_1` varchar(512) NULL DEFAULT NULL,
                                          `STR_PROP_2` varchar(512) NULL DEFAULT NULL,
                                          `STR_PROP_3` varchar(512) NULL DEFAULT NULL,
                                          `INT_PROP_1` int(11) NULL DEFAULT NULL,
                                          `INT_PROP_2` int(11) NULL DEFAULT NULL,
                                          `LONG_PROP_1` bigint(20) NULL DEFAULT NULL,
                                          `LONG_PROP_2` bigint(20) NULL DEFAULT NULL,
                                          `DEC_PROP_1` decimal(13, 4) NULL DEFAULT NULL,
                                          `DEC_PROP_2` decimal(13, 4) NULL DEFAULT NULL,
                                          `BOOL_PROP_1` varchar(1) NULL DEFAULT NULL,
                                          `BOOL_PROP_2` varchar(1) NULL DEFAULT NULL,
                                          PRIMARY KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) USING BTREE,
                                          CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB COMMENT = 'InnoDB free: 504832 kB; (`SCHED_NAME` `TRIGGER_NAME` `TRIGGE';
