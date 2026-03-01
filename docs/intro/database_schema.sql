-- ===================================
-- Columbia Base 数据库架构说明
-- ===================================
--
-- 本文件包含完整的数据库表结构和说明
-- 用于向同事介绍项目的数据库设计
--
-- 版本: 1.0
-- 更新日期: 2026-03-01
--

-- ===================================
-- 用户与认证相关表
-- ===================================

-- 用户表
-- 存储用户基本信息，支持用户名、手机号、微信OPENID多种登录方式
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '哈希密码（BCrypt加密）',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '手机号',
  `wechat_openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '微信唯一标识',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态: 0-待激活, 1-正常, 2-锁定',
  `role_level` int(11) NULL DEFAULT 1 COMMENT '角色等级: 1-游客, 3-正式用户, 5-付费用户, 7-内部管理用户, 10-系统管理员',
  `avatar_file_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像文件ID (关联 t_file.file_uuid)',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_username` (`username`) USING BTREE,
  INDEX `idx_phone` (`phone`) USING BTREE,
  INDEX `idx_wechat_openid` (`wechat_openid`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- 用户认证令牌表
-- 存储用户登录后的JWT令牌和会话信息
DROP TABLE IF EXISTS `t_user_auth`;
CREATE TABLE `t_user_auth` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '关联用户ID',
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'JWT令牌',
  `login_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '登录 IP，安全审计用',
  `expire_time` datetime(0) NULL DEFAULT NULL COMMENT '令牌过期时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE,
  INDEX `idx_token` (`token`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户认证令牌表' ROW_FORMAT = Dynamic;

-- 微信认证表
-- 用于微信扫码登录流程的状态管理
DROP TABLE IF EXISTS `t_wechat_auth`;
CREATE TABLE `t_wechat_auth` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花 ID',
  `scene` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景值 ID，6 位随机字符串',
  `openid` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信 openid',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '状态: pending-等待扫码，expire-已过期，success-扫码成功',
  `expired_at` datetime(0) NOT NULL COMMENT '过期时间，创建时间 +5 分钟',
  `create_time` datetime(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scene`(`scene`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '微信认证状态表' ROW_FORMAT = Dynamic;

-- ===================================
-- 产品与激活码相关表
-- ===================================

-- 产品表
-- 存储产品信息，产品可以包含多个课程
DROP TABLE IF EXISTS `t_product`;
CREATE TABLE `t_product` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '产品名，如：高中数学审阅 Agent',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '产品描述',
  `base_credits` int(11) NULL DEFAULT NULL COMMENT '激活该产品赠送的初始积分',
  `status` int(11) NULL DEFAULT 1 COMMENT '状态: 1-启用, 0-下架/隐藏',
  `cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '封面图片URL',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '产品表' ROW_FORMAT = Dynamic;

-- 激活码表
-- 存储产品兑换码信息
DROP TABLE IF EXISTS `t_activation_code`;
CREATE TABLE `t_activation_code` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '兑换码，建议8-12位随机字符',
  `product_id` bigint(20) NULL DEFAULT NULL COMMENT '关联产品ID',
  `status` int(11) NULL DEFAULT NULL COMMENT '状态: 0-未使用, 1-已使用, 2-已过期',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '使用该激活码的用户ID',
  `used_time` datetime(0) NULL DEFAULT NULL COMMENT '激活时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code`) USING BTREE,
  INDEX `idx_product_id` (`product_id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '激活码表' ROW_FORMAT = Dynamic;

-- ===================================
-- 课程与内容相关表
-- ===================================

-- 课程表
-- 存储课程基础信息
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
    `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '课程介绍',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, OFFLINE-下架',
    `product_id` BIGINT(20) NULL COMMENT '关联产品 ID',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_product_id` (`product_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程表' ROW_FORMAT = Dynamic;

-- 章节表
-- 课程下包含多个章节
DROP TABLE IF EXISTS `t_chapter`;
CREATE TABLE `t_chapter` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `course_id` BIGINT(20) NOT NULL COMMENT '关联课程 ID',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节名称',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序号',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_course_id` (`course_id`) USING BTREE,
    INDEX `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '章节表' ROW_FORMAT = Dynamic;

-- 课时表
-- 章节下包含多个课时，支持多种类型：视频、文档、播客、作业、互动练习
DROP TABLE IF EXISTS `t_lesson`;
CREATE TABLE `t_lesson` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `chapter_id` BIGINT(20) NOT NULL COMMENT '关联章节 ID',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课时标题',
    `item_type` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型: VIDEO-视频, DOCUMENT-文档, PODCAST-播客, ASSIGNMENT-作业, INTERACTIVE-互动练习',
    `is_required` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否必修: 0-否, 1-是',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序号',
    `content_payload` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容数据 JSON',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_chapter_id` (`chapter_id`) USING BTREE,
    INDEX `idx_item_type` (`item_type`) USING BTREE,
    INDEX `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '课时表' ROW_FORMAT = Dynamic;

-- 用户课程关联表
-- 记录用户购买/激活的课程及其权限状态
DROP TABLE IF EXISTS `t_user_course`;
CREATE TABLE `t_user_course` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '学生 ID',
    `course_id` BIGINT(20) NOT NULL COMMENT '关联课程 ID',
    `access_status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-生效中, EXPIRED-已过期, SUSPENDED-被封禁',
    `progress_percent` INT(11) NOT NULL DEFAULT 0 COMMENT '总体完成度百分比 (0-100)',
    `activated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '激活时间',
    `valid_until` DATETIME NULL COMMENT '有效期至（买断制为空）',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_course` (`user_id`, `course_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_course_id` (`course_id`) USING BTREE,
    INDEX `idx_access_status` (`access_status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户课程关联表' ROW_FORMAT = Dynamic;

-- 用户课时进度表
-- 记录用户对各课时的学习进度
DROP TABLE IF EXISTS `t_user_lesson_progress`;
CREATE TABLE `t_user_lesson_progress` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '学生 ID',
    `lesson_id` BIGINT(20) NOT NULL COMMENT '关联课时 ID',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LOCKED' COMMENT '状态: LOCKED-未解锁, UNLOCKED-可学习, IN_PROGRESS-学习中, COMPLETED-已完成',
    `progress_payload` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '进度数据 JSON，如视频观看百分比',
    `last_accessed_at` DATETIME NULL COMMENT '最后访问时间',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_lesson` (`user_id`, `lesson_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_lesson_id` (`lesson_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户课时进度表' ROW_FORMAT = Dynamic;

-- 用户作业表
-- 存储用户提交的作业和批改信息
DROP TABLE IF EXISTS `t_user_assignment`;
CREATE TABLE `t_user_assignment` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '学生 ID',
    `lesson_id` BIGINT(20) NOT NULL COMMENT '关联课时 ID',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待提交, SUBMITTED-已提交待批改, GRADED-已批改, REJECTED-被打回重做',
    `submission_payload` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '作业提交内容 JSON',
    `score` INT(11) COMMENT '分数',
    `feedback` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '教师评语/系统反馈',
    `submitted_at` DATETIME NULL COMMENT '提交时间',
    `graded_at` DATETIME NULL COMMENT '批改时间',
    `grader_id` BIGINT(20) COMMENT '批改人 ID',
    `create_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_lesson_assignment` (`user_id`, `lesson_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_lesson_id` (`lesson_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_grader_id` (`grader_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '用户作业表' ROW_FORMAT = Dynamic;

-- ===================================
-- 积分系统相关表
-- ===================================

-- 积分余额表
-- 记录用户的积分余额状态
DROP TABLE IF EXISTS `t_credit_balance`;
CREATE TABLE `t_credit_balance` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（主键）',
  `available_credits` int(11) NULL DEFAULT NULL COMMENT '可用积分',
  `frozen_credits` int(11) NULL DEFAULT NULL COMMENT '冻结积分，用于Agent任务运行中扣除',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户积分余额表' ROW_FORMAT = Dynamic;

-- 积分日志表
-- 记录所有积分变动流水
DROP TABLE IF EXISTS `t_credit_log`;
CREATE TABLE `t_credit_log` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '类型: RECHARGE-充值, ACTIVATE-激活奖励, CONSUME-消费, FREEZE-冻结, UNFREEZE-解冻',
  `amount` int(11) NULL DEFAULT NULL COMMENT '变动金额，正负值',
  `task_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联任务 ID，方便对账',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '变动说明',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id` (`user_id`) USING BTREE,
  INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '积分变动日志表' ROW_FORMAT = Dynamic;

-- ===================================
-- 文件管理相关表
-- ===================================

-- 文件管理表
-- 管理上传到OSS的文件信息
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file` (
    `id` BIGINT(20) NOT NULL COMMENT '主键，雪花 ID',
    `file_uuid` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务唯一标识',
    `file_md5` VARCHAR(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件 MD5',
    `user_id` BIGINT(20) NOT NULL COMMENT '上传用户 ID',
    `bucket_name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS 桶名',
    `oss_path` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'OSS 存储路径',
    `file_size` BIGINT(20) NOT NULL COMMENT '文件大小（Bytes）',
    `content_type` VARCHAR(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MIME 类型',
    `metadata` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '业务自定义元数据（JSON）',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-正常, 1-已删除',
    `create_time` DATETIME(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_file_uuid` (`file_uuid`) USING BTREE,
    UNIQUE INDEX `uk_user_file_md5` (`user_id`, `file_md5`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT = '文件管理表' ROW_FORMAT = Dynamic;

-- ===================================
-- 审计日志相关表
-- ===================================

-- 审计日志表
-- 记录管理员的所有敏感操作，用于安全审计
DROP TABLE IF EXISTS `t_audit_log`;
CREATE TABLE `t_audit_log` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花ID',
  `admin_id` bigint(20) NOT NULL COMMENT '操作者 ID（管理员 ID）',
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属模块: USER, PRODUCT, CREDIT, ACTIVATION',
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '具体动作',
  `target_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作对象 ID（user_id 或 product_id）',
  `before_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '操作前的原始数据',
  `after_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '操作后的新数据/请求 Body',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作备注',
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作员 IP 地址',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_admin_id`(`admin_id`) USING BTREE,
  INDEX `idx_module_action`(`module`, `action`) USING BTREE,
  INDEX `idx_create_time` (`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员操作审计日志表' ROW_FORMAT = Dynamic;

-- ===================================
-- 数据库设计说明
-- ===================================

/*
1. 主键策略：
   - 所有表使用雪花ID（bigint）作为主键
   - 保证分布式环境下的ID唯一性

2. 字符集：
   - 统一使用 utf8mb4 字符集
   - 支持存储emoji等特殊字符

3. 时间字段：
   - create_time: 记录创建时间，默认使用 CURRENT_TIMESTAMP
   - update_time: 记录更新时间，自动更新使用 ON UPDATE CURRENT_TIMESTAMP

4. 索引策略：
   - 外键字段建立索引
   - 查询频繁的字段建立索引
   - 唯一索引使用 uk_ 前缀
   - 普通索引使用 idx_ 前缀

5. 表关系说明：

   用户体系：
   - t_user (用户) 1:N t_user_auth (认证令牌)
   - t_user (用户) 1:1 t_credit_balance (积分余额)
   - t_user (用户) 1:N t_credit_log (积分日志)
   - t_user (用户) 1:N t_file (文件)

   产品体系：
   - t_product (产品) 1:N t_course (课程)
   - t_product (产品) 1:N t_activation_code (激活码)
   - t_course (课程) 1:N t_chapter (章节)
   - t_chapter (章节) 1:N t_lesson (课时)

   用户学习数据：
   - t_user (用户) N:M t_course (课程) → t_user_course
   - t_user (用户) N:M t_lesson (课时) → t_user_lesson_progress
   - t_user (用户) N:M t_lesson (课时) → t_user_assignment

   审计体系：
   - t_user (管理员) 1:N t_audit_log (审计日志)

6. 状态枚举说明：

   UserRole (用户角色等级):
   - 1: 游客
   - 3: 正式用户
   - 5: 付费用户
   - 7: 内部管理用户
   - 10: 系统管理员

   UserStatus (用户状态):
   - 0: 待激活
   - 1: 正常
   - 2: 锁定

   CourseStatus (课程状态):
   - DRAFT: 草稿
   - PUBLISHED: 已发布
   - OFFLINE: 下架

   LessonItemType (课时类型):
   - VIDEO: 视频
   - DOCUMENT: 文档
   - PODCAST: 播客
   - ASSIGNMENT: 作业
   - INTERACTIVE: 互动练习

   AccessStatus (课程访问状态):
   - ACTIVE: 生效中
   - EXPIRED: 已过期
   - SUSPENDED: 被封禁

   LessonStatus (课时学习状态):
   - LOCKED: 未解锁
   - UNLOCKED: 可学习
   - IN_PROGRESS: 学习中
   - COMPLETED: 已完成

   AssignmentStatus (作业状态):
   - PENDING: 待提交
   - SUBMITTED: 已提交待批改
   - GRADED: 已批改
   - REJECTED: 被打回重做

   CreditLogType (积分类型):
   - RECHARGE: 充值
   - ACTIVATE: 激活奖励
   - CONSUME: 消费
   - FREEZE: 冻结
   - UNFREEZE: 解冻

   ActivationCodeStatus (激活码状态):
   - 0: 未使用
   - 1: 已使用
   - 2: 已过期

   AuditModule (审计模块):
   - USER: 用户模块
   - PRODUCT: 产品模块
   - CREDIT: 积分模块
   - ACTIVATION: 激活模块
   - AUDIT: 审计模块

   AuditAction (审计动作):
   - BAN_USER: 封禁用户
   - SAVE_PRODUCT: 保存产品
   - MANUAL_ACTIVATE: 手动激活
   - BATCH_CREATE_CODE: 批量创建激活码
   - QUERY_USER_ACTIVATION: 查询用户激活状态
   - DEACTIVATE_PRODUCT: 停用产品
   - QUERY_ACTIVATION_CODES: 查询激活码列表
   - MANUAL_RECHARGE: 手动充值
   - QUERY_AUDIT_LOG: 查询审计日志
*/