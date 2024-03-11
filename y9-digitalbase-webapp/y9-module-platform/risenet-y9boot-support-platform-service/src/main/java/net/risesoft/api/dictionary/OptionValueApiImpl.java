package net.risesoft.api.dictionary;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.dictionary.OptionValueApi;
import net.risesoft.entity.Y9OptionValue;
import net.risesoft.model.platform.OptionValue;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.dictionary.Y9OptionValueService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 字典表管理组件
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
@RequestMapping(value = "/services/rest/v1/optionValue", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OptionValueApiImpl implements OptionValueApi {

    private final Y9OptionValueService optionValueService;

    /**
     * 根据字典类型查找字典数据列表
     *
     * @param tenantId 租户id
     * @param type 类型
     * @return {@code Y9Result<List<OptionValue>>} 通用请求返回对象 - data 是查找的字典数据列表
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<OptionValue>> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("type") @NotBlank String type) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OptionValue> y9OptionValueList = optionValueService.listByType(type);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9OptionValueList, OptionValue.class));
    }
}
