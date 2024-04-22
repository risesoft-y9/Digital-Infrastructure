package net.risesoft.api.impl;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.log.CommonAppForPersonApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.Y9CommonAppForPersonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 个人常用应用组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@RestController
@RequestMapping(value = "/services/rest/v1/commonApp")
@RequiredArgsConstructor
public class CommonAppForPersonApiController implements CommonAppForPersonApi {

    private final Y9CommonAppForPersonService commonAppForPersonService;

    /**
     * 根据用户id，获取常用应用名称列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data 是常用应用名称列表
     * @since 9.6.0
     */
    @Override
    @GetMapping(value = "/getAppNames")
    public Y9Result<List<String>> getAppNamesByPersonId(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(commonAppForPersonService.getAppNamesByPersonId(personId));
    }

}
