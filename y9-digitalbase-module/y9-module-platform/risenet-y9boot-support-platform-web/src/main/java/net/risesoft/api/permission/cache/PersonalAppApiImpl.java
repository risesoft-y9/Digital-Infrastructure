package net.risesoft.api.permission.cache;

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
import net.risesoft.model.platform.permission.cache.PersonalApp;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonalAppService;
import net.risesoft.y9.Y9LoginUserHolder;

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

        return Y9Result.success(y9PersonalAppService.listByOrgUnitId(orgUnitId));
    }

    @Override
    public Y9Page<PersonalApp> pageByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId,
        @RequestParam(name = "categoryId", required = false) String categoryId, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        Page<PersonalApp> appList = y9PersonalAppService.pageByOrgUnitId(orgUnitId, categoryId, pageQuery);
        return Y9Page.success(pageQuery.getPage(), appList.getTotalPages(), appList.getTotalElements(),
            appList.getContent());
    }

    @Override
    public Y9Result<Object> sort(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("appIdList") List<String> appIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);

        y9PersonalAppService.sort(orgUnitId, appIdList);

        return Y9Result.success();
    }

}
