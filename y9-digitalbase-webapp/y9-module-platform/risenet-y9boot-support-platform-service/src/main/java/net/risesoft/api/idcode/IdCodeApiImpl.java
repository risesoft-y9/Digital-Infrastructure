package net.risesoft.api.idcode;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.idcode.Y9IdCode;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.idcode.Y9IdCodeService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 统一码服务组件
 *
 * @author qinman
 * @date 2024/6/12
 * @since 9.6.6
 */
@Primary
@Validated
@RestController
@RequestMapping(value = "/services/rest/v1/idCode", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class IdCodeApiImpl {

    private final Y9PersonService y9PersonService;
    private final Y9IdCodeService y9IdCodeService;


    /**
     * 根据统一码，获取人员信息
     *
     * @param tenantId 租户id
     * @param code     人员统一码
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.0
     */
    @GetMapping("/getPerson")
    public Y9Result<Person> getPerson(@RequestParam("tenantId") @NotBlank String tenantId,
                                      @RequestParam("code") @NotBlank String code) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9IdCode y9IdCode = y9IdCodeService.findById(code);
        if (y9IdCode == null) {
            return Y9Result.failure("统一码不存在");
        }
        Y9Person y9Person = y9PersonService.getById(y9IdCode.getOrgUnitId());
        if (y9Person == null) {
            return Y9Result.failure("人员不存在或者已删除");
        }
        y9Person.setPassword(null);
        return Y9Result.success(Y9ModelConvertUtil.convert(y9Person, Person.class), "根据人员id，获取人员统一码信息成功");
    }
}
