package net.risesoft.service.dictionary;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.dictionary.OptionClass;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9OptionClassService {

    /**
     * 根据type删除字典类型，以及该类型对应的数据列表
     *
     * @param type 类型
     */
    void deleteByType(String type);

    /**
     * 按类型查找
     *
     * @param type 类型
     * @return {@code Optional<OptionClass> }
     */
    Optional<OptionClass> findByType(String type);

    /**
     * 字典类型表中是否有数据
     *
     * @return boolean
     */
    boolean hasData();

    /**
     * 字曲典类型列表
     *
     * @return {@code List<OptionClass>}
     */
    List<OptionClass> list();

    /**
     * 保存新增字典类型
     *
     * @param optionClass 字典类型对象
     * @return {@link OptionClass}
     */
    OptionClass saveOptionClass(OptionClass optionClass);

}
