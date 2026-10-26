package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密配置
 *
 * <p>系统采用 BCrypt 自适应单向散列算法替代早期的“固定盐 + MD5”方案，原因与效果如下：</p>
 * <ol>
 *     <li>BCrypt 每次加密都会自动生成 16 字节随机盐，并将盐与成本因子内嵌于密文
 *         （形如 $2a$10$...），无需在数据库中额外维护盐字段，天然抵御彩虹表攻击；</li>
 *     <li>通过 strength（成本因子）控制 2^strength 轮迭代，可随硬件性能提升持续调高，
 *         使离线暴力破解的成本随之上升，克服 MD5 计算过快、易被 GPU 批量爆破的缺陷；</li>
 *     <li>校验统一使用 {@code matches()} 做恒定时间比较，避免字符串 equals 带来的时序侧信道风险。</li>
 * </ol>
 *
 * @author wanchun
 */
@Configuration
public class PasswordConfig {

    /**
     * 成本因子：2^10 轮迭代，普通服务器单次校验耗时约 60~100ms，兼顾安全性与登录体验。
     */
    private static final int BCRYPT_STRENGTH = 10;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }
}
