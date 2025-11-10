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

import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.dictionary.OptionClass;
import net.risesoft.permission.annotation.IsAnyManager;
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
@IsAnyManager(ManagerLevelEnum.SYSTEM_MANAGER)
public class OptionClassController {

    private final Y9OptionClassService y9OptionClassService;

    /**
     * 获取字典类型列表
     *
     * @return {@code Y9Result<List<OptionClass>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取字典类型列表")
    @RequestMapping(value = "/listOptionClass")
    public Y9Result<List<OptionClass>> listOptionClass() {
        return Y9Result.success(y9OptionClassService.list(), "获取字典类型列表成功");
    }

    /**
     * 根据type删除字典类型，以及该类型对应的数据列表
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
     * 新增或修改字典类型
     *
     * @param optionClass 字典类型实体
     * @return {@code Y9Result<OptionClass>}
     */
    @RiseLog(operationName = "新增或修改字典类型", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOptionClass")
    public Y9Result<OptionClass> saveOptionClass(@Validated OptionClass optionClass) {
        return Y9Result.success(y9OptionClassService.saveOptionClass(optionClass), "新增字典类型成功");
    }
}
