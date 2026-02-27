package net.risesoft.y9.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import cn.hutool.core.util.StrUtil;

/**
 * 字符串工具类
 *
 * @author shidaobang
 * @date 2024/05/27
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Y9StringUtil {

    /**
     * 格式化文本, {} 表示占位符
     *
     * {@link cn.hutool.core.util.StrUtil#format(CharSequence, Object...)}
     * 
     * @param template 模板
     * @param params params 参数
     * @return {@code String } 格式化后的文本
     */
    public static String format(String template, Object... params) {
        return StrUtil.format(template, params);
    }

}
