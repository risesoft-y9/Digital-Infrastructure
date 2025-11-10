package net.risesoft.service.permission.cache;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.permission.cache.PositionToResource;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Resource;
import net.risesoft.pojo.Y9PageQuery;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface Y9PositionToResourceService {

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

    /**
     * 根据岗位id查找
     *
     * @param positionId 岗位id
     * @return {@code List<Y9PositionToResourceAndAuthority>}
     */
    List<PositionToResource> list(String positionId);

    /**
     * 根据岗位id、父资源id、资源类型及授权类型查找
     *
     * @param positionId 岗位id
     * @param parentResourceId 父资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9PositionToResourceAndAuthority>}
     */
    List<PositionToResource> list(String positionId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority);

    /**
     * 根据岗位id 及授权类型查找应用列表
     *
     * @param positionId 岗位id
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9App>}
     */
    List<App> listAppsByAuthority(String positionId, AuthorityEnum authority);

    /**
     * 获得某一资源下,主体对象有相应操作权限的子菜单
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9Menu>}
     */
    List<Menu> listSubMenus(String positionId, String resourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority);

    /**
     * 获得某一资源下，有相应操作权限的子节点
     *
     * @param positionId 岗位id
     * @param resourceId 资源id
     * @param authority 权限类型{@link AuthorityEnum}
     * @return {@code List<Y9ResourceBase>}
     */
    List<Resource> listSubResources(String positionId, String resourceId, AuthorityEnum authority);

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
