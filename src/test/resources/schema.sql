CREATE TABLE IF NOT EXISTS product
(
    product_id         INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    product_name       VARCHAR(128)  NOT NULL,
    category           VARCHAR(32)  NOT NULL,
    image_url          VARCHAR(256) NOT NULL,
    price              INT          NOT NULL CHECK (price >= 0),
    stock              INT          NOT NULL CHECK (stock >= 0),
    description        VARCHAR(1024),
    status             VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'INACTIVE')),
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
    );


CREATE TABLE IF NOT EXISTS user
(
    user_id            INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email              VARCHAR(256) NOT NULL UNIQUE,
    role               VARCHAR(16) NOT NULL CHECK (role IN ('ROLE_USER', 'ROLE_ADMIN')),
    password           VARCHAR(256) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS `order`
(
    order_id           INT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INT       NOT NULL,
    total_amount       INT       NOT NULL CHECK (total_amount >= 0),
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_item_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id      INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL CHECK (quantity > 0),
    amount        INT NOT NULL CHECK (amount >= 0),
    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX IF NOT EXISTS idx_order_user_created
    ON `order` (user_id, created_date);

CREATE INDEX IF NOT EXISTS idx_order_item_order
    ON order_item (order_id);

CREATE INDEX IF NOT EXISTS idx_order_item_product
    ON order_item (product_id);

CREATE INDEX IF NOT EXISTS idx_product_category_created
    ON product (category, created_date);

CREATE INDEX IF NOT EXISTS idx_product_status_category_created
    ON product (status, category, created_date);
