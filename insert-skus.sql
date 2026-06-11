USE mine_commerce;

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
