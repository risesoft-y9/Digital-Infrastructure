package net.risesoft.api.resource;

import java.util.List;

import net.risesoft.model.System;

/**
 * 系统管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface SystemApi {

    /**
     * 根据系统唯一标识获取系统
     *
     * @param id 系统名称
     * @return System 系统信息
     * @since 9.6.2
     */
    System getById(String id);

    /**
     * 根据系统名称获取系统
     *
     * @param name 系统名称
     * @return System 系统管理员
     */
    System getByName(String name);

    /**
     * 根据系统id，获取系统名称列表
     *
     * @param systemIds 系统id集合（List&lt;String&lt;）
     * @return List&lt;String&gt; 系统名称列表
     */
    List<String> listSystemNameByIds(List<String> systemIds);
}
