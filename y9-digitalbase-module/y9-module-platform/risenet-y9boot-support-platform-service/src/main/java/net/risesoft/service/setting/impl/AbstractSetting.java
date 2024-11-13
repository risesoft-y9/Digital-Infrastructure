package net.risesoft.service.setting.impl;

/**
 * 设置基类
 *
 * @author shidaobang
 * @date 2024/11/13
 * @since 9.6.8
 */
public abstract class AbstractSetting {

    /**
     * 前缀，用于查找配置文件的配置
     *
     * @return {@code String }
     */
    public abstract String getPrefix();

}
