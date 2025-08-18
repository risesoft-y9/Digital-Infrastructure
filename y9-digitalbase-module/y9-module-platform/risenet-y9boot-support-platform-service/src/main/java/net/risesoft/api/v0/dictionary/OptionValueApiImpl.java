package net.risesoft.api.v0.dictionary;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.v0.dictionary.OptionValueApi;
import net.risesoft.entity.dictionary.Y9OptionValue;
import net.risesoft.model.platform.dictionary.OptionValue;
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
@RestController(value = "v0OptionValueApiImpl")
@RequestMapping(value = "/services/rest/optionValue", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class OptionValueApiImpl implements OptionValueApi {

    private final Y9OptionValueService optionValueService;

    /**
     * 根据类型查找
     *
     * @param tenantId 租户id
     * @param type 类型
     * @return 字典数据列表
     * @since 9.6.0
     */
    @Override
    public List<OptionValue> listByType(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("type") @NotBlank String type) {
        Y9LoginUserHolder.setTenantId(tenantId);

        List<Y9OptionValue> y9OptionValueList = optionValueService.listByType(type);
        return Y9ModelConvertUtil.convert(y9OptionValueList, OptionValue.class);
    }
}
