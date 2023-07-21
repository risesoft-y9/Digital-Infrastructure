package net.risesoft.api.resource;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.AppIcon;
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
@RestController
@RequestMapping(value = "/services/rest/appIcon", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AppIconApiImpl implements AppIconApi {
    
    private final Y9AppIconService appIconService;

    /**
     * 查询所有图标
     *
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/listAllIcon")
    public List<AppIcon> listAllIcon() {
        List<Y9AppIcon> appIconList = appIconService.listAll();
        return Y9ModelConvertUtil.convert(appIconList, AppIcon.class);
    }

    /**
     * 根据名称查询应用图标列表
     *
     * @param name 图标名称
     * @return
     * @since 9.6.0
     */
    @Override
    @GetMapping("/searchAppIcon")
    public List<AppIcon> searchAppIcon(@RequestParam String name) {
        List<Y9AppIcon> appIconList = appIconService.listByName(name);
        return Y9ModelConvertUtil.convert(appIconList, AppIcon.class);
    }

}
