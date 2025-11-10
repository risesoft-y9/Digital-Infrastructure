package net.risesoft.service.permission;

import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.permission.AuthorizationPrincipalTypeEnum;
import net.risesoft.model.platform.permission.Authorization;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9AuthorizationService {

    /**
     * 根据id删除
     *
     * @param id 唯一标识
     */
    void delete(String id);

    /**
     * 根据多个权限id删除权限
     *
     * @param ids 资源id数组
     */
    void delete(String[] ids);

    /**
     * 根据主键，获取权限配置对象
     *
     * @param id 唯一标识
     * @return Authorization
     */
    Optional<Authorization> findById(String id);

    /**
     * 根据授权主体id和授权主体类型查找
     *
     * @param principalId 授权主体的唯一标识
     * @param principalTypeEnum 授权主体类型 {@link AuthorizationPrincipalTypeEnum}
     * @return {@code List<Authorization>}
     */
    List<Authorization> listByPrincipalIdAndPrincipalType(String principalId,
        AuthorizationPrincipalTypeEnum principalTypeEnum);

    /**
     * 根据授权主体类型principalType和资源Id查询
     *
     * @param principalType 授权主体类型
     * @param resourceId 资源id
     * @return {@code List<Authorization>}
     */
    List<Authorization> listByPrincipalTypeAndResourceId(AuthorizationPrincipalTypeEnum principalType,
        String resourceId);

    /**
     * 根据授权主体类型principalType和资源id为条件查询，通过 principalType 排除某个授权主体类型
     *
     * @param resourceId 资源id
     * @param principalType 授权主体类型
     * @return {@code List<Authorization>}
     */
    List<Authorization> listByResourceIdAndPrincipalTypeNot(String resourceId,
        AuthorizationPrincipalTypeEnum principalType);

    /**
     * 根据RoleIds查询
     *
     * @param principalIds 授权主体的唯一标识列表
     * @param resourceId 资源id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return {@code List<Authorization>}
     */
    List<Authorization> listByRoleIds(List<String> principalIds, String resourceId, AuthorityEnum authority);

    /**
     * 根据授权主体id，获取与此授权主体相关的权限分页记录
     *
     * @param principalId 授权主体的唯一标识
     * @param pageQuery 分页查询
     * @return {@code Page<Authorization>}
     */
    Y9Page<Authorization> pageByPrincipalId(String principalId, Y9PageQuery pageQuery);

    void save(AuthorityEnum authority, String principalId, AuthorizationPrincipalTypeEnum principalType,
        String[] resourceIds);

    void saveByOrg(AuthorityEnum authority, String resourceId, String[] principleIds);

    void saveByRoles(AuthorityEnum authority, String resourceId, String[] roleIds);

    /**
     * 保存或者修改资源关联组织信息
     *
     * @param y9Authorization 权限配置对象
     * @return {@link Authorization}
     */
    Authorization saveOrUpdateOrg(Authorization y9Authorization);

    /**
     * 保存或者修改资源关联角色信息
     *
     * @param y9Authorization 权限配置对象
     * @return {@link Authorization}
     */
    Authorization saveOrUpdateRole(Authorization y9Authorization);

    /**
     * 获取资源继承的角色授权列表
     *
     * @param authorizationPrincipalType 授权主体类型
     * @param resourceId 资源id
     * @return {@code List<Authorization> }
     */
    List<Authorization> listInheritByPrincipalTypeAndResourceId(
        AuthorizationPrincipalTypeEnum authorizationPrincipalType, String resourceId);

    /**
     * 获取资源继承的组织节点授权列表
     *
     * @param resourceId 资源id
     * @return {@code List<Authorization> }
     */
    List<Authorization> listInheritByPrincipalTypeIsOrgUnitAndResourceId(String resourceId);

    List<String> listResourceIdByPrincipleId(String roleId, AuthorityEnum authority);

    List<String> listPrincipalIdByResourceId(String resourceId, AuthorityEnum authority);
}
