-- ============================================================
-- 数据库迁移脚本（幂等版）：添加审计字段 + 修正不合理字段类型
-- 可重复执行，不会因列已存在而报错
-- 执行前请备份数据库
-- ============================================================

DROP PROCEDURE IF EXISTS add_column_if_not_exists;
DROP PROCEDURE IF EXISTS modify_column_if_exists;
DROP PROCEDURE IF EXISTS convert_ms_timestamp;

-- ============================================================
-- 辅助存储过程1：如果列不存在则添加
-- ============================================================
DELIMITER $$
CREATE PROCEDURE add_column_if_not_exists(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_column_definition VARCHAR(200)
)
BEGIN
    DECLARE v_column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO v_column_exists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;
    IF v_column_exists = 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` ADD COLUMN `', p_column_name, '` ', p_column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- ============================================================
-- 辅助存储过程2：如果列存在则修改类型
-- ============================================================
DELIMITER $$
CREATE PROCEDURE modify_column_if_exists(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64),
    IN p_column_definition VARCHAR(200)
)
BEGIN
    DECLARE v_column_exists INT DEFAULT 0;
    SELECT COUNT(*) INTO v_column_exists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;
    IF v_column_exists > 0 THEN
        SET @sql = CONCAT('ALTER TABLE `', p_table_name, '` MODIFY COLUMN `', p_column_name, '` ', p_column_definition);
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- ============================================================
-- 辅助存储过程3：将 varchar 毫秒时间戳转换为 datetime
-- 仅处理纯数字（毫秒时间戳）的值，忽略已是日期格式的值
-- 自动处理 NOT NULL 列：无效值设为 '1970-01-01' 而非 NULL
-- ============================================================
DELIMITER $$
CREATE PROCEDURE convert_ms_timestamp(
    IN p_table_name VARCHAR(64),
    IN p_column_name VARCHAR(64)
)
BEGIN
    DECLARE v_column_exists INT DEFAULT 0;
    DECLARE v_is_datetime INT DEFAULT 0;
    DECLARE v_is_nullable INT DEFAULT 0;

    -- 检查列是否存在
    SELECT COUNT(*) INTO v_column_exists
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = p_table_name
      AND COLUMN_NAME = p_column_name;

    IF v_column_exists > 0 THEN
        -- 检查列当前数据类型是否为 datetime
        SELECT COUNT(*) INTO v_is_datetime
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND COLUMN_NAME = p_column_name
          AND DATA_TYPE = 'datetime';

        -- 检查列是否允许 NULL
        SELECT COUNT(*) INTO v_is_nullable
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = p_table_name
          AND COLUMN_NAME = p_column_name
          AND IS_NULLABLE = 'YES';

        -- 只有当列还是 varchar 类型时才执行转换
        IF v_is_datetime = 0 THEN
            -- 处理无效的纯数字值（>13位无法转换的时间戳）
            IF v_is_nullable > 0 THEN
                -- 可空列：设为 NULL
                SET @sql = CONCAT(
                    'UPDATE `', p_table_name, '` SET `', p_column_name, '` = NULL ',
                    'WHERE `', p_column_name, '` IS NOT NULL ',
                    'AND `', p_column_name, '` REGEXP ''^[0-9]+$'' ',
                    'AND LENGTH(`', p_column_name, '`) > 13'
                );
            ELSE
                -- 非空列：设为默认值
                SET @sql = CONCAT(
                    'UPDATE `', p_table_name, '` SET `', p_column_name, '` = ''1970-01-01 00:00:00'' ',
                    'WHERE `', p_column_name, '` IS NOT NULL ',
                    'AND `', p_column_name, '` REGEXP ''^[0-9]+$'' ',
                    'AND LENGTH(`', p_column_name, '`) > 13'
                );
            END IF;
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;

            -- 将有效的毫秒时间戳（10-13位数字）转为 datetime
            SET @sql = CONCAT(
                'UPDATE `', p_table_name, '` SET `', p_column_name, '` = FROM_UNIXTIME(CAST(`', p_column_name, '` AS UNSIGNED) / 1000) ',
                'WHERE `', p_column_name, '` IS NOT NULL ',
                'AND `', p_column_name, '` REGEXP ''^[0-9]{10,13}$'''
            );
            PREPARE stmt FROM @sql;
            EXECUTE stmt;
            DEALLOCATE PREPARE stmt;
        END IF;
    END IF;
END$$
DELIMITER ;


-- ============================================================
-- 第一部分：为所有表添加审计字段（幂等）
-- ============================================================

-- ------------------- adopt 表 -------------------
CALL add_column_if_not_exists('adopt', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('adopt', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('adopt', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('adopt', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('adopt', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('adopt', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- animal 表 -------------------
CALL add_column_if_not_exists('animal', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('animal', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('animal', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('animal', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('animal', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('animal', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_ad 表 -------------------
CALL add_column_if_not_exists('b_ad', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_ad', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_ad', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_ad', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_ad', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_address 表 -------------------
CALL add_column_if_not_exists('b_address', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_address', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_address', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_address', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_address', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_banner 表 -------------------
CALL add_column_if_not_exists('b_banner', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_banner', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_banner', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_banner', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_banner', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_classification 表 -------------------
CALL add_column_if_not_exists('b_classification', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_classification', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_classification', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_classification', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_classification', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_comment 表 -------------------
CALL add_column_if_not_exists('b_comment', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_comment', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_comment', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_comment', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_comment', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_comment', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_error_log 表 -------------------
CALL add_column_if_not_exists('b_error_log', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_error_log', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_error_log', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_error_log', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_error_log', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_error_log', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_login_log 表 -------------------
CALL add_column_if_not_exists('b_login_log', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_login_log', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_login_log', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_login_log', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_login_log', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_login_log', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_notice 表 -------------------
CALL add_column_if_not_exists('b_notice', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_notice', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_notice', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_notice', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_notice', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_op_log 表 -------------------
CALL add_column_if_not_exists('b_op_log', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_op_log', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_op_log', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_op_log', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_op_log', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_op_log', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_order 表 -------------------
CALL add_column_if_not_exists('b_order', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_order', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_order', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_order', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_order', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_order', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_tag 表 -------------------
CALL add_column_if_not_exists('b_tag', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_tag', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_tag', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_tag', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_tag', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_thing 表 -------------------
CALL add_column_if_not_exists('b_thing', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_thing', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_thing', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_thing', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_thing', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_thing_collect 表 -------------------
CALL add_column_if_not_exists('b_thing_collect', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_thing_collect', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_thing_collect', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_thing_collect', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_thing_collect', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_thing_collect', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_thing_tag 表 -------------------
CALL add_column_if_not_exists('b_thing_tag', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_thing_tag', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_thing_tag', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_thing_tag', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_thing_tag', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_thing_tag', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_thing_wish 表 -------------------
CALL add_column_if_not_exists('b_thing_wish', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_thing_wish', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_thing_wish', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('b_thing_wish', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_thing_wish', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_thing_wish', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- b_user 表 -------------------
CALL add_column_if_not_exists('b_user', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('b_user', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('b_user', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('b_user', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('b_user', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- foster 表 -------------------
CALL add_column_if_not_exists('foster', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('foster', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('foster', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('foster', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('foster', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('foster', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- goods 表 -------------------
CALL add_column_if_not_exists('goods', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('goods', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('goods', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('goods', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('goods', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('goods', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- room 表 -------------------
CALL add_column_if_not_exists('room', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('room', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('room', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('room', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('room', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('room', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");

-- ------------------- submit 表 -------------------
CALL add_column_if_not_exists('submit', 'create_by', "bigint(20) DEFAULT NULL COMMENT '创建人ID'");
CALL add_column_if_not_exists('submit', 'create_by_name', "varchar(50) DEFAULT NULL COMMENT '创建人名称'");
CALL add_column_if_not_exists('submit', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL add_column_if_not_exists('submit', 'update_by', "bigint(20) DEFAULT NULL COMMENT '修改人ID'");
CALL add_column_if_not_exists('submit', 'update_by_name', "varchar(50) DEFAULT NULL COMMENT '修改人名称'");
CALL add_column_if_not_exists('submit', 'update_time', "datetime DEFAULT NULL COMMENT '修改时间'");


-- ============================================================
-- 第二部分：将 varchar 毫秒时间戳数据转换为 datetime（必须在 MODIFY 之前执行）
-- ============================================================

-- 毫秒时间戳 -> datetime
CALL convert_ms_timestamp('b_ad', 'create_time');
CALL convert_ms_timestamp('b_address', 'create_time');
CALL convert_ms_timestamp('b_banner', 'create_time');
CALL convert_ms_timestamp('b_classification', 'create_time');
CALL convert_ms_timestamp('b_comment', 'comment_time');
CALL convert_ms_timestamp('b_notice', 'create_time');
CALL convert_ms_timestamp('b_order', 'order_time');
CALL convert_ms_timestamp('b_order', 'pay_time');
CALL convert_ms_timestamp('b_tag', 'create_time');
CALL convert_ms_timestamp('b_thing', 'create_time');
CALL convert_ms_timestamp('b_user', 'create_time');

-- b_error_log.log_time: 混合格式（部分为 '2023-03-11 14:13:50.128264'，部分为毫秒时间戳）
-- 先清空无法转换的纯数字超长值
UPDATE `b_error_log` SET `log_time` = NULL WHERE `log_time` IS NOT NULL AND `log_time` REGEXP '^[0-9]{14,}$';
-- 将毫秒时间戳转为 datetime
UPDATE `b_error_log` SET `log_time` = FROM_UNIXTIME(CAST(`log_time` AS UNSIGNED) / 1000) WHERE `log_time` IS NOT NULL AND `log_time` REGEXP '^[0-9]{10,13}$';

-- b_op_log.re_time: 混合格式（部分为 '2023-05-01 01:32:41'，部分为毫秒时间戳）
UPDATE `b_op_log` SET `re_time` = NULL WHERE `re_time` IS NOT NULL AND `re_time` REGEXP '^[0-9]{14,}$';
UPDATE `b_op_log` SET `re_time` = FROM_UNIXTIME(CAST(`re_time` AS UNSIGNED) / 1000) WHERE `re_time` IS NOT NULL AND `re_time` REGEXP '^[0-9]{10,13}$';

-- b_op_log.access_time: varchar -> int（直接取数字部分）
UPDATE `b_op_log` SET `access_time` = CAST(`access_time` AS UNSIGNED) WHERE `access_time` IS NOT NULL AND `access_time` REGEXP '^[0-9]+$';

-- submit.time: 日期字符串格式 '2026/9/18 15:51:07' -> datetime
UPDATE `submit` SET `time` = STR_TO_DATE(`time`, '%Y/%c/%e %H:%i:%s') WHERE `time` IS NOT NULL AND `time` REGEXP '^[0-9]{4}/';

-- foster.start_date: 已经是 '2026-09-18' 格式，可直接转
-- 但先确保没有脏数据
UPDATE `foster` SET `start_date` = NULL WHERE `start_date` IS NOT NULL AND `start_date` NOT REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}$';


-- ============================================================
-- 第三部分：修正已有字段类型（数据已转换完毕，可安全 MODIFY）
-- ============================================================

-- ------------------- 时间字段 varchar -> datetime -------------------
CALL modify_column_if_exists('b_ad', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_address', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_banner', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_classification', 'create_time', "datetime NOT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_comment', 'comment_time', "datetime DEFAULT NULL COMMENT '评论时间'");
CALL modify_column_if_exists('b_error_log', 'log_time', "datetime DEFAULT NULL COMMENT '日志时间'");
CALL modify_column_if_exists('b_notice', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_op_log', 're_time', "datetime DEFAULT NULL COMMENT '请求时间'");
CALL modify_column_if_exists('b_op_log', 'access_time', "int(11) DEFAULT NULL COMMENT '访问耗时(毫秒)'");
CALL modify_column_if_exists('b_order', 'order_time', "datetime DEFAULT NULL COMMENT '订单时间'");
CALL modify_column_if_exists('b_order', 'pay_time', "datetime DEFAULT NULL COMMENT '支付时间'");
CALL modify_column_if_exists('b_order', 'order_number', "varchar(20) DEFAULT NULL COMMENT '订单编号'");
CALL modify_column_if_exists('b_tag', 'create_time', "datetime NOT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_thing', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('b_user', 'create_time', "datetime DEFAULT NULL COMMENT '创建时间'");
CALL modify_column_if_exists('foster', 'start_date', "date DEFAULT NULL COMMENT '入住日期'");
CALL modify_column_if_exists('submit', 'time', "datetime DEFAULT NULL COMMENT '上报时间'");
CALL modify_column_if_exists('submit', 'status', "varchar(20) DEFAULT NULL COMMENT '处理状态'");
CALL modify_column_if_exists('room', 'status', "varchar(20) DEFAULT NULL COMMENT '房间状态'");

-- ------------------- 价格字段 -> decimal(10,2) -------------------
CALL modify_column_if_exists('b_thing', 'price', "decimal(10,2) DEFAULT NULL COMMENT '价格'");
CALL modify_column_if_exists('goods', 'price', "decimal(10,2) DEFAULT NULL COMMENT '商品单价'");

-- ------------------- 主键/外键字段 int -> bigint -------------------
CALL modify_column_if_exists('adopt', 'record_id', "bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识'");
CALL modify_column_if_exists('adopt', 'owner_uid', "bigint(20) DEFAULT NULL COMMENT '领养人编号'");
CALL modify_column_if_exists('adopt', 'pet_uid', "bigint(20) DEFAULT NULL COMMENT '宠物编号'");
CALL modify_column_if_exists('animal', 'pid', "bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识'");
CALL modify_column_if_exists('foster', 'order_id', "bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识'");
CALL modify_column_if_exists('foster', 'client_uid', "bigint(20) DEFAULT NULL COMMENT '委托人编号'");
CALL modify_column_if_exists('goods', 'id', "bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'");
CALL modify_column_if_exists('room', 'id', "bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'");
CALL modify_column_if_exists('submit', 'id', "bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID'");
CALL modify_column_if_exists('b_comment', 'user_id', "bigint(20) DEFAULT NULL");
CALL modify_column_if_exists('b_comment', 'thing_id', "bigint(20) DEFAULT NULL");
CALL modify_column_if_exists('b_order', 'thing_id', "bigint(20) DEFAULT NULL");
CALL modify_column_if_exists('b_order', 'user_id', "bigint(20) DEFAULT NULL");
CALL modify_column_if_exists('b_thing_collect', 'thing_id', "bigint(20) NOT NULL");
CALL modify_column_if_exists('b_thing_collect', 'user_id', "bigint(20) NOT NULL");
CALL modify_column_if_exists('b_thing_wish', 'thing_id', "bigint(20) NOT NULL");
CALL modify_column_if_exists('b_thing_wish', 'user_id', "bigint(20) NOT NULL");
CALL modify_column_if_exists('b_address', 'user_id', "bigint(20) DEFAULT NULL");

-- ============================================================
-- 清理辅助存储过程
-- ============================================================
DROP PROCEDURE IF EXISTS add_column_if_not_exists;
DROP PROCEDURE IF EXISTS modify_column_if_exists;
DROP PROCEDURE IF EXISTS convert_ms_timestamp;
