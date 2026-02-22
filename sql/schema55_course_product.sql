-- ----------------------------
-- Course Product Schema (MySQL 5.5 Compatible)
-- ----------------------------

-- ----------------------------
-- Table structure for t_course
-- ----------------------------
DROP TABLE IF EXISTS `t_course`;
CREATE TABLE `t_course` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
    `description` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '课程介绍',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, OFFLINE-下架',
    `create_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_chapter
-- ----------------------------
DROP TABLE IF EXISTS `t_chapter`;
CREATE TABLE `t_chapter` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `course_id` BIGINT(20) NOT NULL COMMENT '关联课程 ID',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节名称',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序号',
    `create_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_course_id` (`course_id`) USING BTREE,
    INDEX `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_lesson
-- ----------------------------
DROP TABLE IF EXISTS `t_lesson`;
CREATE TABLE `t_lesson` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `chapter_id` BIGINT(20) NOT NULL COMMENT '关联章节 ID',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课时标题',
    `item_type` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '类型: VIDEO, DOCUMENT, PODCAST, ASSIGNMENT, INTERACTIVE',
    `is_required` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否必修 (0: 否, 1: 是)',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序号',
    `content_payload` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容数据 JSON',
    `create_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_chapter_id` (`chapter_id`) USING BTREE,
    INDEX `idx_item_type` (`item_type`) USING BTREE,
    INDEX `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user_course
-- ----------------------------
DROP TABLE IF EXISTS `t_user_course`;
CREATE TABLE `t_user_course` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `user_id` BIGINT(20) NOT NULL COMMENT '学生 ID',
    `course_id` BIGINT(20) NOT NULL COMMENT '关联课程 ID',
    `access_status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-生效中, EXPIRED-已过期, SUSPENDED-被封禁',
    `progress_percent` INT(11) NOT NULL DEFAULT 0 COMMENT '总体完成度百分比 (0-100)',
    `activated_at` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '激活时间',
    `valid_until` DATETIME(0) NULL COMMENT '有效期至 (买断制为空)',
    `create_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_course` (`user_id`, `course_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_course_id` (`course_id`) USING BTREE,
    INDEX `idx_access_status` (`access_status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user_lesson_progress
-- ----------------------------
DROP TABLE IF EXISTS `t_user_lesson_progress`;
CREATE TABLE `t_user_lesson_progress` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `user_id` BIGINT(20) NOT NULL COMMENT '学生 ID',
    `lesson_id` BIGINT(20) NOT NULL COMMENT '关联课时 ID',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'LOCKED' COMMENT '状态: LOCKED-未解锁, UNLOCKED-可学习, IN_PROGRESS-学习中, COMPLETED-已完成',
    `progress_payload` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '进度数据 JSON',
    `last_accessed_at` DATETIME(0) NULL COMMENT '最后访问时间',
    `create_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_lesson` (`user_id`, `lesson_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_lesson_id` (`lesson_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_user_assignment
-- ----------------------------
DROP TABLE IF EXISTS `t_user_assignment`;
CREATE TABLE `t_user_assignment` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `user_id` BIGINT(20) NOT NULL COMMENT '学生 ID',
    `lesson_id` BIGINT(20) NOT NULL COMMENT '关联课时 ID',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING-待提交, SUBMITTED-已提交待批改, GRADED-已批改, REJECTED-被打回重做',
    `submission_payload` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '作业提交内容 JSON',
    `score` INT(11) COMMENT '分数',
    `feedback` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '教师评语/系统反馈',
    `submitted_at` DATETIME(0) NULL COMMENT '提交时间',
    `graded_at` DATETIME(0) NULL COMMENT '批改时间',
    `grader_id` BIGINT(20) COMMENT '批改人 ID',
    `create_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_user_lesson_assignment` (`user_id`, `lesson_id`) USING BTREE,
    INDEX `idx_user_id` (`user_id`) USING BTREE,
    INDEX `idx_lesson_id` (`lesson_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_grader_id` (`grader_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
