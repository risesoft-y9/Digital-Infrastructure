package net.risesoft.api.resource;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.ResourceTypeEnum;
import net.risesoft.model.Resource;

/**
 * 资源管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
public interface ResourceApi {

    /**
     * 创建资源
     *
     * @param resourceId 资源id
     * @param resourceName 资源名称
     * @param parentResourceId 父资源id
     * @param customId 自定义标识
     * @return Resource 新创建的资源对象
     * @since 9.6.0
     */
    @PostMapping("/createMenuResource")
    Resource createMenuResource(@RequestParam("resourceId") String resourceId, @RequestParam("resourceName") String resourceName, @RequestParam("parentResourceId") String parentResourceId, @RequestParam("customId") String customId);

    /**
     * 根据customId获取资源，用于工作流，customId保存的是processDefinitionKey
     *
     * @param customId customId
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @GetMapping("/findByCustomId")
    Resource findByCustomId(@RequestParam("customId") String customId);

    /**
     * 根据customId和parentId获取资源
     *
     * @param customId customId
     * @param parentId 资源id
     * @param resourceType 资源类型 {@link ResourceTypeEnum}
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @GetMapping("/findByCustomIdAndParentId")
    Resource findByCustomIdAndParentId(@RequestParam("customId") String customId, @RequestParam("parentId") String parentId, @RequestParam("resourceType") Integer resourceType);

    /**
     * 获得指定资源的父资源
     *
     * @param resourceId：资源的唯一标识
     * @return Resource 父资源
     * @since 9.6.0
     */
    @GetMapping("/getParentResource")
    Resource getParentResource(@RequestParam("resourceId") String resourceId);

    /**
     * 获得指定资源对象
     *
     * @param resourceId 资源唯一标示
     * @return Resource 资源对象
     * @since 9.6.0
     */
    @GetMapping("/getResource")
    Resource getResource(@RequestParam("resourceId") String resourceId);

    /**
     * 根据系统标识获取该系统的资源树的顶级节点
     *
     * @param systemName 系统标识
     * @return Resource 资源节点
     * @since 9.6.0
     */
    @GetMapping("/getRootResourceBySystemName")
    Resource getRootResourceBySystemName(@RequestParam("systemName") String systemName);

    /**
     * 获取指定资源的菜单子资源
     *
     * @param resourceId 资源id
     * @return List&lt;Resource&gt; 资源对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSubMenus")
    List<Resource> listSubMenus(@RequestParam("resourceId") String resourceId);

    /**
     * 获得指定资源的子资源
     *
     * @param resourceId 资源唯一标识
     * @return List&lt;Resource&gt; 资源对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSubResources")
    List<Resource> listSubResources(@RequestParam("resourceId") String resourceId);

}
