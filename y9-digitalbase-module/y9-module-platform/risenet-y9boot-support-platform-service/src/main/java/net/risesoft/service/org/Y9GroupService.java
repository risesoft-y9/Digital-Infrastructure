package net.risesoft.service.org;

import java.util.List;
import java.util.Optional;

import net.risesoft.model.platform.org.Group;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9GroupService {

    /**
     * 更改禁用状态
     *
     * @param id ID
     * @return 用户组
     */
    Group changeDisabled(String id);

    /**
     * 根据主键id移除用户组实例(并且移除组内的人员)
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据父节点id删除用户组实例(并且移除组内的人员)
     *
     * @param parentId 父节点id
     */
    void deleteByParentId(String parentId);

    /**
     * 根据id查找用户组
     *
     * @param id 唯一标识
     * @return {@code Group} 用户组对象 或 null
     */
    Optional<Group> findById(String id);

    /**
     * 根据主键id获取用户组实例
     *
     * @param id 唯一标识
     * @return Group 用户组对象
     * @throws Y9NotFoundException id 对应的记录不存在的情况
     */
    Group getById(String id);

    /**
     * 根据dn查询
     *
     * @param dn dn
     * @param disabled 是否包含禁用的用户组
     * @return {@code List<Group>}
     */
    List<Group> listByDn(String dn, Boolean disabled);

    /**
     * 根据名称查询
     *
     * @param name 用户组名称
     * @return {@code List<Group>}
     */
    List<Group> listByNameLike(String name);

    /**
     * 根据父节点id,获取本层级的用户组列表
     *
     * @param parentId 父节点id
     * @param disabled 是否包含禁用的用户组
     * @return {@code List<Group>}
     */
    List<Group> listByParentId(String parentId, Boolean disabled);

    /**
     * 根据人员 id获取用户组列表
     *
     * @param personId 人员id
     * @param disabled 是否包含禁用的用户组
     * @return {@code List<Group>}
     */
    List<Group> listByPersonId(String personId, Boolean disabled);

    Group move(String id, String parentId);

    /**
     * 修改此用户组实例的信息
     *
     * @param group 用户组对象
     * @return Group
     */
    Group saveOrUpdate(Group group);

    /**
     * 保存或者更新用户组扩展信息
     *
     * @param id 用户组id
     * @param properties 扩展信息
     * @return {@link Group}
     */
    Group saveProperties(String id, String properties);

}
