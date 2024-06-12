package net.risesoft.controller.idcode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.entity.Y9Person;
import net.risesoft.entity.idcode.Y9IdCode;
import net.risesoft.enums.CodePayTypeEnum;
import net.risesoft.enums.CodeTypeEnum;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.interfaces.Four;
import net.risesoft.interfaces.Six;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.CodeAddress;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.idcode.Y9IdCodeService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.ConfigReader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

/**
 * 统一码管理
 *
 * @author qinman
 * @date 2024/06/12
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/idCode", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@IsManager({ManagerLevelEnum.SYSTEM_MANAGER})
public class IdCodeController {

    private final Y9IdCodeService y9IdCodeService;
    private final Y9PersonService y9PersonService;

    /**
     * 为人员添加统一码
     *
     * @param personId 人员id
     * @return Y9Result<Y9IdCode>
     */
    @RiseLog(operationName = "为人员添加统一码", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/creat")
    public Y9Result<Y9IdCode> creat(@RequestParam @NotBlank String personId) {
        Y9Person y9Person = y9PersonService.getById(personId);
        if (y9Person == null) {
            return Y9Result.failure("人员不存在");
        }
        Y9IdCode y9IdCode = y9IdCodeService.findByOrgUnitId(personId);
        if (y9IdCode == null) {
            IdcodeRegResult result = Four.m407(ConfigReader.MAIN_CODE, "73", 10127, "10000000", personId, "", y9Person.getName(), CodePayTypeEnum.REGISTER.getValue(), ConfigReader.GOTO_URL, ConfigReader.SAMPLE_URL);
            if (result.getResultCode() == 1) {
                y9IdCode = new Y9IdCode();
                y9IdCode.setId(result.getOrganUnitIdCode());
                y9IdCode.setOrgUnitId(personId);
                y9IdCode.setRegId(result.getCategoryRegId());
                y9IdCodeService.save(y9IdCode);
            } else {
                return Y9Result.failure("调用统一码407接口失败：" + result.getResultMsg());
            }
        }
        if (StringUtils.isNotEmpty(y9IdCode.getImgUrl())) {
            return Y9Result.failure("该人员已有统一码，请勿重复添加");
        }
        CodeAddress result = Six.m603(y9IdCode.getId(), 300, CodeTypeEnum.QR.getValue());
        if (result.getResultCode() == 1) {
            y9IdCode.setImgUrl(result.getAddress());
            y9IdCodeService.save(y9IdCode);
            return Y9Result.success(y9IdCode, "为人员添加统一码成功");
        } else {
            return Y9Result.failure("调用统一码603接口失败：" + result.getResultMsg());
        }
    }

    /**
     * 根据人员id，获取人员统一码信息
     *
     * @param personId 人员id
     * @return Y9Result<Y9IdCode>
     */
    @RiseLog(operationName = "根据人员id，获取人员统一码信息")
    @RequestMapping(value = "/getPersonById")
    public Y9Result<Y9IdCode> getPersonById(@RequestParam @NotBlank String personId) {
        return Y9Result.success(y9IdCodeService.findByOrgUnitId(personId), "根据人员id，获取人员统一码信息");
    }
}
