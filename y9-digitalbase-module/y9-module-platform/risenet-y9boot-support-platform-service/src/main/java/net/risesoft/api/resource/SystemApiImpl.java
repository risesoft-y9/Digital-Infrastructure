package net.risesoft.api.resource;

import java.util.List;
import java.util.Optional;

import jakarta.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.model.platform.System;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
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
@RestController
@RequestMapping(value = "/services/rest/v1/system", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SystemApiImpl implements SystemApi {

    private final Y9SystemService y9SystemService;

    private final Y9TenantSystemService y9TenantSystemService;

    /**
     * 根据系统唯一标识获取系统
     *
     * @param id 系统名称
     * @return {@code Y9Result<System>} 通用请求返回对象 - data 是系统对象
     * @since 9.6.2
     */
    @Override
    public Y9Result<System> getById(@RequestParam("id") @NotBlank String id) {
        return Y9Result.success(y9SystemService.getById(id));
    }

    /**
     * 根据系统名称获取系统
     *
     * @param name 系统名称
     * @return {@code Y9Result<System>} 通用请求返回对象 - data 是系统对象
     * @since 9.6.0
     */
    @Override
    public Y9Result<System> getByName(@RequestParam("name") @NotBlank String name) {
        return Y9Result.success(y9SystemService.findByName(name).orElse(null));
    }

    /**
     * 注册系统
     *
     * @param name 系统英文名称
     * @param cnName 系统名称
     * @param contextPath 系统上下文
     * @param isvGuid 租户id
     * @return {@code Y9Result<System>} 通用请求返回对象 - data 是注册的系统对象
     * @since 9.6.3
     */
    @Override
    public Y9Result<System> registrySystem(@RequestParam("name") String name, @RequestParam("cnName") String cnName,
        @RequestParam("contextPath") String contextPath, @RequestParam("isvGuid") String isvGuid) {
        List<System> systemList = y9SystemService.listByContextPath(contextPath);
        if (!systemList.isEmpty()) {
            return Y9Result.failure("该系统上下文已存在，请重新输入！");
        }
        Optional<System> y9SystemOptional = y9SystemService.findByName(name);
        if (y9SystemOptional.isPresent()) {
            return Y9Result.failure("该系统名称已存在，请重新输入！");
        }
        if (StringUtils.isBlank(isvGuid)) {
            isvGuid = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(isvGuid);

        try {
            System system = new System();
            system.setTenantId(isvGuid);
            system.setName(name);
            system.setCnName(cnName);
            system.setContextPath(contextPath);
            System savedSystem = y9SystemService.saveOrUpdate(system);

            // 自动租用
            y9TenantSystemService.saveTenantSystem(savedSystem.getId(), Y9LoginUserHolder.getTenantId());

            return Y9Result.success(PlatformModelConvertUtil.convert(savedSystem, System.class), "注册应用成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("创建失败！");
        }

    }

}
