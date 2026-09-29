package net.risesoft.api.v0.resource;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.resource.SystemApi;
import net.risesoft.model.platform.System;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 系统管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Primary
@Validated
@RestController(value = "v0SystemApiImpl")
@RequestMapping(value = "/services/rest/system", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class SystemApiImpl implements SystemApi {

    private final Y9SystemService y9SystemService;

    private final Y9TenantSystemService y9TenantSystemService;

    /**
     * 根据系统id,获取系统
     *
     * @param id 系统唯一标识
     * @return AdminSystem 系统管理员
     * @since 9.6.2
     */
    @Override
    public System getById(@RequestParam("id") @NotBlank String id) {
        return y9SystemService.getById(id);
    }

    /**
     * 根据系统名获取系统
     *
     * @param name 系统名称
     * @return System 系统
     */
    @Override
    public System getByName(@RequestParam("name") @NotBlank String name) {
        return y9SystemService.findByName(name).orElse(null);
    }

    /**
     * 注册系统
     *
     * @param name 系统英文名称
     * @param cnName 系统名称
     * @param contextPath 系统上下文
     * @param isvGuid 租户id
     * @return {@code Y9Result<System>}
     */
    @Override
    public Y9Result<System> registrySystem(String name, String cnName, String contextPath, String isvGuid) {
        System system = new System();
        system.setName(name);
        system.setCnName(cnName);
        system.setContextPath(contextPath);
        System savedSystem = y9SystemService.saveAndRegister4Tenant(system);

        return Y9Result.success(PlatformModelConvertUtil.convert(savedSystem, System.class), "注册应用成功！");
    }

}
