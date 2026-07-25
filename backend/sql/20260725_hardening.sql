USE `rrs`;

-- 执行前先确认下列查询均无结果；脚本不会自动删除已有业务数据。
SELECT user_id, type, COUNT(*) AS duplicate_count FROM wallet GROUP BY user_id, type HAVING COUNT(*) > 1;
SELECT user_id, dishes_id, COUNT(*) AS duplicate_count FROM collection GROUP BY user_id, dishes_id HAVING COUNT(*) > 1;
SELECT user_id, dishes_package_id, COUNT(*) AS duplicate_count FROM shipping_car GROUP BY user_id, dishes_package_id HAVING COUNT(*) > 1;
SELECT number, COUNT(*) AS duplicate_count FROM dishes_table GROUP BY number HAVING COUNT(*) > 1;
SELECT code, COUNT(*) AS duplicate_count FROM orders GROUP BY code HAVING COUNT(*) > 1;
SELECT orders_id, COUNT(*) AS duplicate_count FROM orders_refund_reply GROUP BY orders_id HAVING COUNT(*) > 1;
SELECT user_id, dishes_id, COUNT(*) AS duplicate_count FROM dishes_evaluations GROUP BY user_id, dishes_id HAVING COUNT(*) > 1;

SET @ddl = IF(
    (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'status') = 0,
    'ALTER TABLE `user` ADD COLUMN `status` tinyint(1) DEFAULT 1 COMMENT ''状态：0禁用，1启用'' AFTER `phone`',
    'SELECT 1'
);
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders' AND INDEX_NAME = 'uk_code') = 0,
    'ALTER TABLE `orders` ADD UNIQUE KEY `uk_code` (`code`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'wallet' AND INDEX_NAME = 'uk_user_type') = 0,
    'ALTER TABLE `wallet` ADD UNIQUE KEY `uk_user_type` (`user_id`, `type`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'collection' AND INDEX_NAME = 'uk_user_dishes') = 0,
    'ALTER TABLE `collection` ADD UNIQUE KEY `uk_user_dishes` (`user_id`, `dishes_id`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'shipping_car' AND INDEX_NAME = 'uk_user_package') = 0,
    'ALTER TABLE `shipping_car` ADD UNIQUE KEY `uk_user_package` (`user_id`, `dishes_package_id`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'dishes_table' AND INDEX_NAME = 'uk_number') = 0,
    'ALTER TABLE `dishes_table` ADD UNIQUE KEY `uk_number` (`number`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'orders_refund_reply' AND INDEX_NAME = 'uk_orders_id') = 0,
    'ALTER TABLE `orders_refund_reply` ADD UNIQUE KEY `uk_orders_id` (`orders_id`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @ddl = IF((SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'dishes_evaluations' AND INDEX_NAME = 'uk_user_dishes') = 0,
    'ALTER TABLE `dishes_evaluations` ADD UNIQUE KEY `uk_user_dishes` (`user_id`, `dishes_id`)', 'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;
