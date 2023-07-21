package net.risesoft.api.permission;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

/**
 * 人员角色接口
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface PersonRoleApi {

    /**
     * 根据人员id获取该人员拥有的角色个数
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @return long 角色数
     * @since 9.6.0
     */
    long countByPersonId(String tenantId, String personId);

    /**
     * 判断人员是否拥有 customId 对应的角色
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @param customId 自定义id
     * @return Boolean 是否拥有权限
     * @since 9.6.0
     */
    Boolean hasRole(String tenantId, String personId, String customId);

    /**
     * 获取人员的权限分页列表
     *
     * @param tenantId 租户id
     * @param personId 人员ID
     * @param type 查询类型，0：查询所有，1：查询拥有的角色
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page 第几页
     * @param rows 多少条数据
     * @return Y9Page&lt;Map&lt;String, Object&gt;&gt; 角色权限列表
     * @since 9.6.0
     */
    Y9Page<Map<String, Object>> pagePersonPermission(String tenantId, String personId, String type, String systemCnName, String appName, String roleName, int page, int rows);

    /**
     * 获取个人权限分页列表（拥有门户网站的权限查看角色才能查看到特殊角色）
     *
     * @param tenantId 租户id
     * @param personId 人员唯一标识
     * @param type 节点类型,0：查询所有，1：查询拥有的角色
     * @param systemCnName 系统中文名称
     * @param appName 应用名称
     * @param roleName 角色名称
     * @param page 第几页
     * @param rows 多少条数据
     * @return Y9Page&lt;Map&lt;String, Object&gt;&gt; 角色权限列表
     * @since 9.6.0
     */
    Y9Page<Map<String, Object>> pagePersonPermissionWithSpecial(String tenantId, String personId, String type, String systemCnName, String appName, String roleName, int page, int rows);
}
