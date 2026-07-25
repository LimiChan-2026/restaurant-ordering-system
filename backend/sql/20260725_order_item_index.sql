USE `rrs`;

-- 一个订单可以包含多个订单项。旧版本误将 orders_item.orders_id 设为唯一索引，
-- 本迁移安全地将其替换为普通索引；不会删除任何已有订单数据。

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'orders_item' AND index_name = 'uk_orders_id') > 0,
    'ALTER TABLE orders_item DROP INDEX uk_orders_id',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE() AND table_name = 'orders_item' AND index_name = 'idx_orders_id') = 0,
    'ALTER TABLE orders_item ADD KEY idx_orders_id (orders_id)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
