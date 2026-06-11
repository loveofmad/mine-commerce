USE mine_commerce;

-- 插入测试数据

-- 1. 插入分类数据
INSERT INTO tb_category (name, parent_id, level, icon, sort, status, create_time, update_time, deleted) VALUES
('水果', 0, 1, '🍎', 1, 1, NOW(), NOW(), 0),
('坚果', 0, 1, '🥜', 2, 1, NOW(), NOW(), 0),
('茶叶', 0, 1, '🍵', 3, 1, NOW(), NOW(), 0),
('蜂蜜', 0, 1, '🍯', 4, 1, NOW(), NOW(), 0),
('特产礼盒', 0, 1, '🎁', 5, 1, NOW(), NOW(), 0);

-- 2. 插入品牌数据
INSERT INTO tb_brand (name, logo, description, sort, status, create_time, update_time, deleted) VALUES
('云南特产', NULL, '云南本地特产', 1, 1, NOW(), NOW(), 0),
('四川风味', NULL, '四川特色风味', 2, 1, NOW(), NOW(), 0),
('农家自产', NULL, '农家自产自销', 3, 1, NOW(), NOW(), 0);

-- 3. 插入商品SPU数据
INSERT INTO tb_spu (category_id, title, subtitle, main_image, images, detail, price, stock, sales, status, create_time, update_time, deleted) VALUES
(1, '新鲜苹果 5斤装', '产地直发，新鲜采摘', '/static/products/apple.jpg', '["/static/products/apple.jpg"]', '新鲜苹果，产地直发，5斤装', 39.90, 500, 120, 1, NOW(), NOW(), 0),
(1, '进口橙子 10斤装', '美国进口，甜度高', '/static/products/orange.jpg', '["/static/products/orange.jpg"]', '进口橙子，甜度高，10斤装', 69.90, 300, 85, 1, NOW(), NOW(), 0),
(2, '核桃 2斤装', '云南核桃，壳薄肉厚', '/static/products/walnut.jpg', '["/static/products/walnut.jpg"]', '云南核桃，壳薄肉厚，2斤装', 49.90, 200, 65, 1, NOW(), NOW(), 0),
(2, '花生 5斤装', '农家自产，香脆可口', '/static/products/peanut.jpg', '["/static/products/peanut.jpg"]', '农家自产花生，香脆可口，5斤装', 29.90, 400, 95, 1, NOW(), NOW(), 0),
(3, '龙井茶 250g', '明前龙井，清香回甘', '/static/products/longjing.jpg', '["/static/products/longjing.jpg"]', '明前龙井，清香回甘，250g装', 128.00, 150, 42, 1, NOW(), NOW(), 0),
(3, '普洱茶 357g', '云南普洱，陈年老茶', '/static/products/puer.jpg', '["/static/products/puer.jpg"]', '云南普洱，陈年老茶，357g饼', 198.00, 100, 28, 1, NOW(), NOW(), 0),
(4, '百花蜂蜜 1斤装', '纯天然百花蜜', '/static/products/honey.jpg', '["/static/products/honey.jpg"]', '纯天然百花蜜，1斤装', 59.90, 250, 78, 1, NOW(), NOW(), 0),
(4, '枸杞蜂蜜 500g', '宁夏枸杞蜜', '/static/products/gouqi.jpg', '["/static/products/gouqi.jpg"]', '宁夏枸杞蜜，500g装', 45.00, 180, 52, 1, NOW(), NOW(), 0),
(5, '云南特产礼盒', '精选云南特产组合', '/static/products/giftbox1.jpg', '["/static/products/giftbox1.jpg"]', '精选云南特产组合，送礼佳品', 168.00, 100, 35, 1, NOW(), NOW(), 0),
(5, '坚果礼盒', '多种坚果组合装', '/static/products/giftbox2.jpg', '["/static/products/giftbox2.jpg"]', '多种坚果组合装，送礼佳品', 199.00, 80, 22, 1, NOW(), NOW(), 0);

-- 4. 插入SKU数据
INSERT INTO tb_sku (spu_id, title, specs, image, price, stock, sales, status, create_time, update_time, deleted) VALUES
(1, '新鲜苹果 5斤装', '{"重量":"5斤"}', '/static/products/apple.jpg', 39.90, 500, 120, 1, NOW(), NOW(), 0),
(2, '进口橙子 10斤装', '{"重量":"10斤"}', '/static/products/orange.jpg', 69.90, 300, 85, 1, NOW(), NOW(), 0),
(3, '核桃 2斤装', '{"重量":"2斤"}', '/static/products/walnut.jpg', 49.90, 200, 65, 1, NOW(), NOW(), 0),
(4, '花生 5斤装', '{"重量":"5斤"}', '/static/products/peanut.jpg', 29.90, 400, 95, 1, NOW(), NOW(), 0),
(5, '龙井茶 250g', '{"重量":"250g"}', '/static/products/longjing.jpg', 128.00, 150, 42, 1, NOW(), NOW(), 0),
(6, '普洱茶 357g', '{"重量":"357g"}', '/static/products/puer.jpg', 198.00, 100, 28, 1, NOW(), NOW(), 0),
(7, '百花蜂蜜 1斤装', '{"重量":"1斤"}', '/static/products/honey.jpg', 59.90, 250, 78, 1, NOW(), NOW(), 0),
(8, '枸杞蜂蜜 500g', '{"重量":"500g"}', '/static/products/gouqi.jpg', 45.00, 180, 52, 1, NOW(), NOW(), 0),
(9, '云南特产礼盒', '{"规格":"礼盒装"}', '/static/products/giftbox1.jpg', 168.00, 100, 35, 1, NOW(), NOW(), 0),
(10, '坚果礼盒', '{"规格":"礼盒装"}', '/static/products/giftbox2.jpg', 199.00, 80, 22, 1, NOW(), NOW(), 0);

-- 5. 插入轮播图数据
INSERT INTO tb_banner (title, image, url, sort, status, create_time, update_time, deleted) VALUES
('春季特惠', '/static/banner/banner1.jpg', '/pages/product/list', 1, 1, NOW(), NOW(), 0),
('新品上市', '/static/banner/banner2.jpg', '/pages/product/list', 2, 1, NOW(), NOW(), 0),
('限时抢购', '/static/banner/banner3.jpg', '/pages/product/list', 3, 1, NOW(), NOW(), 0);

-- 6. 插入优惠券数据
INSERT INTO tb_coupon (name, type, amount, min_amount, total, used, per_limit, status, start_time, end_time, create_time, update_time, deleted) VALUES
('满100减10', 0, 10.00, 100.00, 1000, 156, 1, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', NOW(), NOW(), 0),
('满200减30', 0, 30.00, 200.00, 500, 89, 1, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', NOW(), NOW(), 0),
('满500减80', 0, 80.00, 500.00, 200, 45, 1, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', NOW(), NOW(), 0),
('9折优惠券', 1, 0.90, 0.00, 300, 67, 2, 1, '2024-01-01 00:00:00', '2024-12-31 23:59:59', NOW(), NOW(), 0);

-- 7. 插入测试用户数据
INSERT INTO tb_user (username, password, nickname, avatar, phone, email, status, create_time, update_time, deleted) VALUES
('user1', '123456', '测试用户1', NULL, '13800138001', 'user1@test.com', 1, NOW(), NOW(), 0),
('user2', '123456', '测试用户2', NULL, '13800138002', 'user2@test.com', 1, NOW(), NOW(), 0),
('user3', '123456', '测试用户3', NULL, '13800138003', 'user3@test.com', 1, NOW(), NOW(), 0);

-- 8. 插入测试订单数据
INSERT INTO tb_order (order_no, user_id, address_id, total_amount, pay_amount, freight_amount, discount_amount, pay_type, status, order_type, receiver_name, receiver_phone, receiver_address, remark, pay_time, deliver_time, receive_time, create_time, update_time, deleted) VALUES
('ORDER20240101001', 1, NULL, 39.90, 39.90, 0.00, 0.00, 0, 3, 0, '张三', '13800138001', '北京市朝阳区xxx', NULL, '2024-01-01 10:00:00', '2024-01-02 10:00:00', '2024-01-03 10:00:00', '2024-01-01 09:00:00', NOW(), 0),
('ORDER20240101002', 1, NULL, 168.90, 158.90, 0.00, 10.00, 0, 2, 0, '张三', '13800138001', '北京市朝阳区xxx', '尽快发货', '2024-01-01 11:00:00', '2024-01-02 11:00:00', NULL, '2024-01-01 10:30:00', NOW(), 0),
('ORDER20240101003', 2, NULL, 69.90, 69.90, 0.00, 0.00, 0, 1, 0, '李四', '13800138002', '上海市浦东新区xxx', NULL, '2024-01-01 12:00:00', NULL, NULL, '2024-01-01 11:00:00', NOW(), 0),
('ORDER20240101004', 2, NULL, 278.00, 248.00, 0.00, 30.00, 0, 0, 0, '李四', '13800138002', '上海市浦东新区xxx', NULL, NULL, NULL, NULL, '2024-01-01 11:30:00', NOW(), 0),
('ORDER20240101005', 3, NULL, 99.80, 99.80, 0.00, 0.00, 0, 3, 0, '王五', '13800138003', '广州市天河区xxx', '周末在家', '2024-01-01 13:00:00', '2024-01-02 13:00:00', '2024-01-03 13:00:00', '2024-01-01 12:00:00', NOW(), 0);

-- 9. 插入订单明细数据
INSERT INTO tb_order_item (order_id, spu_id, sku_id, spu_name, sku_title, sku_image, price, quantity, total_amount, create_time, update_time) VALUES
(1, 1, 1, '新鲜苹果 5斤装', '新鲜苹果 5斤装', '/static/products/apple.jpg', 39.90, 1, 39.90, NOW(), NOW()),
(2, 1, 1, '新鲜苹果 5斤装', '新鲜苹果 5斤装', '/static/products/apple.jpg', 39.90, 2, 79.80, NOW(), NOW()),
(2, 3, 3, '核桃 2斤装', '核桃 2斤装', '/static/products/walnut.jpg', 49.90, 2, 99.80, NOW(), NOW()),
(3, 2, 2, '进口橙子 10斤装', '进口橙子 10斤装', '/static/products/orange.jpg', 69.90, 1, 69.90, NOW(), NOW()),
(4, 5, 5, '龙井茶 250g', '龙井茶 250g', '/static/products/longjing.jpg', 128.00, 1, 128.00, NOW(), NOW()),
(4, 7, 7, '百花蜂蜜 1斤装', '百花蜂蜜 1斤装', '/static/products/honey.jpg', 59.90, 1, 59.90, NOW(), NOW()),
(4, 4, 4, '花生 5斤装', '花生 5斤装', '/static/products/peanut.jpg', 29.90, 3, 89.70, NOW(), NOW()),
(5, 3, 3, '核桃 2斤装', '核桃 2斤装', '/static/products/walnut.jpg', 49.90, 2, 99.80, NOW(), NOW());
