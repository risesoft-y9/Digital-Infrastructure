package net.risesoft.service.identity;

import java.util.List;

import net.risesoft.entity.Y9Position;
import net.risesoft.entity.identity.position.Y9PositionToResourceAndAuthority;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionToResourceAndAuthorityService {

    /**
     * 按应用id删除
     *
     * @param appId id
     */
    void deleteByAppId(String appId);

    /**
     * 根据授权配置id删除权限缓存
     *
     * @param authorizationId
     */
    void deleteByAuthorizationId(String authorizationId);

    /**
     * 根据授权配置id和组织id删除
     *
     * @param authorizationId
     * @param orgId
     */
    void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgId);

    /**
     * 根据授权配置id和岗位id删除
     *
     * @param authorizationId
     * @param positionId
     */
    void deleteByAuthorizationIdAndPositionId(String authorizationId, String positionId);

    /**
     * 根据组织id删除其下所有所有岗位的授权缓存
     *
     * @param orgUnitId
     */
    void deleteByOrgUnitId(String orgUnitId);

    /**
     * 根据岗位id删除
     *
     * @param positionId
     */
    void deleteByPositionId(String positionId);

    /**
     * 按资源id删除
     *
     * @param resourceId id
     */
    void deleteByResourceId(String resourceId);

    /**
     * 判断岗对资源是否有相应的权限
     *
     * @param positionId
     * @param resourceId
     * @param authority
     * @return
     */
    boolean hasPermission(String positionId, String resourceId, AuthorityEnum authority);

    /**
     * 判断岗对资源是否有相应的权限
     *
     * @param positionId
     * @param customId
     * @param authority
     * @return
     */
    boolean hasPermissionByCustomId(String positionId, String customId, AuthorityEnum authority);

    List<Y9PositionToResourceAndAuthority> list(String positionId);

    /**
     * 根据岗位id、父资源id及授权类型查找
     *
     * @param positionId
     * @param parentResourceId
     * @param authority
     * @return
     */
    List<Y9PositionToResourceAndAuthority> list(String positionId, String parentResourceId, AuthorityEnum authority);

    /**
     * 根据岗位id、父资源id、资源类型及授权类型查找
     *
     * @param positionId
     * @param parentResourceId
     * @param resourceType
     * @param authority
     * @return
     */
    List<Y9PositionToResourceAndAuthority> list(String positionId, String parentResourceId,
        ResourceTypeEnum resourceType, AuthorityEnum authority);

    /**
     * 根据岗位id 及授权类型查找应用列表
     *
     * @param positionId 岗位id
     * @param authority {@link AuthorityEnum}
     * @return
     */
    List<Y9App> listAppsByAuthority(String positionId, AuthorityEnum authority);

    /**
     * 根据人员查找
     *
     * @param positionId
     * @return
     */
    List<Y9PositionToResourceAndAuthority> listByPositionId(String positionId);

    void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization,
        Boolean inherit);

    /**
     * 更新
     *
     * @param resourceId
     * @param resourceName
     * @param systemName
     * @param systemCnName
     * @param description
     * @return
     */
    void updateByResourceId(String resourceId, String resourceName, String systemName, String systemCnName,
        String description);

    /**
     * 获得某一资源下，有相应操作权限的子节点
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param authority 权限类型
     * @return {@link List}<{@link Y9ResourceBase}>
     */
    List<Y9ResourceBase> listSubResources(String positionId, String resourceId, AuthorityEnum authority);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param resourceType 资源类型
     * @param authority 权限类型
     * @return {@link List}<{@link Y9Menu}>
     */
    List<Y9Menu> listSubMenus(String positionId, String resourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority);
}
