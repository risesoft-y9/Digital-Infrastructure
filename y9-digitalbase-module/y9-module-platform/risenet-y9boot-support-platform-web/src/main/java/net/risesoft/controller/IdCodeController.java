package net.risesoft.controller;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.IdCodeConfig;
import net.risesoft.enums.CodePayTypeEnum;
import net.risesoft.enums.CodeTypeEnum;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.interfaces.Four;
import net.risesoft.interfaces.Six;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.CodeAddress;
import net.risesoft.model.IdcodeRegResult;
import net.risesoft.model.platform.IdCode;
import net.risesoft.model.platform.org.Person;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.idcode.Y9IdCodeService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.util.Config;

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
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER})
public class IdCodeController {

    private final Y9IdCodeService y9IdCodeService;
    private final Y9PersonService y9PersonService;

    private final Environment environment;

    @PostConstruct
    public void init() {
        IdCodeConfig.init(environment.getProperty("idCode.api_code"), environment.getProperty("idCode.api_key"),
            environment.getProperty("idCode.idCode_url"), environment.getProperty("idCode.main_code"),
            environment.getProperty("idCode.analyze_url"), environment.getProperty("idCode.goto_url"),
            environment.getProperty("idCode.sample_url"));
    }

    /**
     * 为人员添加统一码
     *
     * @param personId 人员id
     * @return {@code Y9Result<Y9IdCode>}
     */
    @RiseLog(operationName = "为人员添加统一码", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/creat")
    public Y9Result<IdCode> creat(@RequestParam @NotBlank String personId) {
        Person person = y9PersonService.getById(personId);
        Optional<IdCode> idCodeOptional = y9IdCodeService.findByOrgUnitId(personId);
        if (idCodeOptional.isEmpty()) {
            IdcodeRegResult result =
                Four.m407(Config.MAIN_CODE, "73", 10127, "10000000", personId, "", person.getName(),
                    CodePayTypeEnum.REGISTER.getValue(), Config.GOTO_URL + "?tenantId=" + person.getTenantId(),
                    Config.SAMPLE_URL + "?tenantId=" + person.getTenantId());
            if (result != null && result.getResultCode() == 1) {
                IdCode idCode = new IdCode();
                idCode.setId(result.getOrganUnitIdCode());
                idCode.setOrgUnitId(personId);
                idCode.setRegId(result.getCategoryRegId());
                y9IdCodeService.save(idCode);
            } else {
                return Y9Result.failure("调用统一码407接口失败：" + (result == null ? "请检查接口地址" : result.getResultMsg()));
            }
        }
        IdCode idCode = idCodeOptional.get();
        if (StringUtils.isNotEmpty(idCode.getImgUrl())) {
            return Y9Result.failure("该人员已有统一码，请勿重复添加");
        }
        CodeAddress result = Six.m603(idCode.getId(), 300, CodeTypeEnum.QR.getValue());
        if (result.getResultCode() == 1) {
            idCode.setImgUrl(result.getAddress());
            y9IdCodeService.save(idCode);
            return Y9Result.success(idCode, "为人员添加统一码成功");
        } else {
            return Y9Result.failure("调用统一码603接口失败：" + result.getResultMsg());
        }
    }

    /**
     * 根据人员id，获取人员统一码信息
     *
     * @param personId 人员id
     * @return {@code Y9Result<Y9IdCode>}
     */
    @RiseLog(operationName = "根据人员id，获取人员统一码信息")
    @RequestMapping(value = "/getPersonById")
    public Y9Result<IdCode> getPersonById(@RequestParam @NotBlank String personId) {
        return Y9Result.success(y9IdCodeService.findByOrgUnitId(personId).orElse(null), "根据人员id，获取人员统一码信息");
    }
}
