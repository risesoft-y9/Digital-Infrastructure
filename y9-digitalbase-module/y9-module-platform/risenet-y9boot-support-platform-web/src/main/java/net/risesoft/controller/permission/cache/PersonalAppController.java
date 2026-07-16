package net.risesoft.controller.permission.cache;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.permission.cache.Y9PersonalAppService;

/**
 * 人员、岗位的应用
 *
 * @author shidaobang
 * @date 2026/05/20
 */
@RestController
@RequestMapping(value = "/api/rest/personalApp", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager(value = {ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER})
public class PersonalAppController {

    private final Y9PersonalAppService y9PersonalAppService;

    /**
     * 更新部门图标
     *
     * @param deptId 部门id
     * @return
     */
    @RiseLog(operationName = "更新部门图标", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/sync4Dept")
    public Y9Result<Object> sync4Dept(@NotBlank String deptId) {
        y9PersonalAppService.buildDeptPersonalAppForPosition(deptId);
        return Y9Result.successMsg("更新部门图标成功!");
    }

    /**
     * 更新个人图标
     *
     * @param personId 人员id
     * @return
     */
    @RiseLog(operationName = "更新个人图标", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/sync4Person")
    public Y9Result<Object> sync4Person(@NotBlank String personId) {
        y9PersonalAppService.buildPersonalAppForPerson(personId);
        return Y9Result.successMsg("更新人员图标成功!");
    }

    /**
     * 更新岗位图标
     *
     * @param positionId 岗位id
     * @return
     */
    @RiseLog(operationName = "更新岗位图标", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/sync4Position")
    public Y9Result<Object> sync4Position(@NotBlank String positionId) {
        y9PersonalAppService.buildPersonalAppForPosition(positionId);
        return Y9Result.successMsg("更新岗位图标成功!");
    }

}
