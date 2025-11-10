package net.risesoft.controller.org;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

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
import net.risesoft.model.platform.org.PersonsPositions;
import net.risesoft.model.platform.org.Position;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PersonsToPositionsService;

/**
 * 岗位管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/position", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER})
public class PositionController {

    private final Y9PersonsToPositionsService y9PersonsToPositionsService;
    private final Y9PositionService y9PositionService;

    /**
     * 为岗位添加人员
     *
     * @param positionId 岗位id
     * @param personIds 人员id数组
     * @return {@code Y9Result<List<Y9PersonsToPositions>>}
     */
    @RiseLog(operationName = "为岗位添加人员", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addPersons")
    public Y9Result<List<PersonsPositions>> addPersons(@RequestParam @NotBlank String positionId,
        @RequestParam @NotEmpty String[] personIds) {
        return Y9Result.success(y9PersonsToPositionsService.addPersons(positionId, personIds), "为岗位添加人员成功");
    }

    /**
     * 根据id，改变岗位禁用状态
     *
     * @param id 岗位id
     * @return {@code Y9Result<Position>}
     */
    @RiseLog(operationName = "禁用岗位", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/changeDisabled")
    public Y9Result<Position> changeDisabled(@NotBlank @RequestParam String id) {
        return Y9Result.success(y9PositionService.changeDisabled(id), "岗位禁用状态修改成功");
    }

    /**
     * 根据岗位id，获取岗位的扩展属性
     *
     * @param positionId 岗位id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam @NotBlank String positionId) {
        String properties = y9PositionService.getById(positionId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功");
    }

    /**
     * 根据岗位id，获取岗位信息
     *
     * @param positionId 岗位id
     * @return {@code Y9Result<Position>}
     */
    @RiseLog(operationName = "根据岗位id，获取岗位信息信息")
    @RequestMapping(value = "/getPositionById")
    public Y9Result<Position> getPositionById(@RequestParam @NotBlank String positionId) {
        return Y9Result.success(y9PositionService.getById(positionId), "根据岗位id，获取岗位信息成功");
    }

    /**
     * 根据父节点id，获取岗位列表
     *
     * @param parentId 父节点id
     * @return {@code Y9Result<List<Position>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据父节点id，获取岗位列表")
    @RequestMapping(value = "/listPositionsByParentId")
    public Y9Result<List<Position>> listPositionsByParentId(@RequestParam @NotBlank String parentId) {
        return Y9Result.success(y9PositionService.listByParentId(parentId, null), "根据父节点id，获取岗位列表成功");
    }

    /**
     * 根据人员id，获取岗位列表
     *
     * @param personId 人员id
     * @return {@code Y9Result<List<Position>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据人员id，获取岗位列表")
    @RequestMapping(value = "/listPositionsByPersonId")
    public Y9Result<List<Position>> listPositionsByPersonId(@RequestParam @NotBlank String personId) {
        return Y9Result.success(y9PositionService.listByPersonId(personId, null), "根据人员id，获取岗位列表成功");
    }

    /**
     * 移动岗位到新的节点
     *
     * @param positionId 岗位id
     * @param parentId 目标父节点id
     * @return {@code Y9Result<Position>}
     */
    @RiseLog(operationName = "移动岗位", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/move")
    public Y9Result<Position> move(@RequestParam @NotBlank String positionId, @RequestParam @NotBlank String parentId) {
        Position position = y9PositionService.move(positionId, parentId);
        return Y9Result.success(position, "移动岗位成功");
    }

    /**
     * 删除岗位
     *
     * @param ids 岗位id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "删除岗位", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam(value = "ids") @NotEmpty List<String> ids) {
        y9PositionService.delete(ids);
        return Y9Result.successMsg("删除岗位成功");
    }

    /**
     * 从岗位移除人员
     *
     * @param positionId 岗位id
     * @param personIds 人员id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "移除岗位的人员", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removePersons")
    public Y9Result<String> removePersons(@RequestParam @NotBlank String positionId,
        @RequestParam @NotEmpty String[] personIds) {
        y9PersonsToPositionsService.deletePersons(positionId, personIds);
        return Y9Result.successMsg("移除岗位的人员成功");
    }

    /**
     * 保存扩展属性(直接覆盖)
     *
     * @param positionId 岗位id
     * @param properties 扩展属性
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存扩展属性", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam @NotBlank String positionId,
        @RequestParam String properties) {
        Position position = y9PositionService.saveProperties(positionId, properties);
        return Y9Result.success(position.getProperties(), "保存扩展属性成成功");
    }

    /**
     * 新建或者更新岗位信息
     *
     * @param position 岗位实体
     * @return {@code Y9Result<Position>}
     */
    @RiseLog(operationName = "新建或者更新岗位信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Position> saveOrUpdate(@Validated Position position) {
        return Y9Result.success(y9PositionService.saveOrUpdate(position), "保存岗位信息成功");
    }

    /**
     * 保存新的序号
     *
     * @param positionIds 岗位ids
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存岗位排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam(value = "positionIds") @NotEmpty List<String> positionIds) {
        y9PositionService.saveOrder(positionIds);
        return Y9Result.successMsg("保存岗位排序成功");
    }

    /**
     * 保存岗位的人员排序
     *
     * @param positionId 岗位id
     * @param personIds 人员id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存岗位的人员排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/orderPersons")
    public Y9Result<String> saveOrderPersons(@RequestParam @NotBlank String positionId,
        @RequestParam @NotEmpty String[] personIds) {
        y9PersonsToPositionsService.orderPersons(positionId, personIds);
        return Y9Result.successMsg("保存岗位的人员排序成功");
    }

    /**
     * 保存人员的岗位排序
     *
     * @param personId 人员id
     * @param positionIds 岗位id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存人员的岗位排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/orderPositions")
    public Y9Result<String> saveOrderPositions(@RequestParam @NotBlank String personId,
        @RequestParam @NotEmpty String[] positionIds) {
        y9PersonsToPositionsService.orderPositions(personId, positionIds);
        return Y9Result.successMsg("保存人员的岗位排序成功");
    }
}
