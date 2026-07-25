-- 为 backend/uploads/ 中已有同名图片的菜品设置封面地址。
-- 可重复执行：按菜品名称更新，不影响未匹配图片的菜品。

UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/宫保鸡丁.jpg' WHERE name = '宫保鸡丁';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/红烧肉.jpg' WHERE name = '红烧肉';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/鱼香肉丝.jpg' WHERE name = '鱼香肉丝';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/麻婆豆腐.jpg' WHERE name = '麻婆豆腐';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/凉拌黄瓜.jpg' WHERE name = '凉拌黄瓜';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/皮蛋豆腐.jpg' WHERE name = '皮蛋豆腐';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/番茄蛋汤.jpg' WHERE name = '番茄蛋汤';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/紫菜蛋花汤.jpg' WHERE name = '紫菜蛋花汤';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/白米饭.jpg' WHERE name = '白米饭';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/蛋炒饭.jpg' WHERE name = '蛋炒饭';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/可乐.jpg' WHERE name = '可乐';
UPDATE dishes SET cover_url = 'http://localhost:8081/uploads/柠檬水.jpg' WHERE name = '柠檬水';
