package net.risesoft.controller.dictionary;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9OptionClass;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.dictionary.Y9OptionClassService;

/**
 * 字曲类型管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/optionClass", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class OptionClassController {

    private final Y9OptionClassService y9OptionClassService;

    /**
     * 获取字曲类型列表
     *
     * @return {@code Y9Result<List<Y9OptionClass>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取字典类型列表")
    @RequestMapping(value = "/listOptionClass")
    public Y9Result<List<Y9OptionClass>> listOptionClass() {
        List<Y9OptionClass> list = y9OptionClassService.list();
        return Y9Result.success(list, "获取字典类型列表成功");
    }

    /**
     * 根据type删除字典类型，以及该类型对应的数据列表， type
     *
     * @param type 类型名称
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "删除字典类型", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam("type") @NotBlank String type) {
        y9OptionClassService.deleteByType(type);
        return Y9Result.successMsg("删除字典类型成功");
    }

    /**
     * 保存新增字典类型
     *
     * @param optionClass 字典类型实体
     * @return {@code Y9Result<Y9OptionClass>}
     */
    @RiseLog(operationName = "新增字典类型", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOptionClass")
    public Y9Result<Y9OptionClass> saveOptionClass(@Validated Y9OptionClass optionClass) {
        Y9OptionClass y9OptionClass = y9OptionClassService.saveOptionClass(optionClass);
        return Y9Result.success(y9OptionClass, "新增字典类型成功");
    }
}
