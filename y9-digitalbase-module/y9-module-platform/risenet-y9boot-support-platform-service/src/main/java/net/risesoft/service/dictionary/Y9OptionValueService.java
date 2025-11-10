package net.risesoft.service.dictionary;

import java.util.List;

import net.risesoft.model.platform.dictionary.OptionValue;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9OptionValueService {

    /**
     * 创建
     *
     * @param code 代码
     * @param name 名字
     * @param type 字典类型
     * @return {@link OptionValue}
     */
    OptionValue create(String code, String name, String type);

    /**
     * 根据id数组，删除字典数据
     *
     * @param ids id数组
     */
    void delete(String[] ids);

    /**
     * 根据字典类型，获取字典属性值列表
     *
     * @param type 字典类型
     * @return {@code List<Y9OptionValue>}
     */
    List<OptionValue> listByType(String type);

    /**
     * 保存新增字典数据
     *
     * @param optionValue 字典数据对象
     * @return {@link OptionValue}
     */
    OptionValue saveOptionValue(OptionValue optionValue);
}
