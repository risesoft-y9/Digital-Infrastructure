package net.risesoft.api.impl;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.log.CommonAppForPersonApi;
import net.risesoft.log.service.Y9CommonAppForPersonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 个人常用应用组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@RestController
@RequestMapping(value = "/services/rest/v1/commonapp")
@RequiredArgsConstructor
public class CommonAppForPersonApiController implements CommonAppForPersonApi {

    private final Y9CommonAppForPersonService commonAppForPersonService;

    /**
     * 根据用户id，获取常用应用名称列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String 常用应用名称列表
     * @since 9.6.0
     */
    @Override
    @GetMapping(value = "/getAppNames")
    public String getAppNamesByPersonId(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return commonAppForPersonService.getAppNamesByPersonId(personId);
    }

    /**
     * 根据用户id，从日志中提取常用应用名称列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return String 常用应用名称列表
     * @since 9.6.0
     */
    @Override
    @GetMapping(value = "/getAppNamesFromLog")
    public String getAppNamesByPersonIdFromLog(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") @NotBlank String personId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return commonAppForPersonService.getAppNamesFromLog(personId);
    }

    /**
     * 半年应用点击次数
     *
     * @return long 点击次数
     * @since 9.6.0
     */
    @Override
    @GetMapping(value = "/getCount")
    public long getCount() {
        return commonAppForPersonService.getCount();
    }

    /**
     * 保存常用应用
     *
     * @return String 操作结果
     * @since 9.6.0
     */
    @Override
    @PostMapping(value = "/save")
    public String saveCommonApp() {
        return commonAppForPersonService.saveForQuery();
    }

    /**
     * 同步近半年的数据
     *
     * @return String 操作结果
     * @since 9.6.0
     */
    @Override
    @PostMapping(value = "/syncData")
    public String syncData() {
        return commonAppForPersonService.syncData();
    }
}
