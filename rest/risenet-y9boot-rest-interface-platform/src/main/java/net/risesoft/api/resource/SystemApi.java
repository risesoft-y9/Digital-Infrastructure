package net.risesoft.api.resource;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.System;

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
     * @return System 系统信息
     * @since 9.6.2
     */
    @GetMapping("/getById")
    System getById(@RequestParam("id") @NotBlank String id);

    /**
     * 根据系统名称获取系统
     *
     * @param name 系统名称
     * @return System 系统信息
     */
    @GetMapping("/getByName")
    System getByName(@RequestParam("name") @NotBlank String name);

}
