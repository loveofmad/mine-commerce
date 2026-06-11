USE mine_commerce;

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
