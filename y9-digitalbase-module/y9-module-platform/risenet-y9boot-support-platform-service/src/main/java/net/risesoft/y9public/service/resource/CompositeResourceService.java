package net.risesoft.y9public.service.resource;

import java.util.List;
import java.util.Optional;

import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;

/**
 * 组合的资源 service
 * 
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
public interface CompositeResourceService {

    /**
     * 通过自定义ID查找
     *
     * @param customId 自定义id
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> findByCustomId(String customId);

    /**
     * 根据customID和parentId获取资源
     *
     * @param customId 自定义id
     * @param parentId 父资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @return {@code Optional<? extends Y9ResourceBase>}
     */
    Optional<? extends Y9ResourceBase> findByCustomIdAndParentId(String customId, String parentId,
        ResourceTypeEnum resourceType);

    /**
     * 根据主键ID获取资源实例 get the instance of resource by id
     *
     * @param id 唯一标识
     * @return {@link Y9ResourceBase}资源对象 或 null
     */
    Y9ResourceBase findById(String id);

    /**
     * 根据资源id和资源类型查找
     *
     * @param resourceId 资源id
     * @param resourceType 资源类型{@link ResourceTypeEnum}
     * @return {@link Y9ResourceBase}资源对象
     */
    Y9ResourceBase findByIdAndResourceType(String resourceId, ResourceTypeEnum resourceType);

    /**
     * 根据父资源id查找子资源集合
     *
     * @param parentId 父资源id
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> listByParentId(String parentId);

    /**
     * 根据系统id查询所有的根资源（App资源）
     *
     * @param systemId 系统id
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> listRootResourceBySystemId(String systemId);

    /**
     * 查询所有的根资源（App资源）
     *
     * @return {@code List<Y9App>}
     */
    List<Y9App> listRootResourceList();

    /**
     * 根据名称查找
     *
     * @param name 名字
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> searchByName(String name);

    /**
     * 排序
     *
     * @param ids id
     */
    void sort(String[] ids);

    /**
     * 通过名字查询资源
     *
     * @param name 名字
     * @return {@code List<Y9ResourceBase>}
     */
    List<Y9ResourceBase> treeSearch(String name);
}
