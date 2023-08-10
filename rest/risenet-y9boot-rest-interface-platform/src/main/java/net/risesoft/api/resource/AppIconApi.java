package net.risesoft.api.resource;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.AppIcon;

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
     * @return List&lt;AppIcon&gt;
     * @since 9.6.0
     */
    @GetMapping("/listAllIcon")
    List<AppIcon> listAllIcon();

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return List&lt;AppIcon&gt;
     * @since 9.6.0
     */
    @GetMapping("/searchAppIcon")
    List<AppIcon> searchAppIcon(@RequestParam("name") @NotBlank String name);
}
