-- ============================================================================
-- 密码存储安全改造：固定盐 + MD5  ->  BCrypt 自适应单向散列
-- 适用库：java_pet / java_pet_2        执行前请先备份数据库
-- 配套代码：com.example.config.PasswordConfig
--           com.example.service.impl.UserServiceImpl
-- ============================================================================

-- 0. 前置检查（若审计字段改造尚未落到数据库，请先执行，否则 insert/update 会报未知列）
--    当前 b_user 表仅有 create_time(varchar)，缺少 create_by / create_by_name / update_by /
--    update_by_name / update_time，而实体已通过 BaseEntity + MyMetaObjectHandler 自动填充：
-- ALTER TABLE `b_user`
--     ADD COLUMN `create_by`      bigint(20)   NULL DEFAULT NULL COMMENT '创建人ID',
--     ADD COLUMN `create_by_name` varchar(50)  NULL DEFAULT NULL COMMENT '创建人名称',
--     ADD COLUMN `update_by`      bigint(20)   NULL DEFAULT NULL COMMENT '修改人ID',
--     ADD COLUMN `update_by_name` varchar(50)  NULL DEFAULT NULL COMMENT '修改人名称',
--     ADD COLUMN `update_time`    datetime     NULL DEFAULT NULL COMMENT '修改时间';
-- ALTER TABLE `b_user` MODIFY COLUMN `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间';

-- 1. 密码字段扩容（必须执行）
--    BCrypt 密文固定 60 字符（形如 $2a$10$xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx），
--    原字段 varchar(50) 会被截断，导致改造后所有账号无法登录。
ALTER TABLE `b_user`
    MODIFY COLUMN `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录密码（BCrypt 密文）';

-- 2. 改造前后对比（执行后查看）
--    pwd_len = 32 表示仍是旧的加盐 MD5 密文，= 60 表示已升级为 BCrypt
SELECT id,
       username,
       LENGTH(`password`)                                  AS pwd_len,
       CASE WHEN `password` LIKE '$2%' THEN 'BCrypt' ELSE 'MD5(待迁移)' END AS algo
FROM `b_user`
ORDER BY id;

-- 3. 存量数据处理（二选一）
--    A. 平滑迁移（推荐，代码已内置）：
--       旧账号下次登录时，若通过校验且库中仍为 MD5 密文，系统会自动用本次明文
--       重新生成 BCrypt 密文写回，账号无需重置、用户无感知。
--
--    B. 强制重置（若要求所有旧密码立即失效）：
--       先用程序（BCryptPasswordEncoder.encode）生成初始密码密文，再整体更新：
--       UPDATE `b_user` SET `password` = '<此处填入 BCrypt 密文>';

-- 4. 回滚方案（如需回退）
-- ALTER TABLE `b_user`
--     MODIFY COLUMN `password` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '登录密码';
