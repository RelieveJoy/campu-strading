DROP TABLE IF EXISTS message;
DROP TABLE IF EXISTS favorite;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
    user_id       BIGINT       NOT NULL AUTO_INCREMENT,
    student_id    VARCHAR(50)  NOT NULL,
    username      VARCHAR(50)  NOT NULL,
    password      VARCHAR(255) NOT NULL,
    phone         VARCHAR(20)  DEFAULT NULL,
    avatar        VARCHAR(255) DEFAULT NULL,
    status        TINYINT      DEFAULT 1,
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_user   BIGINT       DEFAULT NULL,
    update_user   BIGINT       DEFAULT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_student_id (student_id)
);

CREATE TABLE category (
    category_id BIGINT      NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL,
    sort        INT         DEFAULT 0,
    status      TINYINT     DEFAULT 1,
    create_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT      DEFAULT NULL,
    update_user BIGINT      DEFAULT NULL,
    PRIMARY KEY (category_id)
);

INSERT INTO category VALUES (1,'教材书籍',1,1,NOW(),NOW(),NULL,NULL);
INSERT INTO category VALUES (2,'电子产品',2,1,NOW(),NOW(),NULL,NULL);
INSERT INTO category VALUES (3,'生活用品',3,1,NOW(),NOW(),NULL,NULL);
INSERT INTO category VALUES (4,'衣物鞋帽',4,1,NOW(),NOW(),NULL,NULL);
INSERT INTO category VALUES (5,'运动器材',5,1,NOW(),NOW(),NULL,NULL);

CREATE TABLE item (
    item_id        BIGINT         NOT NULL AUTO_INCREMENT,
    seller_id      BIGINT         NOT NULL,
    title          VARCHAR(100)   NOT NULL,
    description    TEXT           DEFAULT NULL,
    price          DECIMAL(10,2)  NOT NULL,
    original_price DECIMAL(10,2)  DEFAULT NULL,
    category_id    BIGINT         DEFAULT NULL,
    status         TINYINT        DEFAULT 1,
    image_url      VARCHAR(500)   DEFAULT NULL,
    view_count     INT            DEFAULT 0,
    create_time    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    update_time    TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    create_user    BIGINT         DEFAULT NULL,
    update_user    BIGINT         DEFAULT NULL,
    PRIMARY KEY (item_id)
);

CREATE TABLE orders (
    order_id    BIGINT         NOT NULL AUTO_INCREMENT,
    item_id     BIGINT         NOT NULL,
    buyer_id    BIGINT         NOT NULL,
    seller_id   BIGINT         NOT NULL,
    amount      DECIMAL(10,2)  NOT NULL,
    status      TINYINT        DEFAULT 1,
    create_time TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT         DEFAULT NULL,
    update_user BIGINT         DEFAULT NULL,
    PRIMARY KEY (order_id)
);

CREATE TABLE favorite (
    favorite_id BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT   NOT NULL,
    item_id     BIGINT   NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (favorite_id),
    UNIQUE KEY uk_user_item (user_id, item_id)
);

CREATE TABLE message (
    message_id  BIGINT   NOT NULL AUTO_INCREMENT,
    item_id     BIGINT   NOT NULL,
    sender_id   BIGINT   NOT NULL,
    receiver_id BIGINT   NOT NULL,
    content     TEXT     NOT NULL,
    is_read     TINYINT  DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id)
);
