package net.risesoft.service.identity;

import java.util.List;

import net.risesoft.entity.Y9Person;
import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.y9public.entity.role.Y9Role;

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
     * @param personId
     * @return
     */
    long countByPersonId(String personId);

    /**
     * 根据人员id获取拥有的角色id（,分隔）
     *
     * @param personId id
     * @return {@link String}
     */
    String getRoleIdsByPersonId(String personId);

    boolean hasPublicRole(String personId, String roleName);

    Boolean hasRole(String personId, String systemName, String roleName, String properties);

    boolean hasRole(String personId, String roleId);

    /**
     * 人员是否拥有customId对应的角色
     * 
     * @param personId
     * @param customId
     * @return
     */
    Boolean hasRoleByCustomId(String personId, String customId);

    /**
     * 根据人员id，查询个人授权列表
     *
     * @param personId
     * @return
     */
    List<Y9PersonToRole> listByPersonId(String personId);

    /**
     * 根据角色id查询拥有角色的所有人
     *
     * @param roleId 角色id
     * @return {@link List}<{@link Y9Person}>
     */
    List<Y9Person> listPersonsByRoleId(String roleId);

    /**
     * 根据人员id查询其拥有的所有角色
     *
     * @param personId 人员id
     * @return {@link List}<{@link Y9Role}>
     */
    List<Y9Role> listRolesByPersonId(String personId);

    /**
     * 根据人员id删除
     *
     * @param personId
     */
    void removeByPersonId(String personId);

    /**
     * 根据角色id移除
     *
     * @param roleId
     */
    void removeByRoleId(String roleId);

}
