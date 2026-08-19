-- ============================================================
-- 宠物网站系统 - MySQL DDL 建表语句
-- 根据 com.example.entity 包下所有实体类自动生成
-- 数据库: java_pet  字符集: utf8mb4
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表 (b_user)
-- ----------------------------
DROP TABLE IF EXISTS `b_user`;
CREATE TABLE `b_user` (
  `id`            VARCHAR(50)   NOT NULL             COMMENT '用户ID',
  `username`      VARCHAR(100)  NULL DEFAULT NULL     COMMENT '用户名',
  `password`      VARCHAR(200)  NULL DEFAULT NULL     COMMENT '密码',
  `nickname`      VARCHAR(100)  NULL DEFAULT NULL     COMMENT '昵称',
  `mobile`        VARCHAR(30)   NULL DEFAULT NULL     COMMENT '手机号',
  `email`         VARCHAR(100)  NULL DEFAULT NULL     COMMENT '邮箱',
  `description`   VARCHAR(500)  NULL DEFAULT NULL     COMMENT '个人简介',
  `role`          VARCHAR(20)   NULL DEFAULT NULL     COMMENT '角色(1-普通用户 2-演示用户 3-管理员)',
  `status`        VARCHAR(20)   NULL DEFAULT NULL     COMMENT '状态',
  `score`         VARCHAR(10)   NULL DEFAULT NULL     COMMENT '积分',
  `avatar`        VARCHAR(200)  NULL DEFAULT NULL     COMMENT '头像',
  `token`         VARCHAR(500)  NULL DEFAULT NULL     COMMENT '登录token',
  `create_time`   VARCHAR(30)   NULL DEFAULT NULL     COMMENT '创建时间',
  `push_email`    VARCHAR(100)  NULL DEFAULT NULL     COMMENT '推送邮箱',
  `push_switch`   VARCHAR(10)   NULL DEFAULT NULL     COMMENT '推送开关',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '用户表';

-- ----------------------------
-- 2. 商品表 (b_thing)
-- ----------------------------
DROP TABLE IF EXISTS `b_thing`;
CREATE TABLE `b_thing` (
  `id`                BIGINT(20)    NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `title`             VARCHAR(200)  NULL DEFAULT NULL     COMMENT '商品名称',
  `nickname`          VARCHAR(100)  NULL DEFAULT NULL     COMMENT '昵称',
  `sex`               VARCHAR(10)   NULL DEFAULT NULL     COMMENT '性别',
  `address`           VARCHAR(200)  NULL DEFAULT NULL     COMMENT '地址',
  `cover`             VARCHAR(200)  NULL DEFAULT NULL     COMMENT '封面图片',
  `description`       TEXT          NULL                   COMMENT '商品描述',
  `price`             VARCHAR(20)   NULL DEFAULT NULL     COMMENT '价格',
  `status`            VARCHAR(20)   NULL DEFAULT NULL     COMMENT '状态',
  `create_time`       VARCHAR(30)   NULL DEFAULT NULL     COMMENT '创建时间',
  `score`             VARCHAR(10)   NULL DEFAULT NULL     COMMENT '评分',
  `pv`                VARCHAR(20)   NULL DEFAULT NULL     COMMENT '浏览量',
  `recommend_count`   INT(11)       NULL DEFAULT 0        COMMENT '推荐数',
  `wish_count`        INT(11)       NULL DEFAULT 0        COMMENT '心愿数',
  `collect_count`     INT(11)       NULL DEFAULT 0        COMMENT '收藏数',
  `classification_id` BIGINT(20)    NULL DEFAULT NULL     COMMENT '分类ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '商品表';

-- ----------------------------
-- 3. 分类表 (b_classification)
-- ----------------------------
DROP TABLE IF EXISTS `b_classification`;
CREATE TABLE `b_classification` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `title`       VARCHAR(100) NULL DEFAULT NULL     COMMENT '分类名称',
  `create_time` VARCHAR(30)  NOT NULL              COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '分类表';

-- ----------------------------
-- 4. 标签表 (b_tag)
-- ----------------------------
DROP TABLE IF EXISTS `b_tag`;
CREATE TABLE `b_tag` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `title`       VARCHAR(100) NULL DEFAULT NULL     COMMENT '标签名称',
  `create_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '标签表';

-- ----------------------------
-- 5. 商品标签关联表 (b_thing_tag)
-- ----------------------------
DROP TABLE IF EXISTS `b_thing_tag`;
CREATE TABLE `b_thing_tag` (
  `id`       BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `thing_id` BIGINT(20) NULL DEFAULT NULL     COMMENT '商品ID',
  `tag_id`   BIGINT(20) NULL DEFAULT NULL     COMMENT '标签ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '商品标签关联表';

-- ----------------------------
-- 6. 地址表 (b_address)
-- ----------------------------
DROP TABLE IF EXISTS `b_address`;
CREATE TABLE `b_address` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `name`        VARCHAR(100) NULL DEFAULT NULL     COMMENT '收货人姓名',
  `mobile`      VARCHAR(30)  NULL DEFAULT NULL     COMMENT '手机号码',
  `description` VARCHAR(200) NULL DEFAULT NULL     COMMENT '详细地址',
  `def`         VARCHAR(10)  NULL DEFAULT NULL     COMMENT '是否默认地址',
  `create_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '创建时间',
  `user_id`     BIGINT(20)   NULL DEFAULT NULL     COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `b_address_user_id_a37a8d6a_fk_b_user_id`(`user_id`) USING BTREE,
  CONSTRAINT `b_address_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '地址表';

-- ----------------------------
-- 7. 广告表 (b_ad)
-- ----------------------------
DROP TABLE IF EXISTS `b_ad`;
CREATE TABLE `b_ad` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '广告ID',
  `image`       VARCHAR(200) NULL DEFAULT NULL     COMMENT '广告图片',
  `link`        VARCHAR(500) NULL DEFAULT NULL     COMMENT '广告链接',
  `create_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '广告表';

-- ----------------------------
-- 8. 轮播图表 (b_banner)
-- ----------------------------
DROP TABLE IF EXISTS `b_banner`;
CREATE TABLE `b_banner` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `image`       VARCHAR(200) NULL DEFAULT NULL     COMMENT '轮播图图片',
  `create_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '创建时间',
  `thing_id`    BIGINT(20)   NULL DEFAULT NULL     COMMENT '商品ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `b_banner_thing_id_3f307d00_fk_b_thing_id`(`thing_id`) USING BTREE,
  CONSTRAINT `b_banner_thing_id_3f307d00_fk_b_thing_id` FOREIGN KEY (`thing_id`) REFERENCES `b_thing` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '轮播图表';

-- ----------------------------
-- 9. 评论表 (b_comment)
-- ----------------------------
DROP TABLE IF EXISTS `b_comment`;
CREATE TABLE `b_comment` (
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `content`      VARCHAR(500) NULL DEFAULT NULL     COMMENT '评论内容',
  `comment_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '评论时间',
  `like_count`   INT(11)      NOT NULL DEFAULT 0    COMMENT '点赞数',
  `user_id`      BIGINT(20)   NULL DEFAULT NULL     COMMENT '用户ID',
  `thing_id`     BIGINT(20)   NULL DEFAULT NULL     COMMENT '商品ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `b_comment_user_id_46f0670f_fk_b_user_id`(`user_id`) USING BTREE,
  INDEX `b_comment_thing_id_57ab492b_fk_b_thing_id`(`thing_id`) USING BTREE,
  CONSTRAINT `b_comment_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `b_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `b_comment_thing_id_57ab492b_fk_b_thing_id` FOREIGN KEY (`thing_id`) REFERENCES `b_thing` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '评论表';

-- ----------------------------
-- 10. 订单表 (b_order)
-- ----------------------------
DROP TABLE IF EXISTS `b_order`;
CREATE TABLE `b_order` (
  `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `status`           VARCHAR(20)  NULL DEFAULT NULL     COMMENT '订单状态',
  `order_time`       VARCHAR(30)  NULL DEFAULT NULL     COMMENT '下单时间',
  `pay_time`         VARCHAR(30)  NULL DEFAULT NULL     COMMENT '支付时间',
  `thing_id`         BIGINT(20)   NULL DEFAULT NULL     COMMENT '商品ID',
  `user_id`          BIGINT(20)   NULL DEFAULT NULL     COMMENT '用户ID',
  `count`            INT(11)      NULL DEFAULT NULL     COMMENT '数量',
  `order_number`     VARCHAR(50)  NULL DEFAULT NULL     COMMENT '订单编号',
  `receiver_address` VARCHAR(500) NULL DEFAULT NULL     COMMENT '收货地址',
  `receiver_name`    VARCHAR(100) NULL DEFAULT NULL     COMMENT '收货人姓名',
  `receiver_phone`   VARCHAR(30)  NULL DEFAULT NULL     COMMENT '收货人电话',
  `remark`           VARCHAR(500) NULL DEFAULT NULL     COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '订单表';

-- ----------------------------
-- 11. 商品收藏表 (b_thing_collect)
-- ----------------------------
DROP TABLE IF EXISTS `b_thing_collect`;
CREATE TABLE `b_thing_collect` (
  `id`       BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `thing_id` BIGINT(20) NULL DEFAULT NULL     COMMENT '商品ID',
  `user_id`  BIGINT(20) NULL DEFAULT NULL     COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '商品收藏表';

-- ----------------------------
-- 12. 商品心愿表 (b_thing_wish)
-- ----------------------------
DROP TABLE IF EXISTS `b_thing_wish`;
CREATE TABLE `b_thing_wish` (
  `id`       BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `thing_id` BIGINT(20) NULL DEFAULT NULL     COMMENT '商品ID',
  `user_id`  BIGINT(20) NULL DEFAULT NULL     COMMENT '用户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '商品心愿表';

-- ----------------------------
-- 13. 错误日志表 (b_error_log)
-- ----------------------------
DROP TABLE IF EXISTS `b_error_log`;
CREATE TABLE `b_error_log` (
  `id`       BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `ip`       VARCHAR(100) NULL DEFAULT NULL     COMMENT '请求IP',
  `url`      VARCHAR(500) NULL DEFAULT NULL     COMMENT '请求URL',
  `method`   VARCHAR(10)  NULL DEFAULT NULL     COMMENT '请求方式',
  `content`  TEXT         NULL                   COMMENT '错误内容',
  `log_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '日志时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '错误日志表';

-- ----------------------------
-- 14. 操作日志表 (b_op_log)
-- ----------------------------
DROP TABLE IF EXISTS `b_op_log`;
CREATE TABLE `b_op_log` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `re_ip`       VARCHAR(100) NULL DEFAULT NULL     COMMENT '请求IP',
  `re_time`     VARCHAR(30)  NULL DEFAULT NULL     COMMENT '请求时间',
  `re_ua`       VARCHAR(500) NULL DEFAULT NULL     COMMENT 'User-Agent',
  `re_url`      VARCHAR(500) NULL DEFAULT NULL     COMMENT '请求URL',
  `re_method`   VARCHAR(10)  NULL DEFAULT NULL     COMMENT '请求方式',
  `re_content`  TEXT         NULL                   COMMENT '请求内容',
  `access_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '访问时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '操作日志表';

-- ----------------------------
-- 15. 公告表 (b_notice)
-- ----------------------------
DROP TABLE IF EXISTS `b_notice`;
CREATE TABLE `b_notice` (
  `id`          BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title`       VARCHAR(200) NULL DEFAULT NULL     COMMENT '公告标题',
  `content`     VARCHAR(500) NULL DEFAULT NULL     COMMENT '公告内容',
  `create_time` VARCHAR(30)  NULL DEFAULT NULL     COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic COMMENT = '公告表';

SET FOREIGN_KEY_CHECKS = 1;
