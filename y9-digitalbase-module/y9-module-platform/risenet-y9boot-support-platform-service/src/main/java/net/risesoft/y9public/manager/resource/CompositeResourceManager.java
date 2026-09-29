package net.risesoft.y9public.manager.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.resource.ResourceTypeEnum;
import net.risesoft.y9.exception.Y9NotFoundException;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9DataCatalog;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * CompositeResourceManager
 *
 * @author shidaobang
 * @date 2024/12/12
 * @since 9.6.8
 */
public interface CompositeResourceManager {

    /**
     * 根据id获取菜单信息
     *
     * @param id 菜单id
     * @return {@link Y9Menu}
     */
    Y9Menu findMenuById(String id);

    /**
     * 根据id获取按钮信息
     *
     * @param id 按钮id
     * @return {@link Y9Operation}
     */
    Y9Operation findOperationById(String id);

    /**
     * 根据id获取应用信息
     *
     * @param id 应用id
     * @return {@link Y9App}
     */
    Y9App findAppById(String id);

    /**
     * 根据id获取数据目录信息
     *
     * @param id 数据目录id
     * @return {@link Y9DataCatalog}
     */
    Y9DataCatalog findDataCatalogById(String id);

    /**
     * 根据id获取资源信息
     *
     * @param id 资源id
     * @return {@link Y9ResourceBase}
     * @throws Y9NotFoundException id 对应的资源不存在的情况
     */
    Y9ResourceBase getById(String id);

    /**
     * 获取可作为父节点的资源
     *
     * @param resourceId 资源id
     * @return {@link Y9ResourceBase}
     * @throws Y9NotFoundException resourceId 对应的父资源不存在的情况
     */
    Y9ResourceBase getResourceAsParent(String resourceId);

    /**
     * 根据id查找资源
     *
     * @param id 资源id
     * @return {@code Optional<Y9ResourceBase>}
     */
    Optional<Y9ResourceBase> findResource(String id);

    /**
     * 通过自定义ID查找
     *
     * @param customId 自定义id
     * @return {@link Y9ResourceBase}
     * @throws Y9NotFoundException customId 对应的资源不存在的情况
     */
    Y9ResourceBase getByCustomId(String customId);

    /**
     * 根据资源id和资源类型查找
     *
     * @param resourceId 资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @return {@link Y9ResourceBase}资源对象
     */
    Y9ResourceBase findByIdAndResourceType(String resourceId, ResourceTypeEnum resourceType);

    /**
     * 根据父资源id获取子资源列表
     *
     * @param parentId 父资源id
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> listByParentId(String parentId);

    /**
     * 获取应用下的所有资源（包括它本身）
     *
     * @param appId 应用 id
     * @return {@code List<Y9ResourceBase> }
     */
    List<Y9ResourceBase> findByAppId(String appId);
}
