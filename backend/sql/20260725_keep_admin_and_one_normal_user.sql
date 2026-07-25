-- 清理测试账号：仅保留管理员 admin（ID 1）和普通用户 zhouzhiruo（ID 2）。
-- 执行前会清理待删除账号的订单及相关数据，并按未结束订单重新计算餐桌占用状态。

START TRANSACTION;

CREATE TEMPORARY TABLE users_to_delete AS
SELECT id
FROM `user`
WHERE id NOT IN (1, 2);

CREATE TEMPORARY TABLE orders_to_delete AS
SELECT id
FROM orders
WHERE user_id IN (SELECT id FROM users_to_delete);

DELETE i
FROM images i
INNER JOIN dishes_evaluations e ON e.id = i.dishes_evaluations_id
INNER JOIN users_to_delete u ON u.id = e.user_id;

DELETE e
FROM dishes_evaluations e
INNER JOIN users_to_delete u ON u.id = e.user_id;

DELETE oi
FROM orders_item oi
INNER JOIN orders_to_delete o ON o.id = oi.orders_id;

DELETE rr
FROM orders_refund_reply rr
INNER JOIN orders_to_delete o ON o.id = rr.orders_id;

DELETE wi
FROM wallet_info wi
INNER JOIN wallet w ON w.id = wi.wallet_id
INNER JOIN users_to_delete u ON u.id = w.user_id;

DELETE w
FROM wallet w
INNER JOIN users_to_delete u ON u.id = w.user_id;

DELETE sc
FROM shipping_car sc
INNER JOIN users_to_delete u ON u.id = sc.user_id;

DELETE c
FROM collection c
INNER JOIN users_to_delete u ON u.id = c.user_id;

DELETE m
FROM messages m
INNER JOIN users_to_delete u ON u.id = m.user_id;

DELETE o
FROM orders o
INNER JOIN orders_to_delete d ON d.id = o.id;

DELETE u
FROM `user` u
INNER JOIN users_to_delete d ON d.id = u.id;

UPDATE dishes_table t
SET occupied = EXISTS (
    SELECT 1
    FROM orders o
    WHERE o.dishes_table_id = t.id
      AND o.status IN (1, 2, 3)
);

COMMIT;
