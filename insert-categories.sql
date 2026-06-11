USE mine_commerce;

INSERT INTO tb_category (name, parent_id, level, icon, sort, status, create_time, update_time, deleted) VALUES
('水果', 0, 1, '/static/icon-fruit.png', 1, 1, NOW(), NOW(), 0),
('坚果', 0, 1, '/static/icon-nuts.png', 2, 1, NOW(), NOW(), 0),
('茶叶', 0, 1, '/static/icon-tea.png', 3, 1, NOW(), NOW(), 0),
('蜂蜜', 0, 1, '/static/icon-honey.png', 4, 1, NOW(), NOW(), 0),
('特产礼盒', 0, 1, '/static/icon-gift.png', 5, 1, NOW(), NOW(), 0);
