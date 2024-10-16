package net.risesoft.api.platform.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.platform.ResourceTypeEnum;
import net.risesoft.model.platform.Resource;
import net.risesoft.pojo.Y9Result;

/**
 * 资源管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface ResourceApi {

    /**
     * 创建菜单资源
     *
     * @param resourceId 资源id
     * @param resourceName 资源名称
     * @param parentResourceId 父资源id
     * @param customId 自定义标识
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是新创建的菜单资源对象
     * @since 9.6.0
     */
    @PostMapping("/createMenuResource")
    Y9Result<Resource> createMenuResource(@RequestParam("resourceId") @NotBlank String resourceId,
        @RequestParam("resourceName") @NotBlank String resourceName,
        @RequestParam("parentResourceId") @NotBlank String parentResourceId,
        @RequestParam("customId") @NotBlank String customId);

    /**
     * 根据customId和parentId获取资源
     *
     * @param customId 自定义id
     * @param parentId 资源id
     * @param resourceType 资源类型 {@link ResourceTypeEnum}
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是资源对象
     * @since 9.6.0
     */
    @GetMapping("/findByCustomIdAndParentId")
    Y9Result<Resource> findByCustomIdAndParentId(@RequestParam("customId") @NotBlank String customId,
        @RequestParam("parentId") @NotBlank String parentId,
        @RequestParam("resourceType") ResourceTypeEnum resourceType);

    /**
     * 获得指定资源的父资源
     *
     * @param resourceId：资源的唯一标识
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是父资源对象
     * @since 9.6.0
     */
    @GetMapping("/getParentResource")
    Y9Result<Resource> getParentResource(@RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 根据id获取资源对象
     *
     * @param resourceId 资源唯一标示
     * @return {@code Y9Result<Resource>} 通用请求返回对象 - data 是资源对象
     * @since 9.6.0
     */
    @GetMapping("/getResource")
    Y9Result<Resource> getResource(@RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 获取指定资源的子菜单资源
     *
     * @param resourceId 资源id
     * @return {@code Y9Result<List<Resource>>} 通用请求返回对象 - data 是资源对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSubMenus")
    Y9Result<List<Resource>> listSubMenus(@RequestParam("resourceId") @NotBlank String resourceId);

    /**
     * 获得指定资源的子资源
     *
     * @param resourceId 资源唯一标识
     * @return {@code Y9Result<List<Resource>>} 通用请求返回对象 - data 是资源对象集合
     * @since 9.6.0
     */
    @GetMapping("/listSubResources")
    Y9Result<List<Resource>> listSubResources(@RequestParam("resourceId") @NotBlank String resourceId);

}
