-- =============================================
-- 餐厅订餐系统 - 数据库初始化脚本
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `rrs` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- 使用数据库
USE `rrs`;

-- =============================================
-- 1. 用户表
-- =============================================
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT '用户编号',
  `account` varchar(50) DEFAULT NULL COMMENT '用户账号',
  `username` varchar(50) DEFAULT NULL COMMENT '用户昵称',
  `password` varchar(100) DEFAULT NULL COMMENT '用户密码',
  `avatar` text COMMENT '用户头像',
  `email` varchar(50) DEFAULT NULL COMMENT '用户邮箱',
  `role` int(11) DEFAULT NULL COMMENT '用户角色',
  `gender` tinyint(1) DEFAULT NULL COMMENT '性别：1-女；2-男',
  `birthday` date DEFAULT NULL COMMENT '生日',
  `phone` varchar(255) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(1) DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` datetime DEFAULT NULL COMMENT '用户注册时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='用户信息表';

-- =============================================
-- 2. 菜品种类表
-- =============================================
CREATE TABLE IF NOT EXISTS `dishes_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `icon_url` varchar(255) DEFAULT NULL COMMENT '图标URL路径',
  `name` varchar(30) DEFAULT NULL COMMENT '菜品种类名',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜品种类信息表';

-- =============================================
-- 3. 菜品信息表
-- =============================================
CREATE TABLE IF NOT EXISTS `dishes` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type_id` int(11) DEFAULT NULL COMMENT '菜品种类ID',
  `name` varchar(50) DEFAULT NULL COMMENT '菜品名',
  `detail` text COMMENT '菜品介绍',
  `cover_url` varchar(255) DEFAULT NULL COMMENT '菜品封面URL',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0下架，1上架',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_id` (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜品信息表';

-- =============================================
-- 4. 菜品套餐表
-- =============================================
CREATE TABLE IF NOT EXISTS `dishes_package` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dishes_id` int(11) DEFAULT NULL COMMENT '菜品ID',
  `name` varchar(30) DEFAULT NULL COMMENT '套餐名',
  `specs` varchar(30) DEFAULT NULL COMMENT '规格',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_dishes_id` (`dishes_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜品套餐信息表';

-- =============================================
-- 5. 收藏表
-- =============================================
CREATE TABLE IF NOT EXISTS `collection` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `dishes_id` int(11) DEFAULT NULL COMMENT '菜品ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dishes` (`user_id`,`dishes_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜品收藏表';

-- =============================================
-- 6. 餐桌表
-- =============================================
CREATE TABLE IF NOT EXISTS `dishes_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `number` varchar(20) DEFAULT NULL COMMENT '餐桌序号',
  `person_number` int(2) DEFAULT NULL COMMENT '就餐人数',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0不可用，1可用',
  `occupied` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否就餐中：0否，1是',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_number` (`number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='餐桌信息表';

INSERT INTO `dishes_table` (`number`, `person_number`, `status`, `create_time`)
SELECT 'A01', 4, 1, NOW()
WHERE NOT EXISTS (SELECT 1 FROM `dishes_table` WHERE `number` = 'A01');

-- =============================================
-- 7. 购物车表
-- =============================================
CREATE TABLE IF NOT EXISTS `shipping_car` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `dishes_package_id` int(11) DEFAULT NULL COMMENT '菜品套餐ID',
  `plus_number` int(11) DEFAULT NULL COMMENT '加购数量',
  `is_seleced` tinyint(1) DEFAULT NULL COMMENT '是否选中',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_package` (`user_id`,`dishes_package_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='购物车信息表';

-- =============================================
-- 8. 订单表
-- =============================================
CREATE TABLE IF NOT EXISTS `orders` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `code` varchar(100) DEFAULT NULL COMMENT '订单号',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `dishes_table_id` int(11) DEFAULT NULL COMMENT '餐桌ID',
  `status` tinyint(1) DEFAULT NULL COMMENT '订单状态',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '订单总价',
  `serve_food_time` datetime DEFAULT NULL COMMENT '出餐时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单信息表';

-- =============================================
-- 9. 订单项表
-- =============================================
CREATE TABLE IF NOT EXISTS `orders_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `orders_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `dishes_package_id` int(11) DEFAULT NULL COMMENT '菜品套餐ID',
  `snap_price` decimal(10,2) DEFAULT NULL COMMENT '快照价格',
  `snap_name` varchar(50) DEFAULT NULL COMMENT '快照名称',
  `snap_cover` varchar(255) DEFAULT NULL COMMENT '快照封面',
  `buy_number` int(11) DEFAULT NULL COMMENT '购买数量',
  `total_price` decimal(10,2) DEFAULT NULL COMMENT '小计价格',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_orders_id` (`orders_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单项';

-- =============================================
-- 10. 订单退款申请表
-- =============================================
CREATE TABLE IF NOT EXISTS `orders_refund_reply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `orders_id` int(11) DEFAULT NULL COMMENT '订单ID',
  `status` tinyint(1) DEFAULT NULL COMMENT '退款状态',
  `refund_cause` varchar(255) DEFAULT NULL COMMENT '退款缘由',
  `reject_refund_cause` varchar(255) DEFAULT NULL COMMENT '拒绝退款原因',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_orders_id` (`orders_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='订单退款申请表';

-- =============================================
-- 11. 钱包表
-- =============================================
CREATE TABLE IF NOT EXISTS `wallet` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `surplus` decimal(10,2) DEFAULT NULL COMMENT '余额',
  `status` tinyint(1) DEFAULT NULL COMMENT '状态：0冻结，1正常',
  `type` tinyint(1) DEFAULT NULL COMMENT '类型：1私人，2商家',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_type` (`user_id`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='钱包信息表';

-- =============================================
-- 12. 钱包明细表
-- =============================================
CREATE TABLE IF NOT EXISTS `wallet_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `wallet_id` int(11) DEFAULT NULL COMMENT '钱包ID',
  `detail` varchar(255) DEFAULT NULL COMMENT '描述',
  `surplus_money` decimal(10,2) DEFAULT NULL COMMENT '金额项',
  `create_time` datetime DEFAULT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_wallet_id` (`wallet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='金额明细表';

-- =============================================
-- 13. 菜品评价表
-- =============================================
CREATE TABLE IF NOT EXISTS `dishes_evaluations` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dishes_id` int(11) DEFAULT NULL COMMENT '菜品ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `content` text COMMENT '评价内容',
  `rating_value` int(11) DEFAULT NULL COMMENT '评分',
  `reply_content` text COMMENT '回复内容',
  `reply_status` tinyint(1) DEFAULT NULL COMMENT '回复状态',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_dishes_id` (`dishes_id`),
  KEY `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_user_dishes` (`user_id`,`dishes_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='菜品评价表';

-- =============================================
-- 14. 评价图片表
-- =============================================
CREATE TABLE IF NOT EXISTS `images` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dishes_evaluations_id` int(11) DEFAULT NULL COMMENT '评价ID',
  `picture_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `number` int(11) DEFAULT NULL COMMENT '序号',
  PRIMARY KEY (`id`),
  KEY `idx_evaluations_id` (`dishes_evaluations_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='评价图片表';

-- =============================================
-- 15. 消息通知表
-- =============================================
CREATE TABLE IF NOT EXISTS `messages` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` int(11) DEFAULT NULL COMMENT '用户ID',
  `content` text COMMENT '消息内容',
  `type` tinyint(1) DEFAULT NULL COMMENT '消息类型',
  `read_status` tinyint(1) DEFAULT NULL COMMENT '阅读状态：0未读，1已读',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='消息通知表';

-- =============================================
-- 插入测试数据
-- =============================================

-- 插入管理员账号（密码：123456，BCrypt加密）
INSERT INTO `user` (`account`, `username`, `password`, `role`, `create_time`) VALUES
('admin', '管理员', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 2, NOW());

-- 插入测试用户账号（密码：123456，BCrypt加密）
INSERT INTO `user` (`account`, `username`, `password`, `role`, `create_time`) VALUES
('zhouzhiruo', '周芷若', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, NOW());

-- 插入商家钱包
INSERT INTO `wallet` (`user_id`, `surplus`, `status`, `type`, `create_time`) VALUES
(1, 0.00, 1, 2, NOW());

-- 插入用户钱包
INSERT INTO `wallet` (`user_id`, `surplus`, `status`, `type`, `create_time`) VALUES
(2, 500.00, 1, 1, NOW());
