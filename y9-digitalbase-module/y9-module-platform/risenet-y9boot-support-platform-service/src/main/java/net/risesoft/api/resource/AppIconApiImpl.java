package net.risesoft.api.resource;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.resource.AppIconApi;
import net.risesoft.model.platform.AppIcon;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9AppIcon;
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
@Primary
@RestController
@Validated
@RequestMapping(value = "/services/rest/v1/appIcon", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AppIconApiImpl implements AppIconApi {

    private final Y9AppIconService appIconService;

    /**
     * 查询所有图标
     *
     * @return {@code Y9Result<List<AppIcon>>} 通用请求返回对象 - data 是应用图标列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<AppIcon>> listAllIcon() {
        List<Y9AppIcon> appIconList = appIconService.listAll();
        return Y9Result.success(Y9ModelConvertUtil.convert(appIconList, AppIcon.class));
    }

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return {@code Y9Result<List<AppIcon>>} 通用请求返回对象 - data 是应用图标列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<AppIcon>> searchAppIcon(@RequestParam("name") @NotBlank String name) {
        List<Y9AppIcon> appIconList = appIconService.listByName(name);
        return Y9Result.success(Y9ModelConvertUtil.convert(appIconList, AppIcon.class));
    }

}
