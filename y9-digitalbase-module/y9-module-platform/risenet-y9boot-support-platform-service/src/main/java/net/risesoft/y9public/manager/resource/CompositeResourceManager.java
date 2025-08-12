package net.risesoft.y9public.manager.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.ResourceTypeEnum;
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

    Y9Menu findMenuById(String id);

    Y9Operation findOperationById(String id);

    Y9App findAppById(String id);

    Y9DataCatalog findDataCatalogById(String id);

    Y9ResourceBase getById(String id);

    Y9ResourceBase getResourceAsParent(String resourceId);

    Optional<Y9ResourceBase> findResource(String id);

    /**
     * 通过自定义ID查找
     *
     * @param customId 自定义id
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> findByCustomId(String customId);

    /**
     * 根据资源id和资源类型查找
     *
     * @param resourceId 资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @return {@link Y9ResourceBase}资源对象
     */
    Y9ResourceBase findByIdAndResourceType(String resourceId, ResourceTypeEnum resourceType);

    List<Y9ResourceBase> listByParentId(String parentId);
}
