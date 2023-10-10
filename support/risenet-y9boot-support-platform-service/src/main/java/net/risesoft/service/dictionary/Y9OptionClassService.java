package net.risesoft.service.dictionary;

import java.util.List;

import net.risesoft.entity.Y9OptionClass;

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
     * 字典类型表中是否有数据
     *
     * @return boolean
     */
    boolean hasData();

    /**
     * 字曲典类型列表
     *
     * @return List<ORGOptionClass>
     */
    List<Y9OptionClass> list();

    /**
     * 保存新增字典类型
     *
     * @param optionClass 字典类型对象
     * @return {@link Y9OptionClass}
     */
    Y9OptionClass saveOptionClass(Y9OptionClass optionClass);

}
