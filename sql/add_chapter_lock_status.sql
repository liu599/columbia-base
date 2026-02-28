-- 添加章节锁定状态字段
ALTER TABLE `t_chapter` ADD COLUMN `lock_status` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'UNLOCK' COMMENT '锁定状态: LOCK-锁定, UNLOCK-解锁' AFTER `sort_order`;

-- 更新现有数据，全部设置为解锁状态
UPDATE `t_chapter` SET `lock_status` = 'UNLOCK' WHERE `lock_status` IS NULL;
