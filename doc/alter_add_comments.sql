-- ============================================================
-- 补全 example_pet 库中所有缺失的字段 COMMENT 及表 COMMENT
-- 基于实体类及前端代码业务含义推断    执行前请先备份数据库！
-- ============================================================

-- ----------------------------
-- 1. ad 广告表
-- ----------------------------
ALTER TABLE `ad` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `ad` MODIFY COLUMN `image` varchar(100) DEFAULT NULL COMMENT '广告图片地址';
ALTER TABLE `ad` MODIFY COLUMN `link` varchar(500) DEFAULT NULL COMMENT '广告跳转链接';
ALTER TABLE `ad` COMMENT = '广告表';

-- ----------------------------
-- 2. address 收货地址表
-- ----------------------------
ALTER TABLE `address` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `address` MODIFY COLUMN `name` varchar(100) DEFAULT NULL COMMENT '收货人姓名';
ALTER TABLE `address` MODIFY COLUMN `mobile` varchar(30) DEFAULT NULL COMMENT '收货人联系电话';
ALTER TABLE `address` MODIFY COLUMN `description` varchar(200) DEFAULT NULL COMMENT '详细地址描述';
ALTER TABLE `address` MODIFY COLUMN `def` varchar(10) DEFAULT NULL COMMENT '是否默认地址（是/否）';
ALTER TABLE `address` MODIFY COLUMN `user_id` bigint(20) DEFAULT NULL COMMENT '所属用户编号';
ALTER TABLE `address` COMMENT = '收货地址表';

-- ----------------------------
-- 3. banner 首页轮播图表
-- ----------------------------
ALTER TABLE `banner` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `banner` MODIFY COLUMN `image` varchar(100) DEFAULT NULL COMMENT '轮播图片地址';
ALTER TABLE `banner` MODIFY COLUMN `thing_id` bigint(20) DEFAULT NULL COMMENT '点击轮播后跳转的商品编号';
ALTER TABLE `banner` COMMENT = '首页轮播图表';

-- ----------------------------
-- 4. classification 商品分类表
-- ----------------------------
ALTER TABLE `classification` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `classification` MODIFY COLUMN `title` varchar(100) DEFAULT NULL COMMENT '分类名称（如：猫、狗、小宠等）';
ALTER TABLE `classification` COMMENT = '商品分类表';

-- ----------------------------
-- 5. comment 评论表
-- ----------------------------
ALTER TABLE `comment` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `comment` MODIFY COLUMN `content` varchar(200) DEFAULT NULL COMMENT '评论正文内容';
ALTER TABLE `comment` MODIFY COLUMN `like_count` int(11) NOT NULL DEFAULT '0' COMMENT '点赞次数';
ALTER TABLE `comment` MODIFY COLUMN `user_id` bigint(20) DEFAULT NULL COMMENT '发表评论的用户编号';
ALTER TABLE `comment` MODIFY COLUMN `thing_id` bigint(20) DEFAULT NULL COMMENT '被评论的商品编号';
ALTER TABLE `comment` COMMENT = '商品评论表';

-- ----------------------------
-- 6. error_log 错误日志表
-- ----------------------------
ALTER TABLE `error_log` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `error_log` MODIFY COLUMN `ip` varchar(100) DEFAULT NULL COMMENT '触发错误的客户端IP';
ALTER TABLE `error_log` MODIFY COLUMN `url` varchar(200) DEFAULT NULL COMMENT '触发错误的请求URL';
ALTER TABLE `error_log` MODIFY COLUMN `method` varchar(10) DEFAULT NULL COMMENT '请求方法（GET/POST等）';
ALTER TABLE `error_log` MODIFY COLUMN `content` text COMMENT '异常堆栈及错误详情';
ALTER TABLE `error_log` COMMENT = '错误日志表';

-- ----------------------------
-- 7. notice 公告表
-- ----------------------------
ALTER TABLE `notice` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `notice` MODIFY COLUMN `title` varchar(100) DEFAULT NULL COMMENT '公告标题';
ALTER TABLE `notice` MODIFY COLUMN `content` varchar(1000) DEFAULT NULL COMMENT '公告正文内容';
ALTER TABLE `notice` COMMENT = '系统公告表';

-- ----------------------------
-- 8. op_log 操作日志表
-- ----------------------------
ALTER TABLE `op_log` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `op_log` MODIFY COLUMN `re_ip` varchar(100) DEFAULT NULL COMMENT '请求来源IP';
ALTER TABLE `op_log` MODIFY COLUMN `re_ua` varchar(255) DEFAULT NULL COMMENT '浏览器User-Agent';
ALTER TABLE `op_log` MODIFY COLUMN `re_url` varchar(200) DEFAULT NULL COMMENT '请求URL路径';
ALTER TABLE `op_log` MODIFY COLUMN `re_method` varchar(10) DEFAULT NULL COMMENT 'HTTP请求方法（GET/POST等）';
ALTER TABLE `op_log` MODIFY COLUMN `re_content` varchar(200) DEFAULT NULL COMMENT '请求参数内容摘要';
ALTER TABLE `op_log` COMMENT = '操作日志表（含登录日志，通过re_url=/api/user/userLogin过滤）';

-- ----------------------------
-- 9. order 订单表
-- ----------------------------
ALTER TABLE `order` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `order` MODIFY COLUMN `status` varchar(2) DEFAULT NULL COMMENT '订单状态：1-待发货/待支付，2-已发货/已支付，3-已完成，7-已取消';
ALTER TABLE `order` MODIFY COLUMN `thing_id` bigint(20) DEFAULT NULL COMMENT '下单商品编号';
ALTER TABLE `order` MODIFY COLUMN `user_id` bigint(20) DEFAULT NULL COMMENT '下单用户编号';
ALTER TABLE `order` MODIFY COLUMN `count` int(11) NOT NULL DEFAULT '0' COMMENT '购买数量';
ALTER TABLE `order` MODIFY COLUMN `receiver_address` varchar(50) DEFAULT NULL COMMENT '收货详细地址';
ALTER TABLE `order` MODIFY COLUMN `receiver_name` varchar(20) DEFAULT NULL COMMENT '收货人姓名';
ALTER TABLE `order` MODIFY COLUMN `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人联系电话';
ALTER TABLE `order` MODIFY COLUMN `remark` varchar(30) DEFAULT NULL COMMENT '买家下单备注';
ALTER TABLE `order` COMMENT = '订单表';

-- ----------------------------
-- 10. tag 标签表
-- ----------------------------
ALTER TABLE `tag` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `tag` MODIFY COLUMN `title` varchar(100) DEFAULT NULL COMMENT '标签名称（如：温顺、活泼、训练有素等）';
ALTER TABLE `tag` COMMENT = '商品标签字典表';

-- ----------------------------
-- 11. thing 商品表（宠物商品）
-- ----------------------------
ALTER TABLE `thing` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `thing` MODIFY COLUMN `title` varchar(100) DEFAULT NULL COMMENT '商品标题（展示名称）';
ALTER TABLE `thing` MODIFY COLUMN `nickname` varchar(20) DEFAULT NULL COMMENT '宠物昵称（待领养宠物名称）';
ALTER TABLE `thing` MODIFY COLUMN `cover` varchar(100) DEFAULT NULL COMMENT '商品主图/封面图地址';
ALTER TABLE `thing` MODIFY COLUMN `description` longtext COMMENT '商品详情描述（富文本）';
ALTER TABLE `thing` MODIFY COLUMN `price` decimal(10,2) DEFAULT NULL COMMENT '商品价格（单位：元）';
ALTER TABLE `thing` MODIFY COLUMN `sex` varchar(10) DEFAULT NULL COMMENT '宠物性别（公/母）';
ALTER TABLE `thing` MODIFY COLUMN `address` varchar(30) DEFAULT NULL COMMENT '宠物所在地';
ALTER TABLE `thing` MODIFY COLUMN `status` varchar(1) NOT NULL COMMENT '商品上下架状态：0-下架，1-上架';
ALTER TABLE `thing` MODIFY COLUMN `score` int(11) DEFAULT '0' COMMENT '商品综合评分';
ALTER TABLE `thing` MODIFY COLUMN `pv` int(11) DEFAULT '0' COMMENT '商品浏览量（页面访问量）';
ALTER TABLE `thing` MODIFY COLUMN `recommend_count` int(11) DEFAULT '0' COMMENT '推荐次数';
ALTER TABLE `thing` MODIFY COLUMN `wish_count` int(11) DEFAULT '0' COMMENT '想要人数（加入心愿单次数）';
ALTER TABLE `thing` MODIFY COLUMN `collect_count` int(11) DEFAULT '0' COMMENT '收藏人数（加入收藏次数）';
ALTER TABLE `thing` MODIFY COLUMN `classification_id` bigint(20) DEFAULT NULL COMMENT '所属分类编号（关联classification表）';
ALTER TABLE `thing` COMMENT = '商品表（宠物商品/待领养宠物）';

-- ----------------------------
-- 12. thing_collect 商品收藏表
-- ----------------------------
ALTER TABLE `thing_collect` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `thing_collect` MODIFY COLUMN `thing_id` bigint(20) NOT NULL COMMENT '被收藏的商品编号';
ALTER TABLE `thing_collect` MODIFY COLUMN `user_id` bigint(20) NOT NULL COMMENT '收藏该商品的用户编号';
ALTER TABLE `thing_collect` COMMENT = '商品收藏记录表（用户与商品多对多）';

-- ----------------------------
-- 13. thing_tag 商品标签关联表
-- ----------------------------
ALTER TABLE `thing_tag` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `thing_tag` MODIFY COLUMN `thing_id` bigint(20) NOT NULL COMMENT '商品编号';
ALTER TABLE `thing_tag` MODIFY COLUMN `tag_id` bigint(20) NOT NULL COMMENT '标签编号';
ALTER TABLE `thing_tag` COMMENT = '商品-标签关联表（一个商品可打多个标签）';

-- ----------------------------
-- 14. thing_wish 商品心愿表
-- ----------------------------
ALTER TABLE `thing_wish` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `thing_wish` MODIFY COLUMN `thing_id` bigint(20) NOT NULL COMMENT '心愿商品编号';
ALTER TABLE `thing_wish` MODIFY COLUMN `user_id` bigint(20) NOT NULL COMMENT '想要该商品的用户编号';
ALTER TABLE `thing_wish` COMMENT = '商品心愿单记录表（用户想要领养/购买意向）';

-- ----------------------------
-- 15. user 用户表
-- ----------------------------
ALTER TABLE `user` MODIFY COLUMN `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键标识';
ALTER TABLE `user` MODIFY COLUMN `username` varchar(50) DEFAULT NULL COMMENT '登录用户名（唯一）';
ALTER TABLE `user` MODIFY COLUMN `role` varchar(2) DEFAULT NULL COMMENT '用户角色：1-普通用户，2-演示用户，3-管理员';
ALTER TABLE `user` MODIFY COLUMN `status` varchar(1) NOT NULL COMMENT '账户状态：0-正常，1-封号';
ALTER TABLE `user` MODIFY COLUMN `nickname` varchar(20) DEFAULT NULL COMMENT '用户昵称（页面展示名）';
ALTER TABLE `user` MODIFY COLUMN `avatar` varchar(100) DEFAULT NULL COMMENT '用户头像图片地址';
ALTER TABLE `user` MODIFY COLUMN `mobile` varchar(13) DEFAULT NULL COMMENT '绑定手机号';
ALTER TABLE `user` MODIFY COLUMN `email` varchar(50) DEFAULT NULL COMMENT '绑定邮箱';
ALTER TABLE `user` MODIFY COLUMN `gender` varchar(1) DEFAULT NULL COMMENT '性别（男/女）';
ALTER TABLE `user` MODIFY COLUMN `description` longtext COMMENT '个人简介/个性签名';
ALTER TABLE `user` MODIFY COLUMN `score` int(11) DEFAULT '0' COMMENT '用户积分';
ALTER TABLE `user` MODIFY COLUMN `push_email` varchar(40) DEFAULT NULL COMMENT '消息推送接收邮箱';
ALTER TABLE `user` MODIFY COLUMN `push_switch` tinyint(1) DEFAULT '0' COMMENT '消息推送开关：0-关闭，1-开启';
ALTER TABLE `user` MODIFY COLUMN `token` varchar(32) DEFAULT NULL COMMENT '登录会话令牌（32位UUID）';
ALTER TABLE `user` COMMENT = '用户表（含普通用户和管理员）';
