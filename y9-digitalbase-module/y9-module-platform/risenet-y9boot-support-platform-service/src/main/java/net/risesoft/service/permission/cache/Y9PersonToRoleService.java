package net.risesoft.service.permission.cache;

import java.util.List;

import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.permission.cache.PersonToRole;
import net.risesoft.y9.exception.Y9NotFoundException;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonToRoleService {

    /**
     * 根据人员id对个人授权计数
     *
     * @param personId 人员id
     * @return long
     */
    long countByPersonId(String personId);

    /**
     * 判断人员是否拥有指定名称的公共角色
     *
     * @param personId 人员id
     * @param roleName 角色名称
     * @return boolean
     */
    boolean hasPublicRole(String personId, String roleName);

    /**
     * 判断人员是否拥有指定系统、名称和扩展属性的角色
     *
     * @param personId 人员id
     * @param systemName 系统名称
     * @param roleName 角色名称
     * @param properties 扩展属性
     * @return {@link Boolean}
     * @throws Y9NotFoundException 系统名称对应的系统不存在
     */
    Boolean hasRole(String personId, String systemName, String roleName, String properties);

    /**
     * 判断人员是否拥有指定角色
     *
     * @param personId 人员id
     * @param roleId 角色id
     * @return boolean
     */
    boolean hasRole(String personId, String roleId);

    /**
     * 人员是否拥有customId对应的角色
     *
     * @param personId 人员id
     * @param customId 自定义id
     * @return Boolean
     */
    boolean hasRoleByCustomId(String personId, String customId);

    /**
     * 根据人员id，查询个人授权列表
     *
     * @param personId 人员id
     * @return {@code List<Y9PersonToRole>}
     */
    List<PersonToRole> listByPersonId(String personId);

    /**
     * 根据角色id查询拥有角色的所有人
     *
     * @param roleId 角色id
     * @param personDisabled 人员是否禁用
     * @return {@code List<Y9Person>}
     */
    List<Person> listPersonsByRoleId(String roleId, Boolean personDisabled);

    /**
     * 根据人员id查询其拥有的所有角色
     *
     * @param personId 人员id
     * @return {@code List<Y9Role>}
     */
    List<Role> listRolesByPersonId(String personId);

}
