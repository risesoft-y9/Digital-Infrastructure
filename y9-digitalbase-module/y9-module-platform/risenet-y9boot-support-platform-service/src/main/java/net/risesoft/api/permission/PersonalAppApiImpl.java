package net.risesoft.api.permission;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.cache.PersonalAppApi;
import net.risesoft.entity.permission.cache.Y9PersonalApp;
import net.risesoft.model.platform.permission.cache.PersonalApp;
import net.risesoft.model.platform.resource.App;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonalAppService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.Y9AppService;

/**
 * 人员图标管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/personalApp", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PersonalAppApiImpl implements PersonalAppApi {

    private final Y9PersonalAppService y9PersonalAppService;
    private final Y9AppService y9AppService;

    @Override
    public Y9Result<Object> buildPersonalAppIconByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonalAppService.buildPersonalAppByOrgUnitId(orgUnitId);

        return Y9Result.success();
    }

    @Override
    public Y9Result<List<PersonalApp>> listByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9PersonalApp> y9PersonalAppList = y9PersonalAppService.listByOrgUnitId(orgUnitId);
        return Y9Result.success(convert(y9PersonalAppList));
    }

    @Override
    public Y9Page<PersonalApp> pageByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId,
        @RequestParam(name = "categoryId", required = false) String categoryId, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<Y9PersonalApp> appList = y9PersonalAppService.pageByOrgUnitId(orgUnitId, categoryId, pageQuery);
        return Y9Page.success(pageQuery.getPage(), appList.getTotalPages(), appList.getTotalElements(),
            convert(appList.getContent()));
    }

    @Override
    public Y9Result<Object> sort(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("appIdList") List<String> appIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonalAppService.sort(orgUnitId, appIdList);

        return Y9Result.success();
    }

    private List<PersonalApp> convert(List<Y9PersonalApp> iconList) {
        List<PersonalApp> appList = new ArrayList<>();
        for (Y9PersonalApp y9PersonalApp : iconList) {
            App app = y9AppService.findById(y9PersonalApp.getAppId()).orElse(null);
            if (null == app) {
                continue;
            }
            boolean enable = app.getEnabled();
            if (enable) {
                PersonalApp personalApp = new PersonalApp();
                personalApp.setAppId(app.getId());
                personalApp.setAppName(app.getName());
                personalApp.setUrl(app.getUrl());
                personalApp.setIconUrl(app.getIconUrl());
                personalApp.setIconData(app.getIconData());
                personalApp.setShowNumber(app.getShowNumber());
                personalApp.setNumberUrl(app.getNumberUrl());
                personalApp.setOpentype(app.getOpentype());
                personalApp.setTabIndex(y9PersonalApp.getTabIndex());
                personalApp.setStar(y9PersonalApp.getStar());
                appList.add(personalApp);
            }
        }
        return appList;
    }

}
