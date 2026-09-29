package net.risesoft.api.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.resource.SystemApi;
import net.risesoft.dataio.resource.SystemDataHandler;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.SystemJsonModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9public.service.resource.Y9SystemService;

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
@Slf4j
public class SystemApiImpl implements SystemApi {

    private final Y9SystemService y9SystemService;

    private final SystemDataHandler systemDataHandler;

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
     * @return {@code Y9Result<System>} 通用请求返回对象 - data 是注册的系统对象
     * @since 9.6.3
     */
    @Override
    public Y9Result<System> registrySystem(@RequestParam("name") String name, @RequestParam("cnName") String cnName,
        @RequestParam("contextPath") String contextPath) {
        System system = new System();
        system.setName(name);
        system.setCnName(cnName);
        system.setContextPath(contextPath);
        System savedSystem = y9SystemService.saveAndRegister4Tenant(system);

        return Y9Result.success(PlatformModelConvertUtil.convert(savedSystem, System.class), "注册应用成功！");
    }

    /**
     * 系统、资源、角色注册。
     *
     * @param systemJsonModel 系统 JSON 模型
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.10
     */
    @Override
    public Y9Result<Object> register(@RequestBody @Valid SystemJsonModel systemJsonModel) {
        systemDataHandler.importSystem(systemJsonModel);

        return Y9Result.success();
    }

}
