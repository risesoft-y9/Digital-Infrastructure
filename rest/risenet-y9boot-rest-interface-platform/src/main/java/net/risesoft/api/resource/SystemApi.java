package net.risesoft.api.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

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

    /**
     * 根据系统id，获取系统名称列表
     *
     * @param systemIds 系统id集合（List&lt;String&lt;）
     * @return List&lt;String&gt; 系统名称列表
     */
    @GetMapping("/listSystemNameByIds")
    List<String> listSystemNameByIds(@RequestParam("systemIds") @NotEmpty List<String> systemIds);
}
