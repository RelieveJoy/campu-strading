DROP TABLE IF EXISTS report;
DROP TABLE IF EXISTS lost_found;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS banner;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS notification;
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
    bio           VARCHAR(200) DEFAULT NULL,
    gender        VARCHAR(10)  DEFAULT NULL,
    birthday      DATE         DEFAULT NULL,
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
    item_condition TINYINT        DEFAULT NULL,
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
    source_type VARCHAR(10) DEFAULT 'item',
    sender_id   BIGINT   NOT NULL,
    receiver_id BIGINT   NOT NULL,
    content     TEXT     NOT NULL,
    is_read     TINYINT  DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id)
);

CREATE TABLE notification (
    notification_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id         BIGINT       NOT NULL,
    initiator_id    BIGINT       NOT NULL,
    initiator_name  VARCHAR(50)  DEFAULT NULL,
    type            VARCHAR(30)  NOT NULL,
    order_id        BIGINT       NOT NULL,
    item_title      VARCHAR(100) DEFAULT NULL,
    content         VARCHAR(200) NOT NULL,
    is_read         TINYINT      DEFAULT 0,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id)
);

CREATE TABLE review (
    review_id   BIGINT       NOT NULL AUTO_INCREMENT,
    order_id    BIGINT       NOT NULL,
    item_id     BIGINT       NOT NULL,
    reviewer_id BIGINT       NOT NULL,
    target_id   BIGINT       NOT NULL,
    rating      TINYINT      NOT NULL DEFAULT 0,
    content     VARCHAR(500) DEFAULT NULL,
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (review_id),
    UNIQUE KEY uk_order (order_id),
    UNIQUE KEY uk_item_reviewer (item_id, reviewer_id)
);

CREATE TABLE banner (
    banner_id   BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(100) NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    link_url    VARCHAR(500) DEFAULT NULL,
    sort        INT          DEFAULT 0,
    status      TINYINT      DEFAULT 1,
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_user BIGINT       DEFAULT NULL,
    update_user BIGINT       DEFAULT NULL,
    PRIMARY KEY (banner_id)
);

CREATE TABLE announcement (
    announcement_id BIGINT       NOT NULL AUTO_INCREMENT,
    title           VARCHAR(100) NOT NULL,
    content         TEXT         DEFAULT NULL,
    status          TINYINT      DEFAULT 1,
    create_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    create_user     BIGINT       DEFAULT NULL,
    update_user     BIGINT       DEFAULT NULL,
    PRIMARY KEY (announcement_id)
);

CREATE TABLE lost_found (
    lost_found_id BIGINT       NOT NULL AUTO_INCREMENT,
    title         VARCHAR(100) NOT NULL,
    description   TEXT         DEFAULT NULL,
    image_url     VARCHAR(500) DEFAULT NULL,
    category      VARCHAR(10)  NOT NULL,
    location      VARCHAR(200) DEFAULT NULL,
    contact       VARCHAR(100) DEFAULT NULL,
    user_id       BIGINT       NOT NULL,
    create_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (lost_found_id)
);

CREATE TABLE report (
    report_id   BIGINT       NOT NULL AUTO_INCREMENT,
    reporter_id BIGINT       NOT NULL,
    target_type VARCHAR(10)  NOT NULL,
    target_id   BIGINT       NOT NULL,
    reason      VARCHAR(100) NOT NULL,
    description TEXT         DEFAULT NULL,
    status      TINYINT      DEFAULT 1,
    create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (report_id)
);
