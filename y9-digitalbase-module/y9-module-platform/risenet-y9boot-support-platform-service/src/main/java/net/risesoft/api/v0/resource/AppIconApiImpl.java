package net.risesoft.api.v0.resource;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.resource.AppIconApi;
import net.risesoft.model.platform.AppIcon;
import net.risesoft.y9public.service.resource.Y9AppIconService;

/**
 * 应用图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@RestController(value = "v0AppIconApiImpl")
@Validated
@RequestMapping(value = "/services/rest/appIcon", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class AppIconApiImpl implements AppIconApi {

    private final Y9AppIconService appIconService;

    /**
     * 查询所有图标
     *
     * @return {@code List<AppIcon>}
     * @since 9.6.0
     */
    @Override
    public List<AppIcon> listAllIcon() {
        return appIconService.listAll();
    }

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return {@code List<AppIcon>}
     * @since 9.6.0
     */
    @Override
    public List<AppIcon> searchAppIcon(@RequestParam("name") @NotBlank String name) {
        return appIconService.listByName(name);
    }

}
