package net.risesoft.service.permission.cache;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.enums.platform.permission.AuthorityEnum;
import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.model.platform.permission.cache.PersonToResource;
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
public interface Y9PersonToResourceService {

    /**
     * 根据授权配置id和组织机构id删除权限缓存
     *
     * @param authorizationId 授权配置id
     * @param orgUnitId 组织机构id
     */
    void deleteByAuthorizationIdAndOrgUnitId(String authorizationId, String orgUnitId);

    /**
     * 根据授权配置id和人员id删除权限缓存
     *
     * @param authorizationId 授权配置id
     * @param personId 人员id
     */
    void deleteByAuthorizationIdAndPersonId(String authorizationId, String personId);

    /**
     * 根据组织id删除其下所有所有人员的权限缓存
     *
     * @param orgUnitId 组织机构id
     */
    void deleteByOrgUnitId(String orgUnitId);

    /**
     * 根据人员id删除
     *
     * @param personId 人员id
     */
    void deleteByPersonId(String personId);

    /**
     * 判断人对资源是否有相应的权限
     *
     * @param personId 人员id
     * @param resourceId 资源id
     * @param authority 授权类型{@link AuthorityEnum}
     * @return boolean
     */
    boolean hasPermission(String personId, String resourceId, AuthorityEnum authority);

    /**
     * 判断人对资源是否有相应权限
     *
     * @param personId 人员id
     * @param resourceCustomId 资源自定义id
     * @param authority 授权类型{@link AuthorityEnum}
     * @return boolean
     */
    boolean hasPermissionByCustomId(String personId, String resourceCustomId, AuthorityEnum authority);

    List<PersonToResource> list(String personId);

    /**
     * 根据人员id、父资源id、资源类型及授权类型查找
     *
     * @param personId 人员id
     * @param parentResourceId 父资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @param authority 授权类型{@link AuthorityEnum}
     * @return {@code List<Y9PersonToResourceAndAuthority>}
     * @see AuthorityEnum
     */
    List<PersonToResource> list(String personId, String parentResourceId, ResourceTypeEnum resourceType,
        AuthorityEnum authority);

    /**
     * 根据人员id 及授权类型查找应用列表
     *
     * @param personId 人员id
     * @param authority {@link AuthorityEnum}
     * @return {@code List<Y9App>}
     */
    List<App> listAppsByAuthority(String personId, AuthorityEnum authority);

    /**
     * 子菜单列表
     *
     * @param personId 人员id
     * @param resourceId 资源id
     * @param authority 权限类型
     * @return {@code List<Y9Menu>}
     */
    List<Menu> listSubMenus(String personId, String resourceId, AuthorityEnum authority);

    /**
     * 获得某一资源下,有相应操作权限的子节点
     *
     * @param personId 人员id
     * @param resourceId 资源id
     * @param authority 权限类型
     * @return {@code List<Y9ResourceBase>}
     */
    List<Resource> listSubResources(String personId, String resourceId, AuthorityEnum authority);

    /**
     * 分页获取人员有权限的应用列表
     *
     * @param personId 人员 id
     * @param authority 权限类型
     * @param pageQuery 分页查询参数
     * @return
     */
    Page<String> pageAppIdByAuthority(String personId, AuthorityEnum authority, Y9PageQuery pageQuery);
}
