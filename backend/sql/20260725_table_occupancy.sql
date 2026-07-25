-- 餐桌就餐状态：保留 status 作为管理员设置的可用状态，occupied 表示被进行中订单占用。
SET @occupancy_column_exists = (SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'dishes_table' AND column_name = 'occupied');
SET @occupancy_sql = IF(@occupancy_column_exists = 0, 'ALTER TABLE dishes_table ADD COLUMN occupied tinyint(1) NOT NULL DEFAULT 0 AFTER status', 'SELECT 1');
PREPARE occupancy_statement FROM @occupancy_sql;
EXECUTE occupancy_statement;
DEALLOCATE PREPARE occupancy_statement;

-- 为升级前已存在的待支付、已支付和出餐中订单恢复占桌状态。
UPDATE dishes_table dt
JOIN orders o ON o.dishes_table_id = dt.id
SET dt.occupied = 1
WHERE o.status IN (1, 2, 3);
