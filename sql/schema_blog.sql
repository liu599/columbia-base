-- ----------------------------
-- Blog Schema (MySQL 5.5 Compatible)
-- ----------------------------

-- ----------------------------
-- Table structure for t_blog_category
-- ----------------------------
DROP TABLE IF EXISTS `t_blog_category`;
CREATE TABLE `t_blog_category` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `name` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
    `slug` VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类别名 (URL友好)',
    `description` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '分类描述',
    `sort_order` INT(11) NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-启用, DISABLED-禁用',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_slug` (`slug`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_sort_order` (`sort_order`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_blog_tag`;
CREATE TABLE `t_blog_tag` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `name` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称',
    `slug` VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签别名 (URL友好)',
    `description` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '标签描述',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE-启用, DISABLED-禁用',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_slug` (`slug`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_blog_tag
-- ----------------------------
DROP TABLE IF EXISTS `t_blog_post`;
CREATE TABLE `t_blog_post` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `title` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章标题',
    `slug` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章别名 (URL友好)',
    `excerpt` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文章摘要',
    `content` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文章内容 (Markdown或HTML)',
    `content_html` MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '渲染后的HTML内容',
    `excerpt_text` TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '纯文本摘要',
    `content_text` MEDIUMTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '纯文本内容',
    `cover_image` VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '封面图片URL',
    `author_id` BIGINT(20) NOT NULL COMMENT '作者ID',
    `category_id` BIGINT(20) COMMENT '分类ID (可为空)',
    `status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT-草稿, PUBLISHED-已发布, ARCHIVED-已归档, DELETED-已删除',
    `view_count` INT(11) NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `like_count` INT(11) NOT NULL DEFAULT 0 COMMENT '点赞次数',
    `comment_count` INT(11) NOT NULL DEFAULT 0 COMMENT '评论次数',
    `is_featured` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否精选 (0: 否, 1: 是)',
    `published_at` DATETIME NULL COMMENT '发布时间',
    `create_time` DATETIME NULL COMMENT '创建时间',
    `update_time` DATETIME NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_slug` (`slug`) USING BTREE,
    INDEX `idx_category_id` (`category_id`) USING BTREE,
    INDEX `idx_author_id` (`author_id`) USING BTREE,
    INDEX `idx_status` (`status`) USING BTREE,
    INDEX `idx_published_at` (`published_at`) USING BTREE,
    INDEX `idx_is_featured` (`is_featured`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_blog_post_tag (Post-Tag Many-to-Many relation)
-- ----------------------------
DROP TABLE IF EXISTS `t_blog_post_tag`;
CREATE TABLE `t_blog_post_tag` (
    `id` BIGINT(20) NOT NULL COMMENT '主键 (雪花 ID)',
    `post_id` BIGINT(20) NOT NULL COMMENT '文章ID',
    `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
    `create_time` DATETIME NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_post_tag` (`post_id`, `tag_id`) USING BTREE,
    INDEX `idx_post_id` (`post_id`) USING BTREE,
    INDEX `idx_tag_id` (`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
