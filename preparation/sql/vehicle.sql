-- =========================================================
--  汽车租赁系统（含管理员 + 车商 + RBAC + 工作日/周末费率 + 逻辑删除）
--  MySQL 8.0+  直接执行
-- =========================================================
CREATE DATABASE IF NOT EXISTS car_rental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE car_rental;

-- 1. 用户主表（租客 + 车商 + 管理员 统一入口） ------------
CREATE TABLE `user` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '用户主键',
    `username`    VARCHAR(50) NOT NULL COMMENT '登录账号',
    `password`    CHAR(60)    NOT NULL COMMENT 'bcrypt密文',
    `phone`       VARCHAR(20) COMMENT '手机号',
    `email`       VARCHAR(100) COMMENT '邮箱',
    `id_card`     VARCHAR(18) COMMENT '身份证号',
    `birthday`    DATE COMMENT '生日',
    `balance`     DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    `points`      INT DEFAULT 0 COMMENT '积分余额',
    `grow_value`  INT DEFAULT 0 COMMENT '成长值',
    `status`      TINYINT DEFAULT 1 COMMENT '1 正常 0 禁用',
    `deleted`     TINYINT DEFAULT 0 COMMENT '逻辑删除：0 未删 1 已删',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_username` (`username`)
) ENGINE = InnoDB COMMENT = '用户主表（租客/车商/管理员）';

-- 2. 会员等级 ----------------------------------------------
CREATE TABLE `member_level` (
    `id`          INT AUTO_INCREMENT COMMENT '等级主键',
    `level_name`  VARCHAR(20) NOT NULL COMMENT '等级名称',
    `min_value`   INT NOT NULL COMMENT '成长值下限',
    `max_value`   INT NOT NULL COMMENT '成长值上限',
    `discount`    DECIMAL(3,2) DEFAULT 1.00 COMMENT '租车折扣',
    `deleted`     TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT = '会员等级区间';

-- 3. 品牌字典 ---------------------------------------------
CREATE TABLE `brand` (
    `id`   INT AUTO_INCREMENT COMMENT '品牌主键',
    `name` VARCHAR(50) NOT NULL COMMENT '品牌名称',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_brand_name` (`name`)
) ENGINE = InnoDB COMMENT = '车辆品牌字典';

-- 4. 车型分类字典 -----------------------------------------
CREATE TABLE `car_category` (
    `id`   INT AUTO_INCREMENT COMMENT '分类主键',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_name` (`name`)
) ENGINE = InnoDB COMMENT = '车型分类字典';

-- 5. 车商入驻表 -------------------------------------------
CREATE TABLE `merchant` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '车商主键',
    `user_id`     BIGINT NOT NULL COMMENT '关联 user.id（车商账号）',
    `company`     VARCHAR(100) NOT NULL COMMENT '公司全称',
    `license_no`  VARCHAR(50) NOT NULL COMMENT '营业执照号码',
    `status`      TINYINT DEFAULT 1 COMMENT '1 正常 0 停用',
    `deleted`     TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_merchant_user` (`user_id`),
    UNIQUE KEY `uk_merchant_license` (`license_no`),
    CONSTRAINT `fk_merchant_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB COMMENT = '车商入驻信息';

-- 6. 车辆表 ------------------------------------------------
CREATE TABLE `car` (
    `id`            INT AUTO_INCREMENT COMMENT '车辆主键',
    `plate_no`      VARCHAR(10) NOT NULL COMMENT '车牌号',
    `model`         VARCHAR(50) NOT NULL COMMENT '具体型号',
    `brand_id`      INT NOT NULL COMMENT '品牌外键',
    `category_id`   INT NOT NULL COMMENT '分类外键',
    `merchant_id`   BIGINT COMMENT '车商外键（NULL=平台自营）',
    `price_daily`   DECIMAL(8,2) NOT NULL COMMENT '兜底日价（无费率规则时用）',
    `status`        TINYINT DEFAULT 1 COMMENT '1 空闲 2 出租中 3 维修',
    `img_url`       VARCHAR(255) COMMENT '车辆照片URL',
    `deleted`       TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_car_plate` (`plate_no`),
    KEY `idx_car_merchant` (`merchant_id`),
    CONSTRAINT `fk_car_brand`    FOREIGN KEY (`brand_id`)    REFERENCES `brand` (`id`),
    CONSTRAINT `fk_car_category` FOREIGN KEY (`category_id`) REFERENCES `car_category` (`id`),
    CONSTRAINT `fk_car_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `merchant` (`id`)
) ENGINE = InnoDB COMMENT = '车辆档案';

-- 7. 订单表 ------------------------------------------------
CREATE TABLE `order` (
    `id`           BIGINT AUTO_INCREMENT COMMENT '订单主键',
    `sn`           VARCHAR(32) NOT NULL COMMENT '业务单号',
    `user_id`      BIGINT NOT NULL COMMENT '租车用户外键',
    `car_id`       INT NOT NULL COMMENT '车辆外键',
    `days`         INT NOT NULL COMMENT '租用天数',
    `amount`       DECIMAL(10,2) NOT NULL COMMENT '总租金（已含工作日/周末差异）',
    `status`       TINYINT DEFAULT 0 COMMENT '0 待取车 1 已取车 2 已还车 3 已取消',
    `pick_time`    DATETIME NOT NULL COMMENT '取车时间',
    `return_time`  DATETIME NOT NULL COMMENT '还车时间',
    `deleted`      TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_sn` (`sn`),
    KEY `idx_order_user` (`user_id`),
    KEY `idx_order_car` (`car_id`),
    CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_order_car`  FOREIGN KEY (`car_id`)  REFERENCES `car` (`id`)
) ENGINE = InnoDB COMMENT = '租车订单';

-- 8. 账户流水 ---------------------------------------------
CREATE TABLE `account_record` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '流水主键',
    `user_id`     BIGINT NOT NULL COMMENT '用户外键',
    `type`        TINYINT NOT NULL COMMENT '1 充值 2 消费 3 退款',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '变动金额（正增负减）',
    `balance`     DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    `order_sn`    VARCHAR(32) COMMENT '关联订单号',
    `deleted`     TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_user` (`user_id`),
    CONSTRAINT `fk_record_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB COMMENT = '账户余额流水';

-- 9. 积分流水 ---------------------------------------------
CREATE TABLE `points_record` (
    `id`          BIGINT AUTO_INCREMENT COMMENT '积分流水主键',
    `user_id`     BIGINT NOT NULL COMMENT '用户外键',
    `points`      INT NOT NULL COMMENT '变动积分（正增负减）',
    `remark`      VARCHAR(100) COMMENT '来源说明',
    `deleted`     TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_points_user` (`user_id`),
    CONSTRAINT `fk_points_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB COMMENT = '积分变动流水';

-- 10. 费率规则（品牌+分类+是否周末） -----------------------
CREATE TABLE `rate_rule` (
    `id`            INT AUTO_INCREMENT COMMENT '费率主键',
    `brand_id`      INT NOT NULL COMMENT '品牌外键',
    `category_id`   INT NOT NULL COMMENT '分类外键',
    `is_weekend`    TINYINT NOT NULL COMMENT '0 工作日 1 周末',
    `base_price`    DECIMAL(8,2) NOT NULL COMMENT '起步价(元/天)',
    `ratio`         DECIMAL(3,2) DEFAULT 1.00 COMMENT '浮动系数',
    `deleted`       TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule` (`brand_id`,`category_id`,`is_weekend`),
    CONSTRAINT `fk_rule_brand`    FOREIGN KEY (`brand_id`)    REFERENCES `brand` (`id`),
    CONSTRAINT `fk_rule_category` FOREIGN KEY (`category_id`) REFERENCES `car_category` (`id`)
) ENGINE = InnoDB COMMENT = '工作日/周末费率规则';

-- 11. RBAC 权限表 -----------------------------------------
CREATE TABLE `permission` (
    `id`   INT AUTO_INCREMENT COMMENT '权限主键',
    `code` VARCHAR(50) NOT NULL COMMENT '权限编码：如 merchant:car:list',
    `name` VARCHAR(50) NOT NULL COMMENT '权限中文描述',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_perm_code` (`code`)
) ENGINE = InnoDB COMMENT = '权限点字典';

-- 12. RBAC 角色表 -----------------------------------------
CREATE TABLE `role` (
    `id`          INT AUTO_INCREMENT COMMENT '角色主键',
    `name`        VARCHAR(50) NOT NULL COMMENT '角色名称',
    `description` VARCHAR(100) COMMENT '角色描述',
    `deleted`     TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_name` (`name`)
) ENGINE = InnoDB COMMENT = '角色字典';

-- 13. 角色-权限 （多对多） ---------------------------------
CREATE TABLE `role_permission` (
    `role_id`       INT NOT NULL COMMENT '角色外键',
    `permission_id` INT NOT NULL COMMENT '权限外键',
    `deleted`       TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at`    DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`role_id`,`permission_id`),
    CONSTRAINT `fk_rp_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
    CONSTRAINT `fk_rp_perm` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE = InnoDB COMMENT = '角色拥有的权限';

-- 14. 用户-角色 （多对多） ---------------------------------
CREATE TABLE `user_role` (
    `user_id`  BIGINT NOT NULL COMMENT '用户外键',
    `role_id`  INT    NOT NULL COMMENT '角色外键',
    `deleted`  TINYINT DEFAULT 0 COMMENT '逻辑删除',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`user_id`,`role_id`),
    CONSTRAINT `fk_ur_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
    CONSTRAINT `fk_ur_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE = InnoDB COMMENT = '用户被赋予的角色';

-- =========================================================
--  初始化数据
-- =========================================================

-- 1. 会员等级
INSERT INTO member_level(level_name, min_value, max_value, discount, deleted, created_at) VALUES
('铜牌', 0, 999, 1.00, 0, NOW()),
('银牌', 1000, 4999, 0.95, 0, NOW()),
('金牌', 5000, 19999, 0.90, 0, NOW()),
('钻石', 20000, 999999, 0.85, 0, NOW());

-- 2. 品牌
INSERT INTO brand(name, deleted, created_at) VALUES
('大众', 0, NOW()),
('丰田', 0, NOW()),
('奔驰', 0, NOW());

-- 3. 分类
INSERT INTO car_category(name, deleted, created_at) VALUES
('经济型', 0, NOW()),
('SUV', 0, NOW()),
('豪华型', 0, NOW());

-- 4. 权限
INSERT INTO permission(code, name, deleted, created_at) VALUES
('user:order:add',      '用户下单', 0, NOW()),
('user:order:cancel',   '取消本人订单', 0, NOW()),
('merchant:car:list',   '查看本商户车辆', 0, NOW()),
('merchant:car:add',    '新增本商户车辆', 0, NOW()),
('merchant:car:upd',    '修改本商户车辆', 0, NOW()),
('merchant:car:del',    '删除本商户车辆', 0, NOW()),
('merchant:order:list', '查看本商户订单', 0, NOW()),
('sys:car:list',        '查看全部车辆', 0, NOW()),
('sys:car:upd',         '修改任意车辆', 0, NOW()),
('sys:order:list',      '查看全部订单', 0, NOW()),
('sys:merchant:audit',  '审核车商', 0, NOW());

-- 5. 角色
INSERT INTO role(name, description, deleted, created_at) VALUES
('普通用户', '租车客户', 0, NOW()),
('车商',     '入驻商户', 0, NOW()),
('运营',     '平台后台', 0, NOW()),
('超管',     '超级管理员', 0, NOW());

-- 6. 角色-权限
INSERT INTO role_permission(role_id, permission_id, deleted, created_at)
SELECT 1, id, 0, NOW() FROM permission WHERE code IN ('user:order:add','user:order:cancel');
INSERT INTO role_permission(role_id, permission_id, deleted, created_at)
SELECT 2, id, 0, NOW() FROM permission WHERE code LIKE 'merchant:%';
INSERT INTO role_permission(role_id, permission_id, deleted, created_at)
SELECT 3, id, 0, NOW() FROM permission WHERE code LIKE 'sys:%';
INSERT INTO role_permission(role_id, permission_id, deleted, created_at)
SELECT 4, id, 0, NOW() FROM permission;

-- 7. 演示账号（密码=123456）
INSERT INTO user(username, password, phone, balance, status, deleted, created_at)
VALUES
('user01',     '$2a$10$EuWPZHwh32Z0w9EtC1g6ZuQuO7c03RjLiMNRZHBW0F6xDB8Kq4dKy', '13800000001', 2000.00, 1, 0, NOW()),
('merchant01', '$2a$10$EuWPZHwh32Z0w9EtC1g6ZuQuO7c03RjLiMNRZHBW0F6xDB8Kq4dKy', '13800000002',    0.00, 1, 0, NOW()),
('admin01',    '$2a$10$EuWPZHwh32Z0w9EtC1g6ZuQuO7c03RjLiMNRZHBW0F6xDB8Kq4dKy', '13800000003',    0.00, 1, 0, NOW());

-- 8. 商户入驻
INSERT INTO merchant(user_id, company, license_no, status, deleted, created_at)
VALUES (2, '广州好车商有限公司', '91440101MA5T6XYZ', 1, 0, NOW());

-- 9. 用户-角色
INSERT INTO user_role(user_id, role_id, deleted, created_at) VALUES
(1, 1, 0, NOW()),   -- user01   -> 普通用户
(2, 2, 0, NOW()),   -- merchant01 -> 车商
(3, 4, 0, NOW());   -- admin01  -> 超管

-- 10. 费率规则
INSERT INTO rate_rule(brand_id, category_id, is_weekend, base_price, ratio, deleted, created_at)
VALUES
(1, 1, 0, 168.00, 1.0, 0, NOW()),   -- 大众经济型 工作日
(1, 1, 1, 168.00, 1.2, 0, NOW()),   -- 大众经济型 周末
(2, 2, 0, 268.00, 1.0, 0, NOW()),   -- 丰田SUV   工作日
(2, 2, 1, 268.00, 1.3, 0, NOW());   -- 丰田SUV   周末

-- 11. 车辆
INSERT INTO car(plate_no, model, brand_id, category_id, merchant_id, price_daily, status, deleted, created_at)
VALUES
('粤A00001', '朗逸', 1, 1, NULL, 168.00, 1, 0, NOW()),   -- 平台自营
('粤A00002', '凯美瑞', 2, 2, 1, 268.00, 1, 0, NOW());    -- 车商车辆