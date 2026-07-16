package net.risesoft.manager.dictionary;

import net.risesoft.entity.dictionary.Y9OptionValue;

/**
 * 字典数据 Manager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9OptionValueManager {

    /**
     * 创建字典数据
     *
     * @param code 字典数据代码
     * @param name 字典数据名称
     * @param type 字典类型
     * @return {@link Y9OptionValue}
     */
    Y9OptionValue create(String code, String name, String type);

    /**
     * 根据字典类型删除字典数据
     *
     * @param type 字典类型
     */
    void deleteByType(String type);

    /**
     * 获取指定字典类型下最大的排序号
     *
     * @param type 字典类型
     * @return 最大排序号
     */
    Integer getMaxTabIndexByType(String type);

}
