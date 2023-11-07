package net.risesoft.api.resource;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.AppIcon;
import net.risesoft.pojo.Y9Result;

/**
 * 应用图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface AppIconApi {

    /**
     * 查询所有图标
     *
     * @return {@code Y9Result<List<AppIcon>>} 通用请求返回对象 - data 是应用图标列表
     * @since 9.6.0
     */
    @GetMapping("/listAllIcon")
    Y9Result<List<AppIcon>> listAllIcon();

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return {@code Y9Result<List<AppIcon>>} 通用请求返回对象 - data 是应用图标列表
     * @since 9.6.0
     */
    @GetMapping("/searchAppIcon")
    Y9Result<List<AppIcon>> searchAppIcon(@RequestParam("name") @NotBlank String name);
}
