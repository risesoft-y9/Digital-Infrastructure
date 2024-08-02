package net.risesoft.y9.util;

import cn.hutool.core.util.StrUtil;

/**
 * Y9StringUtil
 *
 * @author shidaobang
 * @date 2024/05/27
 */
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
