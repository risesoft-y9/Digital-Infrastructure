package net.risesoft.controller.org;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.Y9Group;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.Y9GroupService;
import net.risesoft.service.relation.Y9PersonsToGroupsService;

/**
 * 用户组管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/group", produces = "application/json")
@RequiredArgsConstructor
public class GroupController {

    private final Y9PersonsToGroupsService y9PersonsToGroupsService;
    private final Y9GroupService y9GroupService;

    /**
     * 为用户组添加人员
     *
     * @param groupId 用户组id
     * @param personIds 人员ids
     * @return
     */
    @RiseLog(operationName = "用户组添加人员", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/addPersons")
    public Y9Result<Object> addPersons(@RequestParam String groupId, @RequestParam String[] personIds) {
        y9PersonsToGroupsService.addPersons(groupId, personIds);
        return Y9Result.successMsg("用户组添加人员成功");
    }

    /**
     * 根据用户组id，获取扩展属性
     *
     * @param groupId 用户组id
     * @return
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam String groupId) {
        String properties = y9GroupService.getById(groupId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功");
    }

    /**
     * 根据用户组id，获取用户组信息
     *
     * @param groupId 用户组id
     * @return
     */
    @RequestMapping(value = "/getGroupById")
    public Y9Result<Y9Group> getGroupById(@RequestParam String groupId) {
        return Y9Result.success(y9GroupService.getById(groupId), "获取用户组信息成功");
    }

    /**
     * 根据人员id，获取用户组列表
     *
     * @param personId 人员id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据人员id，获取用户组列表")
    @RequestMapping(value = "/listGroupsByPersonId")
    public Y9Result<List<Y9Group>> listGroupsByPersonId(@RequestParam String personId) {
        return Y9Result.success(y9GroupService.listByPersonId(personId), "根据人员id，获取用户组列表");
    }

    /**
     * 移动用户组到新的节点
     *
     * @param groupId 用户组id
     * @param parentId 父节点id
     * @return
     */
    @RiseLog(operationName = "移动用户组", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/move")
    public Y9Result<Y9Group> move(@RequestParam String groupId, @RequestParam String parentId) {
        Y9Group y9Group = y9GroupService.move(groupId, parentId);
        return Y9Result.success(y9Group, "移动用户组成功");
    }

    /**
     * 删除用户组
     *
     * @param groupId 用户组id
     * @return
     */
    @RiseLog(operationName = "删除用户组", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String groupId) {
        y9GroupService.delete(groupId);
        return Y9Result.successMsg("删除用户组成功");
    }

    /**
     * 批量用户组移除人员
     *
     * @param groupId 用户组id
     * @param personIds 人员ids
     * @return
     */
    @RiseLog(operationName = "用户组移除人员", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/removePersons")
    public Y9Result<String> removePersons(@RequestParam String groupId, @RequestParam String[] personIds) {
        y9PersonsToGroupsService.removePersons(groupId, personIds);
        return Y9Result.successMsg("用户组移除人员成功");
    }

    /**
     * 保存扩展属性(直接覆盖)
     *
     * @param groupId 用户组id
     * @param properties 扩展属性
     */
    @RiseLog(operationName = "新增扩展属性", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam String groupId, @RequestParam String properties) {
        Y9Group y9Group = y9GroupService.saveProperties(groupId, properties);
        return Y9Result.success(y9Group.getProperties(), "新增扩展属性成功");
    }

    /**
     * 保存新的序号
     *
     * @param groupIds 用户组ids
     */
    @RiseLog(operationName = "保存用户组排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam(value = "groupIds") List<String> groupIds) {
        y9GroupService.saveOrder(groupIds);
        return Y9Result.successMsg("保存用户组排序成功");
    }

    /**
     * 保存人员的用户组排序
     *
     * @param personId 人员id
     * @param groupIds 用户组ids
     * @return
     */
    @RiseLog(operationName = "保存人员的用户组排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/orderGroups")
    public Y9Result<String> saveOrderGroups(@RequestParam String personId, @RequestParam String[] groupIds) {
        y9PersonsToGroupsService.orderGroups(personId, groupIds);
        return Y9Result.successMsg("保存人员的用户组排序成功");
    }

    /**
     * 保存用户组的人员排序
     *
     * @param groupId 用户组id
     * @param personIds 成员ids
     * @return
     */
    @RiseLog(operationName = "保存用户组的人员排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/orderPersons")
    public Y9Result<String> saveOrderPersons(@RequestParam String groupId, @RequestParam String[] personIds) {
        y9PersonsToGroupsService.orderPersons(groupId, personIds);
        return Y9Result.successMsg("保存用户组的人员排序成功");
    }

    /**
     * 新建或者更新用户组信息
     *
     * @param group 用户组实体
     * @return
     */
    @RiseLog(operationName = "新建或者更新用户组信息", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Group> saveOrUpdate(@Validated Y9Group group) {
        Y9Group returnGroup = y9GroupService.saveOrUpdate(group);
        return Y9Result.success(returnGroup, "新建或者更新用户组信息成功");
    }

}
