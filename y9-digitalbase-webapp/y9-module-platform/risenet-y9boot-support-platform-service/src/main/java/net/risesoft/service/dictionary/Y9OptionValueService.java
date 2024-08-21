package net.risesoft.service.dictionary;

import java.util.List;

import net.risesoft.entity.Y9OptionValue;

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
     * @return {@link Y9OptionValue}
     */
    Y9OptionValue create(String code, String name, String type);

    /**
     * 根据id数组，删除字典数据
     *
     * @param ids id数组
     */
    void delete(String[] ids);

    /**
     * 根据type，删除字典数据
     *
     * @param type 字典类型
     */
    void deleteByType(String type);

    /**
     * 根据字典类型，获取字典属性值列表
     *
     * @param type 字典类型
     * @return {@code List<Y9OptionValue>}
     */
    List<Y9OptionValue> listByType(String type);

    /**
     * 保存新增字典数据
     *
     * @param optionValue 字典数据对象
     * @return {@link Y9OptionValue}
     */
    Y9OptionValue saveOptionValue(Y9OptionValue optionValue);
}
