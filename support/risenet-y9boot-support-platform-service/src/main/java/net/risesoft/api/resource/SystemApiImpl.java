package net.risesoft.api.resource;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.System;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9System;
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
@RequestMapping(value = "/services/rest/system", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SystemApiImpl implements SystemApi {

    private final Y9SystemService y9SystemService;

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

}
