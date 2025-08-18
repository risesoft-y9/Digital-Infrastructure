package net.risesoft.service.permission.cache;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.permission.Y9Authorization;
import net.risesoft.entity.permission.cache.position.Y9PositionToResourceAndAuthority;
import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.pojo.Y9PageQuery;
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
     * 根据授权配置id和组织id删除
     *
     * @param authorizationId 授权配置id
     * @param orgId 组织id
     */
    void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgId);

    /**
     * 根据授权配置id和岗位id删除
     *
     * @param authorizationId 授权配置id
     * @param positionId 岗位id
     */
    void deleteByAuthorizationIdAndPositionId(String authorizationId, String positionId);

    /**
     * 根据组织id删除其下所有所有岗位的授权缓存
     *
     * @param orgUnitId 组织id
     */
    void deleteByOrgUnitId(String orgUnitId);

    /**
     * 根据岗位id删除
     *
     * @param positionId 岗位id
     */
    void deleteByPositionId(String positionId);

    /**
     * 判断岗对资源是否有相应的权限
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param authority 权限类型{@link AuthorityEnum}
     * @return boolean
     */
    boolean hasPermission(String positionId, String resourceId, AuthorityEnum authority);

    /**
     * 判断岗对资源是否有相应的权限
     *
     * @param positionId 岗位id
     * @param customId 自定义id
     * @param authority 权限类型 {@link AuthorityEnum}
     * @return boolean
     */
    boolean hasPermissionByCustomId(String positionId, String customId, AuthorityEnum authority);

    List<Y9PositionToResourceAndAuthority> list(String positionId);

    /**
     * 根据岗位id、父资源id及授权类型查找
     *
     * @param positionId 岗位id
     * @param parentResourceId 父资源id
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9PositionToResourceAndAuthority>}
     */
    List<Y9PositionToResourceAndAuthority> list(String positionId, String parentResourceId, AuthorityEnum authority);

    /**
     * 根据岗位id、父资源id、资源类型及授权类型查找
     *
     * @param positionId 岗位id
     * @param parentResourceId 父资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9PositionToResourceAndAuthority>}
     */
    List<Y9PositionToResourceAndAuthority> list(String positionId, String parentResourceId,
        ResourceTypeEnum resourceType, AuthorityEnum authority);

    /**
     * 根据岗位id 及授权类型查找应用列表
     *
     * @param positionId 岗位id
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9App>}
     */
    List<Y9App> listAppsByAuthority(String positionId, AuthorityEnum authority);

    /**
     * 根据岗位id查找
     *
     * @param positionId 岗位id
     * @return {@code List<Y9PositionToResourceAndAuthority>}
     */
    List<Y9PositionToResourceAndAuthority> listByPositionId(String positionId);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9Menu>}
     */
    List<Y9Menu> listSubMenus(String positionId, String resourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority);

    /**
     * 获得某一资源下，有相应操作权限的子节点
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> listSubResources(String positionId, String resourceId, AuthorityEnum authority);

    /**
     * 更新或保存
     *
     * @param y9ResourceBase 资源信息
     * @param y9Position 岗位信息
     * @param y9Authorization 权限配置信息
     * @param inherit 是否为继承上级节点的权限
     */
    void saveOrUpdate(Y9ResourceBase y9ResourceBase, Y9Position y9Position, Y9Authorization y9Authorization,
        Boolean inherit);

    /**
     * 分页获取岗位有权限的应用列表
     *
     * @param positionId 岗位 id
     * @param authority 权限类型
     * @param pageQuery 分页查询参数
     * @return
     */
    Page<String> pageAppIdByAuthority(String positionId, AuthorityEnum authority, Y9PageQuery pageQuery);
}
