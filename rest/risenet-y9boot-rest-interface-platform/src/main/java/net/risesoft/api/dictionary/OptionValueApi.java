package net.risesoft.api.dictionary;

import java.util.List;

import net.risesoft.model.OptionValue;

/**
 * 字典表管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface OptionValueApi {

    /**
     * 根据类型查找
     *
     * @param tenantId 租户id
     * @param type 类型
     * @return 字典数据列表
     * @since 9.6.0
     */
    List<OptionValue> listByType(String tenantId, String type);
}
