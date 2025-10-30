package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9PersonExt;

/**
 * 人员扩展管理类
 *
 * @author shidaobang
 * @date 2022/10/19
 */
public interface Y9PersonExtManager {

    /**
     * 删除人员扩展信息
     *
     * @param personId personId
     */
    void deleteByPersonId(String personId);

    /**
     * 根据人员id，获取人员扩展信息
     *
     * @param personId 人员id
     * @return {@code  Optional<Y9PersonExt>}
     */
    Optional<Y9PersonExt> findByPersonId(String personId);

    /**
     * 根据idType和idNum查询
     *
     * @param idType 证件类型
     * @param idNum 证件号
     * @return {@code List<Y9PersonExt>}
     */
    List<Y9PersonExt> listByIdTypeAndIdNum(String idType, String idNum);

    /**
     * 保存人员扩展信息
     *
     * @param personExt 扩展信息详情
     * @param person 人员信息详情
     * @return {@link Y9PersonExt}
     */
    Y9PersonExt saveOrUpdate(Y9PersonExt personExt, Y9Person person);
}
