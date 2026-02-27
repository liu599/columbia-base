-- ==========================================
-- 添加封面图列到 t_product 和 t_course 表
-- ==========================================

-- 为 t_product 表添加 cover 列
-- cover 字段: 封面图文件 ID，对应 t_file 表的主键 id，可以为空
ALTER TABLE `t_product` ADD COLUMN `cover` BIGINT(20) NULL COMMENT '封面图 file_id (对应 t_file.id)' AFTER `status`;

-- 为 t_course 表添加 cover 列
-- cover 字段: 封面图文件 ID，对应 t_file 表的主键 id，可以为空
ALTER TABLE `t_course` ADD COLUMN `cover` BIGINT(20) NULL COMMENT '封面图 file_id (对应 t_file.id)' AFTER `product_id`;

-- ==========================================
-- 可选：添加索引（如果需要根据 cover 字段查询）
-- ==========================================

-- 如果需要频繁根据 cover 字段查询，可以添加索引
-- (注意：如果 cover 字段大部分为 NULL 或查询不频繁，不建议添加索引)
-- ALTER TABLE `t_product` ADD INDEX `idx_cover` (`cover`) USING BTREE;
-- ALTER TABLE `t_course` ADD INDEX `idx_cover` (`cover`) USING BTREE;

-- ==========================================
-- 数据迁移说明
-- ==========================================
--
-- 1. 执行此 SQL 脚本后，所有现有的产品和课程记录的 cover 字段将默认为 NULL
-- 2. 后续可以通过 UPDATE 语句为已有记录设置封面图文件 ID
--    例如：
--    UPDATE `t_product` SET `cover` = 123 WHERE `id` = 1;
-- 3. 应用程序代码已更新：
--    - Product 和 Course 实体类已添加 cover 字段
--    - PublicProductVO 和 ProductCourseVO 已添加 coverUrl 字段
--    - ProductServiceImpl 已更新，会自动将 cover file_id 转换为临时签名 URL
--
-- ==========================================
-- 回滚脚本（如需回滚）
-- ==========================================
--
-- ALTER TABLE `t_product` DROP COLUMN `cover`;
-- ALTER TABLE `t_course` DROP COLUMN `cover`;
--
-- ==========================================
