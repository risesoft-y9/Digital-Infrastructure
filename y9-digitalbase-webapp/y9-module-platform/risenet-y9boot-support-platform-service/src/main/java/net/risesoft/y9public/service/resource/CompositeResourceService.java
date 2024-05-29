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
     * @return {@code List<Y9ResourceBase> }
     */
    List<Y9ResourceBase> findByCustomId(String customId);

    /**
     * 根据customID和parentId获取资源
     *
     * @param customId
     * @param parentId
     * @return
     */
    Optional<? extends Y9ResourceBase> findByCustomIdAndParentId(String customId, String parentId,
        ResourceTypeEnum resourceType);

    /**
     * 根据主键ID获取资源实例 get the instance of resource by id
     *
     * @param id
     * @return 资源对象 或 null
     */
    Y9ResourceBase findById(String id);

    /**
     * 根据资源id和资源类型查找
     *
     * @param resourceId
     * @param resourceType
     * @return
     */
    Y9ResourceBase findByIdAndResourceType(String resourceId, ResourceTypeEnum resourceType);

    /**
     * 根据父资源id查找
     *
     * @param parentId
     * @return
     */
    List<Y9ResourceBase> listByParentId(String parentId);

    /**
     * 通过resourceID，查找子集合
     *
     * @param resourceId
     * @return
     */
    List<Y9ResourceBase> listChildrenById(String resourceId);

    /**
     * 根据系统id查询所有的根资源（App资源）
     *
     * @param systemId
     * @return
     */
    List<Y9ResourceBase> listRootResourceBySystemId(String systemId);

    /**
     * 查询所有的根资源（App资源）
     *
     * @return
     */
    List<Y9App> listRootResourceList();

    /**
     * 根据名称查找
     *
     * @param name
     * @return
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
     * @param name
     * @return
     */
    List<Y9ResourceBase> treeSearch(String name);
}
