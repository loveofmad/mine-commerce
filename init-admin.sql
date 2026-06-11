-- 插入默认管理员账号
-- 账号: admin
-- 密码: admin123
INSERT INTO tb_admin (username, password, nickname, status, create_time, update_time, deleted) 
VALUES ('admin', 'admin123', '超级管理员', 1, NOW(), NOW(), 0);
