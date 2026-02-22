-- 为 t_course 表添加 product_id 字段
-- 用于关联产品（Product）和课程（Course）的一对多关系

ALTER TABLE `t_course`
ADD COLUMN `product_id` BIGINT(20) NULL COMMENT '关联产品 ID' AFTER `status`,
ADD INDEX `idx_product_id` (`product_id`) USING BTREE;
