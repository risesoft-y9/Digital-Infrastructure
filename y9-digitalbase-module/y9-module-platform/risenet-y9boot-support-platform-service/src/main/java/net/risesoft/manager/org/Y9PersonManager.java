package net.risesoft.manager.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.entity.org.Y9Person;
import net.risesoft.entity.org.Y9PersonExt;
import net.risesoft.y9.exception.Y9NotFoundException;

public interface Y9PersonManager {

    /**
     * 删除人员
     *
     * @param y9Person 人员信息
     */
    void delete(Y9Person y9Person);

    /**
     * 根据id获取人员信息
     *
     * @param id 人员id
     * @return {@code Optional<Y9Person>}
     */
    Optional<Y9Person> findByIdFromCache(String id);

    /**
     * 根据id，获取人员信息（直接读取数据库）
     *
     * @param id 人员id
     * @return {@code Optional<Y9Person>}
     */
    Optional<Y9Person> findById(String id);

    /**
     * 根据主键id获取人员实例
     *
     * @param id 唯一标识
     * @return {@link Y9Person } 人员对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Person getByIdFromCache(String id);

    /**
     * 根据id获取人员信息（直接读取数据库）
     *
     * @param id 人员id
     * @return {@link Y9Person}
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Y9Person getById(String id);

    /**
     * 根据用户组id获取人员列表
     *
     * @param groupId 用户组id
     * @param disabled 是否禁用
     * @return {@code List<Y9Person>}
     * @throws Y9NotFoundException 用户组关联的人员不存在的情况
     */
    List<Y9Person> listByGroupId(String groupId, Boolean disabled);

    /**
     * 根据父节点id获取人员列表
     *
     * @param parentId 父节点id
     * @param disabled 是否禁用
     * @return {@code List<Y9Person>}
     */
    List<Y9Person> listByParentId(String parentId, Boolean disabled);

    /**
     * 根据岗位id获取人员列表
     *
     * @param positionId 岗位id
     * @param disabled 是否禁用
     * @return {@code List<Y9Person>}
     * @throws Y9NotFoundException 岗位关联的人员不存在的情况
     */
    List<Y9Person> listByPositionId(String positionId, Boolean disabled);

    /**
     * 新增人员
     *
     * @param person 人员信息
     * @return {@link Y9Person}
     */
    Y9Person insert(Y9Person person);

    /**
     * 更新人员
     *
     * @param person 人员信息
     * @param originalPerson 原人员信息
     * @return {@link Y9Person}
     */
    Y9Person update(Y9Person person, Y9Person originalPerson);

    /**
     * 根据原人员id更新人员及其扩展信息
     *
     * @param originalPerson 原人员信息
     * @param originalExt 原人员扩展信息
     * @throws Y9NotFoundException 人员所在的父组织节点不存在的情况
     */
    void updatePersonByOriginalId(Y9Person originalPerson, Y9PersonExt originalExt);
}
