package net.risesoft.service.identity;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.entity.identity.person.Y9PersonToRole;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9public.entity.role.Y9Role;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PersonToRoleService {

    /**
     * 重新计算人员的角色
     *
     * @param personId 人员id
     */
    void recalculate(String personId);

    /**
     * 根据人员id对个人授权计数
     *
     * @param personId
     * @return
     */
    long countByPersonId(String personId);

    /**
     * 根据人员id及系统名对个人授权计数
     *
     * @param personId
     * @param systemName
     * @return
     */
    long countByPersonIdAndSystemName(String personId, String systemName);

    /**
     * 人员是否拥有customId对应的角色
     * 
     * @param personId
     * @param customId
     * @return
     */
    Boolean hasRole(String personId, String customId);

    /**
     * 根据人员id，查询个人授权列表
     *
     * @param personId
     * @return
     */
    List<Y9PersonToRole> listByPersonId(String personId);
    
    /**
     * 根据人员id，分页查询个人授权
     *
     * @param personId
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonId(String personId, int page, int rows);

    /**
     * 根据人员id及应用名称，分页查询个人授权
     *
     * @param personId
     * @param appName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndAppName(String personId, String appName, int page, int rows);

    /**
     * 根据人员id、应用名称及系统中文名，分页查询个人授权
     *
     * @param personId
     * @param appName
     * @param systemCnName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndAppNameAndSystemCnName(String personId, String appName, String systemCnName, int page, int rows);

    /**
     * 根据人员id、应用名称、系统中文名及角色名称，分页查询个人授权
     *
     * @param personId
     * @param appName
     * @param systemCnName
     * @param roleName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndAppNameAndSystemCnNameAndRoleName(String personId, String appName, String systemCnName, String roleName, int page, int rows);

    /**
     * 根据人员id及应用名称列表，分页查询个人授权
     *
     * @param personId
     * @param appNames
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndAppNames(String personId, List<String> appNames, int page, int rows);

    /**
     * 根据人员id及角色名，分页查询个人授权
     *
     * @param personId
     * @param roleName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndRoleName(String personId, String roleName, int page, int rows);

    /**
     * 根据人员id、角色名称及应用名称，分页查询个人授权
     *
     * @param personId
     * @param roleName
     * @param appName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndRoleNameAndAppName(String personId, String roleName, String appName, int page, int rows);

    /**
     * 根据人员id、角色名称及系统中文名，分页查询个人授权
     *
     * @param personId
     * @param roleName
     * @param systemCnName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndRoleNameAndSystemCnName(String personId, String roleName, String systemCnName, int page, int rows);

    /**
     * 根据人员id、角色名及系统中文名，分页查询个人授权
     *
     * @param personId
     * @param roleName
     * @param systemCnName
     * @param appNames
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndRoleNameAndSystemCnNames(String personId, String roleName, String systemCnName, List<String> appNames, int page, int rows);

    /**
     * 根据人员id、角色名及应用名列表，分页查询个人授权
     *
     * @param personId
     * @param roleName
     * @param appNames
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndRoleNames(String personId, String roleName, List<String> appNames, int page, int rows);

    /**
     * 根据人员id及系统中文名，分页查询个人授权
     *
     * @param personId
     * @param systemCnName
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndSystemCnName(String personId, String systemCnName, int page, int rows);

    /**
     * 根据人员id及系统中文名，分页查询个人授权
     *
     * @param personId
     * @param systemCnName
     * @param appNames
     * @param page
     * @param rows
     * @return
     */
    Page<Y9PersonToRole> pageByPersonIdAndSystemCnNames(String personId, String systemCnName, List<String> appNames, int page, int rows);

    /**
     * 获取个人权限列表（特定角色需要权限查看）
     *
     * @param personId 人员唯一标识
     * @param type 节点类型
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page page
     * @param rows rows
     * @return
     */
    Y9Page<Map<String, Object>> pagePersonAccessPermission(String personId, String type, String systemCnName, String appName, String roleName, int page, int rows);

    /**
     * 获取个人权限列表
     *
     * @param personId 人员唯一标识
     * @param type 节点类型
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page page
     * @param rows rows
     * @return
     */
    Y9Page<Map<String, Object>> pagePersonPermission(String personId, String type, String systemCnName, String appName, String roleName, int page, int rows);

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

    /**
     * 更新人员授权信息
     *
     * @param roleId
     * @param roleName
     * @param systemName
     * @param systemCnName
     * @param description
     */
    void update(String roleId, String roleName, String systemName, String systemCnName, String description);

    /**
     * 根据角色更新
     *
     * @param y9Role
     * @return
     */
    void updateByRole(Y9Role y9Role);

    /**
     * 根据人员id获取拥有的角色id（,分隔）
     *
     * @param personId id
     * @return {@link String}
     */
    String getRoleIdsByPersonId(String personId);
}
