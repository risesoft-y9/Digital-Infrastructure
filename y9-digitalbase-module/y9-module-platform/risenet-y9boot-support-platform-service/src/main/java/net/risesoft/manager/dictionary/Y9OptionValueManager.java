package net.risesoft.manager.dictionary;

import net.risesoft.entity.Y9OptionValue;

/**
 * 字典数据 Manager
 *
 * @author shidaobang
 * @date 2023/06/13
 * @since 9.6.2
 */
public interface Y9OptionValueManager {

    Y9OptionValue create(String code, String name, String type);

    void deleteByType(String type);

    Integer getMaxTabIndexByType(String type);

}
