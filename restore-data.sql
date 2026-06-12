USE mine_commerce;

INSERT INTO tb_category (name, parent_id, level, icon, sort, status, create_time, update_time, deleted) VALUES
('水果', 0, 1, '/static/icon-fruit.png', 1, 1, NOW(), NOW(), 0),
('坚果', 0, 1, '/static/icon-nuts.png', 2, 1, NOW(), NOW(), 0),
('茶叶', 0, 1, '/static/icon-tea.png', 3, 1, NOW(), NOW(), 0),
('蜂蜜', 0, 1, '/static/icon-honey.png', 4, 1, NOW(), NOW(), 0),
('特产礼盒', 0, 1, '/static/icon-gift.png', 5, 1, NOW(), NOW(), 0);

INSERT INTO tb_spu (category_id, title, subtitle, main_image, images, detail, price, stock, sales, status, create_time, update_time, deleted) VALUES
(1, '新鲜苹果 5斤装', '产地直发，新鲜采摘', 'https://via.placeholder.com/300?text=Apple', '["https://via.placeholder.com/300?text=Apple"]', '新鲜苹果', 39.90, 500, 120, 1, NOW(), NOW(), 0),
(1, '进口橙子 10斤装', '美国进口，甜度高', 'https://via.placeholder.com/300?text=Orange', '["https://via.placeholder.com/300?text=Orange"]', '进口橙子', 69.90, 300, 85, 1, NOW(), NOW(), 0),
(2, '核桃 2斤装', '云南核桃，壳薄肉厚', 'https://via.placeholder.com/300?text=Walnut', '["https://via.placeholder.com/300?text=Walnut"]', '云南核桃', 49.90, 200, 65, 1, NOW(), NOW(), 0),
(2, '花生 5斤装', '农家自产，香脆可口', 'https://via.placeholder.com/300?text=Peanut', '["https://via.placeholder.com/300?text=Peanut"]', '农家花生', 29.90, 400, 95, 1, NOW(), NOW(), 0),
(3, '龙井茶 250g', '明前龙井，清香回甘', 'https://via.placeholder.com/300?text=Tea', '["https://via.placeholder.com/300?text=Tea"]', '明前龙井', 128.00, 150, 42, 1, NOW(), NOW(), 0),
(3, '普洱茶 357g', '云南普洱，陈年老茶', 'https://via.placeholder.com/300?text=Puer', '["https://via.placeholder.com/300?text=Puer"]', '云南普洱', 198.00, 100, 28, 1, NOW(), NOW(), 0),
(4, '百花蜂蜜 1斤装', '纯天然百花蜜', 'https://via.placeholder.com/300?text=Honey', '["https://via.placeholder.com/300?text=Honey"]', '百花蜜', 59.90, 250, 78, 1, NOW(), NOW(), 0),
(4, '枸杞蜂蜜 500g', '宁夏枸杞蜜', 'https://via.placeholder.com/300?text=Gouqi', '["https://via.placeholder.com/300?text=Gouqi"]', '枸杞蜜', 45.00, 180, 52, 1, NOW(), NOW(), 0),
(5, '云南特产礼盒', '精选云南特产组合', 'https://via.placeholder.com/300?text=Gift1', '["https://via.placeholder.com/300?text=Gift1"]', '特产礼盒', 168.00, 100, 35, 1, NOW(), NOW(), 0),
(5, '坚果礼盒', '多种坚果组合装', 'https://via.placeholder.com/300?text=Gift2', '["https://via.placeholder.com/300?text=Gift2"]', '坚果礼盒', 199.00, 80, 22, 1, NOW(), NOW(), 0);

INSERT INTO tb_user (username, password, nickname, phone, status) VALUES
('user1', '123456', '测试用户1', '13800138001', 1),
('user2', '123456', '测试用户2', '13800138002', 1),
('user3', '123456', '测试用户3', '13800138003', 1);

INSERT INTO tb_coupon (name, type, amount, min_amount, total, used, status, start_time, end_time) VALUES
('满100减10', 0, 10.00, 100.00, 1000, 156, 1, '2024-01-01', '2027-12-31 23:59:59'),
('满200减30', 0, 30.00, 200.00, 500, 89, 1, '2024-01-01', '2027-12-31 23:59:59'),
('满500减80', 0, 80.00, 500.00, 200, 45, 1, '2024-01-01', '2027-12-31 23:59:59');

INSERT INTO tb_admin (username, password, nickname, status) VALUES
('admin', 'admin123', '超级管理员', 1);
