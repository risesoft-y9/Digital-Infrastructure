package net.risesoft.controller.dictionary;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.consts.OptionClassConsts;
import net.risesoft.entity.Y9OptionValue;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.dictionary.Y9OptionValueService;

/**
 * 字典属性管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/optionValue", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class OptionValueController {

    private final Y9OptionValueService y9OptionValueService;

    /**
     * 根据字典类型，获取字典属性值列表
     *
     * @param type 字典类型
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取字典属性值列表")
    @RequestMapping(value = "/listByType")
    public Y9Result<List<Y9OptionValue>> listByType(@RequestParam("type") @NotBlank String type) {
        return Y9Result.success(y9OptionValueService.listByType(type), "获取字典属性值列表成功");
    }

    /**
     * 获取职务名称列表
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取职务名称列表")
    @RequestMapping(value = "/listDuty")
    public Y9Result<List<Y9OptionValue>> listDuty() {
        return Y9Result.success(y9OptionValueService.listByType(OptionClassConsts.DUTY), "获取职务名称列表成功");
    }

    /**
     * 获取职备级别列表
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取职备级别列表")
    @RequestMapping(value = "/listDutyLevel")
    public Y9Result<List<Y9OptionValue>> listDutyLevel() {
        return Y9Result.success(y9OptionValueService.listByType(OptionClassConsts.DUTY_LEVEL), "获取职备级别列表成功");
    }

    /**
     * 获取职务类型列表
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取职务类型列表")
    @RequestMapping(value = "/listDutyType")
    public Y9Result<List<Y9OptionValue>> listDutyType() {
        return Y9Result.success(y9OptionValueService.listByType(OptionClassConsts.DUTY_TYPE), "获取职务类型列表成功");
    }

    /**
     * 获取人员编制列表
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取人员编制列表")
    @RequestMapping(value = "/listOfficialType")
    public Y9Result<List<Y9OptionValue>> listOfficialType() {
        return Y9Result.success(y9OptionValueService.listByType(OptionClassConsts.OFFICIAL_TYPE), "获取人员编制列表成功");
    }

    /**
     * 获取组织机构类型列表
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取组织机构类型列表")
    @RequestMapping(value = "/listOrgType")
    public Y9Result<List<Y9OptionValue>> listOrgType() {
        return Y9Result.success(y9OptionValueService.listByType(OptionClassConsts.ORGANIZATION_TYPE), "获取组织机构类型列表成功");
    }

    /**
     * 获取证件类型列表
     *
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取证件类型列表")
    @RequestMapping(value = "/listPrincipalIdType")
    public Y9Result<List<Y9OptionValue>> listPrincipalIdType() {
        return Y9Result.success(y9OptionValueService.listByType(OptionClassConsts.PRINCIPAL_ID_TYPE), "获取证件类型列表成功");
    }

    /**
     * 根据id数组，删除字典数据
     *
     * @param ids id数组
     */
    @RiseLog(operationName = "删除字典数据", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removeByIds")
    public Y9Result<String> removeByIds(@RequestParam("ids") @NotEmpty String[] ids) {
        y9OptionValueService.delete(ids);
        return Y9Result.successMsg("删除字典数据成功");
    }

    /**
     * 保存新增字典数据
     *
     * @param optionValue 字典数据实体
     * @return
     */
    @RiseLog(operationName = "保存新增字典数据", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOptionValue")
    public Y9Result<Y9OptionValue> saveOptionValue(@Validated Y9OptionValue optionValue) {
        Y9OptionValue value = y9OptionValueService.saveOptionValue(optionValue);
        return Y9Result.success(value, "保存新增字典数据成功");
    }

}
