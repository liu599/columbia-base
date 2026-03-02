-- ----------------------------
-- Table structure for t_kb (知识库表)
-- ----------------------------
DROP TABLE IF EXISTS `t_kb`;
CREATE TABLE `t_kb` (
  `id` bigint(20) NOT NULL COMMENT '主键 (雪花ID)',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '知识库描述',
  `create_time` datetime(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_kb_file (知识库-文件关联表)
-- ----------------------------
DROP TABLE IF EXISTS `t_kb_file`;
CREATE TABLE `t_kb_file` (
  `id` bigint(20) NOT NULL COMMENT '主键 (雪花ID)',
  `kb_id` bigint(20) NOT NULL COMMENT '知识库ID',
  `file_id` bigint(20) NOT NULL COMMENT '文件ID (关联t_file.id)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'uploaded' COMMENT '状态: parsing-解析中, success-成功, error-失败, uploaded-已上传',
  `parser_config` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '解析器配置 (JSON格式)',
  `create_time` datetime(0) DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime(0) DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_kb_file`(`kb_id`, `file_id`) USING BTREE,
  INDEX `idx_kb_id`(`kb_id`) USING BTREE,
  INDEX `idx_file_id`(`file_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
