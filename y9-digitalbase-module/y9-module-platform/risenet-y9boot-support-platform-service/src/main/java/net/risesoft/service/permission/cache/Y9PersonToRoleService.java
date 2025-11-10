package net.risesoft.service.permission.cache;

import java.util.List;

import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.org.Person;
import net.risesoft.model.platform.permission.cache.PersonToRole;

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

    boolean hasPublicRole(String personId, String roleName);

    Boolean hasRole(String personId, String systemName, String roleName, String properties);

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
