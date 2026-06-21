CREATE DATABASE IF NOT EXISTS campus_trading DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE campus_trading;

CREATE TABLE IF NOT EXISTS user (
    user_id       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    student_id    VARCHAR(50)  NOT NULL COMMENT '学号',
    username      VARCHAR(50)  NOT NULL COMMENT '姓名',
    password      VARCHAR(255) NOT NULL COMMENT '密码',
    phone         VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    avatar        VARCHAR(255) DEFAULT NULL COMMENT '头像',
    status        TINYINT      DEFAULT 1 COMMENT '状态 1正常 0禁用',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user   BIGINT       DEFAULT NULL,
    update_user   BIGINT       DEFAULT NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_student_id (student_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS category (
    category_id BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50) NOT NULL COMMENT '分类名',
    sort        INT         DEFAULT 0 COMMENT '排序',
    status      TINYINT     DEFAULT 1 COMMENT '状态 1正常 0禁用',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user BIGINT      DEFAULT NULL,
    update_user BIGINT      DEFAULT NULL,
    PRIMARY KEY (category_id)
) ENGINE=InnoDB;

INSERT IGNORE INTO category VALUES (1,'教材书籍',1,1,NOW(),NOW(),NULL,NULL);
INSERT IGNORE INTO category VALUES (2,'电子产品',2,1,NOW(),NOW(),NULL,NULL);
INSERT IGNORE INTO category VALUES (3,'生活用品',3,1,NOW(),NOW(),NULL,NULL);
INSERT IGNORE INTO category VALUES (4,'衣物鞋帽',4,1,NOW(),NOW(),NULL,NULL);
INSERT IGNORE INTO category VALUES (5,'运动器材',5,1,NOW(),NOW(),NULL,NULL);

CREATE TABLE IF NOT EXISTS item (
    item_id        BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    seller_id      BIGINT         NOT NULL COMMENT '卖家用户ID',
    title          VARCHAR(100)   NOT NULL COMMENT '商品标题',
    description    TEXT           DEFAULT NULL COMMENT '商品描述',
    price          DECIMAL(10,2)  NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2)  DEFAULT NULL COMMENT '原价',
    category_id    BIGINT         DEFAULT NULL COMMENT '分类ID',
    status         TINYINT        DEFAULT 1 COMMENT '状态 1在售 0下架 2已售出',
    image_url      VARCHAR(500)   DEFAULT NULL COMMENT '图片URL',
    view_count     INT            DEFAULT 0 COMMENT '浏览量',
    create_time    DATETIME       DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user    BIGINT         DEFAULT NULL,
    update_user    BIGINT         DEFAULT NULL,
    PRIMARY KEY (item_id),
    KEY idx_seller (seller_id),
    KEY idx_category (category_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS orders (
    order_id    BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    item_id     BIGINT         NOT NULL COMMENT '商品ID',
    buyer_id    BIGINT         NOT NULL COMMENT '买家用户ID',
    seller_id   BIGINT         NOT NULL COMMENT '卖家用户ID',
    amount      DECIMAL(10,2)  NOT NULL COMMENT '成交价',
    status      TINYINT        DEFAULT 1 COMMENT '1待确认 2已完成 3已取消',
    create_time DATETIME       DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user BIGINT         DEFAULT NULL,
    update_user BIGINT         DEFAULT NULL,
    PRIMARY KEY (order_id),
    KEY idx_buyer (buyer_id),
    KEY idx_seller (seller_id),
    KEY idx_item (item_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS favorite (
    favorite_id BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT   NOT NULL COMMENT '用户ID',
    item_id     BIGINT   NOT NULL COMMENT '商品ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (favorite_id),
    KEY idx_user (user_id),
    KEY idx_item (item_id),
    UNIQUE KEY uk_user_item (user_id, item_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS message (
    message_id  BIGINT   NOT NULL AUTO_INCREMENT COMMENT '主键',
    item_id     BIGINT   NOT NULL COMMENT '商品ID',
    sender_id   BIGINT   NOT NULL COMMENT '发送者用户ID',
    receiver_id BIGINT   NOT NULL COMMENT '接收者用户ID',
    content     TEXT     NOT NULL COMMENT '消息内容',
    is_read     TINYINT  DEFAULT 0 COMMENT '0未读 1已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (message_id),
    KEY idx_item (item_id),
    KEY idx_receiver (receiver_id),
    KEY idx_item_users (item_id, sender_id, receiver_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS notification (
    notification_id BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id         BIGINT       NOT NULL COMMENT '接收通知的用户ID',
    initiator_id    BIGINT       NOT NULL COMMENT '触发通知的用户ID',
    initiator_name  VARCHAR(50)  DEFAULT NULL COMMENT '触发者用户名',
    type            VARCHAR(30)  NOT NULL COMMENT 'CREATED/CONFIRMED/CANCELLED',
    order_id        BIGINT       NOT NULL COMMENT '关联订单ID',
    item_title      VARCHAR(100) DEFAULT NULL COMMENT '商品标题',
    content         VARCHAR(200) NOT NULL COMMENT '通知内容',
    is_read         TINYINT      DEFAULT 0 COMMENT '0未读 1已读',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (notification_id),
    KEY idx_user_read (user_id, is_read),
    KEY idx_time (create_time)
) ENGINE=InnoDB;
