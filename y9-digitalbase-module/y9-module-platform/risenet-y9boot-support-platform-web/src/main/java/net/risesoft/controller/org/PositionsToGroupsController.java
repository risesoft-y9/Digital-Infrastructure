package net.risesoft.controller.org;

import java.util.ArrayList;
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

import net.risesoft.entity.org.Y9Position;
import net.risesoft.entity.relation.Y9PositionsToGroups;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9PositionService;
import net.risesoft.service.relation.Y9PositionsToGroupsService;

/**
 * 岗位用户组关联管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/groupPosition", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER})
public class PositionsToGroupsController {

    private final Y9PositionService y9PositionService;
    private final Y9PositionsToGroupsService y9PositionsToGroupsService;

    /**
     * 批量添加用户组的岗位
     *
     * @param groupId 用户组id
     * @param positionIds 岗位ids
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "批量添加用户组的岗位", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addPositions")
    public Y9Result<Object> addPositions(@RequestParam @NotBlank String groupId,
        @RequestParam @NotEmpty String[] positionIds) {
        y9PositionsToGroupsService.saveGroupPosition(groupId, positionIds);
        return Y9Result.successMsg("添加岗位成功");
    }

    /**
     * 获取岗位关联列表
     *
     * @param groupId 用户组id
     * @return {@code Y9Result<List<Y9Position>>}
     * @since 9.6.1
     */
    @RiseLog(operationName = "获取组岗位列表")
    @RequestMapping(value = "/listPositionsByGroupId")
    public Y9Result<List<Y9Position>> listPositionsByGroupId(@RequestParam @NotBlank String groupId) {
        List<Y9PositionsToGroups> list = y9PositionsToGroupsService.listByGroupId(groupId);
        List<Y9Position> positionList = new ArrayList<>();
        if (null != list && !list.isEmpty()) {
            for (Y9PositionsToGroups groupPosition : list) {
                Y9Position y9Position = y9PositionService.getById(groupPosition.getPositionId());
                positionList.add(y9Position);
            }
        }
        return Y9Result.success(positionList, "获取岗位关联列表成功");
    }

    /**
     * 批量移除用户组的岗位
     *
     * @param groupId 用户组id
     * @param positionIds 岗位ids
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "批量移除用户组的岗位", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removePositions")
    public Y9Result<String> removePositions(@RequestParam @NotBlank String groupId,
        @RequestParam @NotEmpty String[] positionIds) {
        y9PositionsToGroupsService.removePositions(groupId, positionIds);
        return Y9Result.successMsg("移除岗位关联成功");
    }

}
