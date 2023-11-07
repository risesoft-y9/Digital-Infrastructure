package net.risesoft.api.resource;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.System;
import net.risesoft.pojo.Y9Result;

/**
 * 系统管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface SystemApi {

    /**
     * 根据系统唯一标识获取系统
     *
     * @param id 系统名称
     * @return {@code Y9Result<System>} 通用请求返回对象 - data 是系统对象
     * @since 9.6.2
     */
    @GetMapping("/getById")
    Y9Result<System> getById(@RequestParam("id") @NotBlank String id);

    /**
     * 根据系统名称获取系统
     *
     * @param name 系统名称
     * @return {@code Y9Result<System>} 通用请求返回对象 - data 是系统对象
     * @since 9.6.0
     */
    @GetMapping("/getByName")
    Y9Result<System> getByName(@RequestParam("name") @NotBlank String name);

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
    @PostMapping("/registrySystem")
    Y9Result<System> registrySystem(@RequestParam("name") String name, @RequestParam("cnName") String cnName,
        @RequestParam("contextPath") String contextPath, @RequestParam("isvGuid") String isvGuid);

}
