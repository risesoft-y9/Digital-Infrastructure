package net.risesoft.api.idcode;

import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.platform.IdCode;
import net.risesoft.model.platform.org.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.idcode.Y9IdCodeService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;

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
     * @param code 人员统一码
     * @return {@code Y9Result<Person>} 通用请求返回对象 - data 是人员对象
     * @since 9.6.6
     */
    @GetMapping("/getPerson")
    public Y9Result<Person> getPerson(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("code") @NotBlank String code) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Optional<IdCode> idCodeOptional = y9IdCodeService.findById(code);
        if (idCodeOptional.isEmpty()) {
            return Y9Result.failure("统一码不存在");
        }
        return Y9Result.success(y9PersonService.getById(idCodeOptional.get().getOrgUnitId()), "根据人员id，获取人员统一码信息成功");
    }
}
