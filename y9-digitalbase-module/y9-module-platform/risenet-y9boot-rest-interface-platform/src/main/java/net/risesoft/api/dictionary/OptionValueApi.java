package net.risesoft.api.dictionary;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.platform.OptionValue;
import net.risesoft.pojo.Y9Result;

/**
 * 字典表管理组件
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 * @since 9.6.0
 */
@Validated
public interface OptionValueApi {

    /**
     * 根据字典类型查找字典数据列表
     *
     * @param tenantId 租户id
     * @param type 类型
     * @return {@code Y9Result<List<OptionValue>>} 通用请求返回对象 - data 是查找的字典数据列表
     * @since 9.6.0
     */
    @GetMapping("/listByType")
    Y9Result<List<OptionValue>> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("type") @NotBlank String type);
}
