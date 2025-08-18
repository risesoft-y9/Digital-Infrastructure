package net.risesoft.api.v0.resource;

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

import net.risesoft.api.platform.v0.resource.SystemApi;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.model.platform.resource.System;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9System;
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
        Y9System y9System = y9SystemService.getById(id);
        return Y9ModelConvertUtil.convert(y9System, System.class);
    }

    /**
     * 根据系统名获取系统
     *
     * @param name 系统名称
     * @return System 系统
     */
    @Override
    public System getByName(@RequestParam("name") @NotBlank String name) {
        Y9System y9System = y9SystemService.findByName(name).orElse(null);
        return Y9ModelConvertUtil.convert(y9System, System.class);
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
        List<Y9System> y9Systems = y9SystemService.listByContextPath(contextPath);
        if (!y9Systems.isEmpty()) {
            return Y9Result.failure("该系统上下文已存在，请重新输入！");
        }
        Optional<Y9System> y9SystemOptional = y9SystemService.findByName(name);
        if (y9SystemOptional.isPresent()) {
            return Y9Result.failure("该系统名称已存在，请重新输入！");
        }
        if (StringUtils.isBlank(isvGuid)) {
            isvGuid = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(isvGuid);

        try {
            Y9System y9System = new Y9System();
            y9System.setTenantId(isvGuid);
            y9System.setName(name);
            y9System.setCnName(cnName);
            y9System.setContextPath(contextPath);
            Y9System system = y9SystemService.saveOrUpdate(y9System);

            // 自动租用
            y9TenantSystemService.saveTenantSystem(system.getId(), Y9LoginUserHolder.getTenantId());

            return Y9Result.success(Y9ModelConvertUtil.convert(system, System.class), "注册应用成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("创建失败！");
        }

    }

}
