-- ----------------------------
-- Table structure for t_wechat_auth
-- ----------------------------
DROP TABLE IF EXISTS `t_wechat_auth`;
CREATE TABLE `t_wechat_auth` (
  `id` bigint(20) NOT NULL COMMENT '主键，雪花 ID',
  `scene` varchar(10) NOT NULL COMMENT '场景值 ID，6 位随机字符串',
  `openid` varchar(255) DEFAULT NULL COMMENT '微信 openid',
  `status` varchar(20) NOT NULL COMMENT '状态：pending-等待扫码，expire-已过期，success-扫码成功',
  `expired_at` datetime NOT NULL COMMENT '过期时间，创建时间 +5 分钟',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_scene`(`scene`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信小程序扫码登录表';
