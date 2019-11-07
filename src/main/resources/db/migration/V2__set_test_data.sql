
INSERT INTO users (id, first_name, last_name, login, password) VALUES (12, 'Stepan', 'Rych', 'sters', '123456');
INSERT INTO users (id, first_name, last_name, login, password) VALUES (15, 'fghfh', 'gfhfh', 'stersk', '11');

INSERT INTO items (id, item_code, item_name, price) VALUES (1, '1234', 'First test item', 12333);
INSERT INTO items (id, item_code, item_name, price) VALUES (2, '2222', 'Second test item', 32333);

INSERT INTO carts (id, closed, creation_time, user_id) VALUES (13, true, 1569832780104, 12);
INSERT INTO carts (id, closed, creation_time, user_id) VALUES (14, true, 1569836256050, 12);
INSERT INTO carts (id, closed, creation_time, user_id) VALUES (15, true, 1572847730005, 12);

INSERT INTO orders (id, amount, cart_id, item_id) VALUES (16, 1, 13, 2);
INSERT INTO orders (id, amount, cart_id, item_id) VALUES (17, 1, 14, 2);
INSERT INTO orders (id, amount, cart_id, item_id) VALUES (18, 2, 14, 1);
INSERT INTO orders (id, amount, cart_id, item_id) VALUES (19, 2, 15, 2);
