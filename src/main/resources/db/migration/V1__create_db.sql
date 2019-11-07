CREATE TABLE items (
    id serial PRIMARY KEY,
    item_code varchar,
    item_name varchar,
    price integer
);

CREATE TABLE users (
    id serial PRIMARY KEY,
    first_name varchar,
    last_name varchar,
    login varchar,
    password varchar
);

CREATE TABLE carts (
    id serial PRIMARY KEY,
    closed boolean,
    creation_time bigint,
    user_id integer
);

ALTER TABLE carts
ADD FOREIGN KEY (user_id) REFERENCES users(id);

CREATE TABLE orders (
    id serial PRIMARY KEY,
    amount integer,
    cart_id integer,
    item_id integer
);

ALTER TABLE orders
    ADD FOREIGN KEY (item_id) REFERENCES items(id);

ALTER TABLE orders
    ADD FOREIGN KEY (cart_id) REFERENCES carts(id);

