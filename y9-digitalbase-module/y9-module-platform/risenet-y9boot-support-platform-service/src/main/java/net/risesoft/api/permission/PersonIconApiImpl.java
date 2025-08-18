package net.risesoft.api.permission;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.cache.PersonIconApi;
import net.risesoft.model.platform.permission.PersonIconItem;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9PageQuery;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.PersonIconService;
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
@RequestMapping(value = "/services/rest/v1/personIcon", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PersonIconApiImpl implements PersonIconApi {

    private final PersonIconService personIconService;

    @Override
    public Y9Result<Object> buildPersonalAppIconByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        personIconService.buildPersonalAppIconByOrgUnitId(orgUnitId);

        return Y9Result.success();
    }

    @Override
    public Y9Result<List<PersonIconItem>> listByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return Y9Result.success(personIconService.listByOrgUnitId(orgUnitId));
    }

    @Override
    public Y9Page<PersonIconItem> pageByOrgUnitId(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @Validated Y9PageQuery pageQuery) {
        Y9LoginUserHolder.setTenantId(tenantId);

        return personIconService.pageByOrgUnitId(orgUnitId, pageQuery);
    }

}
