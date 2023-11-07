package net.risesoft.api.log;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 个人常用应用组件
 *
 * @author mengjuhua
 * @date 2022/10/19
 * @since 9.6.0
 */
@Validated
public interface CommonAppForPersonApi {

    /**
     * 根据用户id，获取常用应用名称列表
     *
     * @param tenantId 租户id
     * @param personId 人员id
     * @return {@code Y9Result<List<String>>} 通用请求返回对象 - data 是常用应用名称列表
     * @since 9.6.0
     */
    @GetMapping(value = "/getAppNames")
    Y9Result<List<String>> getAppNamesByPersonId(@RequestParam("tenantId") String tenantId,
        @RequestParam("personId") @NotBlank String personId);

}
