package net.risesoft.controller.org;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.org.vo.OrgTreeNodeVO;
import net.risesoft.entity.Y9Department;
import net.risesoft.entity.Y9OrgBase;
import net.risesoft.entity.Y9Organization;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.enums.platform.TreeTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.org.CompositeOrgBaseService;
import net.risesoft.service.org.Y9DepartmentService;
import net.risesoft.service.org.Y9OrganizationService;
import net.risesoft.service.org.Y9PersonService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;

/**
 * 组织机构管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@Validated
@RestController
@RequestMapping(value = "/api/rest/org", produces = "application/json")
@RequiredArgsConstructor
public class OrgController {

    private final Y9DepartmentService y9DepartmentService;
    private final CompositeOrgBaseService compositeOrgBaseService;
    private final Y9OrganizationService y9OrganizationService;
    private final Y9PersonService y9PersonService;

    /**
     * 根据组织机构id，获取扩展属性
     *
     * @param orgId 组织机构id
     * @return
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam @NotBlank String orgId) {
        String properties = y9OrganizationService.getById(orgId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功！");
    }

    /**
     * 根据组织机构id，获取组织机构信息
     *
     * @param orgId 组织机构id
     * @return
     */
    @RiseLog(operationName = "根据组织机构id，获取组织机构信息")
    @RequestMapping(value = "/getOrganizationById")
    public Y9Result<Organization> getOrganizationById(@NotBlank @RequestParam String orgId) {
        return Y9Result.success(Y9ModelConvertUtil.convert(y9OrganizationService.getById(orgId), Organization.class),
            "根据组织机构id，获取组织机构信息成功！");
    }

    /**
     * 根据组织机构实体guidPath和禁用/未禁用状态查找所有人员数量
     *
     * @param id 节点id
     * @param orgType 组织类型
     * @return
     */
    @RiseLog(operationName = "根据guidPath和禁用/未禁用状态查找部门下人员总数")
    @RequestMapping(value = "/getAllPersonsCount")
    public Y9Result<Long> getPersonsCountByDisabled(@RequestParam @NotBlank String id,
        @RequestParam OrgTypeEnum orgType) {
        long count = 0;
        if (orgType.equals(OrgTypeEnum.ORGANIZATION)) {
            Y9Organization org = y9OrganizationService.getById(id);
            count = y9PersonService.countByGuidPathLikeAndDisabledAndDeletedFalse(org.getId());
        } else if (orgType.equals(OrgTypeEnum.DEPARTMENT)) {
            Y9Department dept = y9DepartmentService.getById(id);
            if (StringUtils.isNotBlank(dept.getGuidPath())) {
                count = y9PersonService.countByGuidPathLikeAndDisabledAndDeletedFalse(dept.getGuidPath());
            } else {
                count = y9PersonService.countByGuidPathLikeAndDisabledAndDeletedFalse(dept.getId());
            }
        }
        return Y9Result.success(count, "根据guidPath和禁用/未禁用状态查找部门下人员总数完成");
    }

    /**
     * 获取机构树子节点
     *
     * @param id 父节点id
     * @param treeType
     *            树类型：tree_type_org，tree_type_bureau，tree_type_dept，tree_type_group，tree_type_position，tree_type_person
     * @param disabled false为不显示禁用人员，true为显示禁用人员
     * @return
     */
    @RiseLog(operationName = "获取机构树子节点")
    @RequestMapping(value = "/getTree")
    @Deprecated
    public Y9Result<List<Y9OrgBase>> getTree(@RequestParam @NotBlank String id, @RequestParam TreeTypeEnum treeType,
        boolean disabled) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        List<Y9OrgBase> treeList = new ArrayList<>();
        if (userInfo.isGlobalManager() && !ManagerLevelEnum.GENERAL_USER.equals(userInfo.getManagerLevel())) {
            treeList = compositeOrgBaseService.getTree(id, treeType, disabled);
        } else if (!ManagerLevelEnum.GENERAL_USER.equals(userInfo.getManagerLevel())) {
            treeList = compositeOrgBaseService.getTree4DeptManager(id, treeType);
        }
        return Y9Result.success(treeList, "获取机构树成功！");
    }

    /**
     * 获取组织架构列表
     *
     * @param virtual 是否为虚拟组织
     * @return
     */
    @RiseLog(operationName = "获取组织架构列表")
    @RequestMapping(value = "/list")
    @Deprecated
    public Y9Result<List<Organization>> list(@RequestParam(required = false) boolean virtual) {
        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            return Y9Result.success(Y9ModelConvertUtil.convert(y9OrganizationService.list(virtual), Organization.class),
                "获取组织架构列表成功！");
        } else {
            List<Y9Organization> orgList = y9OrganizationService.list(false);
            Y9Department managerDept = y9DepartmentService.getById(Y9LoginUserHolder.getUserInfo().getParentId());
            String mapping = managerDept.getGuidPath();
            List<Y9Organization> authOrgList =
                orgList.stream().filter(org -> mapping.contains(org.getGuidPath())).collect(Collectors.toList());
            return Y9Result.success(Y9ModelConvertUtil.convert(authOrgList, Organization.class), "获取组织架构列表成功！");
        }
    }

    /**
     * 根据主键id删除机构实例
     *
     * @param orgId 组织机构id
     * @return
     */
    @RiseLog(operationName = "删除机构", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam @NotBlank String orgId) {
        y9OrganizationService.delete(orgId);
        return Y9Result.success(null, "删除组织机构成功！");
    }

    /**
     * 保存扩展属性(直接覆盖)
     *
     * @param orgId 组织id
     * @param properties 扩展属性
     */
    @RiseLog(operationName = "新增扩展属性", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam @NotBlank String orgId,
        @RequestParam String properties) {
        Y9Organization orgOrg = y9OrganizationService.saveProperties(orgId, properties);
        return Y9Result.success(orgOrg.getProperties(), "保存扩展属性成成功！");
    }

    /**
     * 对组织机构按id顺序排序
     *
     * @param orgIds 组织机构ids
     * @return
     */
    @RiseLog(operationName = "对组织机构按id顺序排序", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam(value = "orgIds") @NotEmpty List<String> orgIds) {
        y9OrganizationService.saveOrder(orgIds);
        return Y9Result.success(null, "保存机构排序成功！");
    }

    /**
     * 新建或者更新组织机构
     *
     * @param org 组织机构实体
     * @return
     */
    @RiseLog(operationName = "新建或者更新组织机构", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Organization> saveOrUpdate(@Validated Y9Organization org) {
        Y9Organization returnOrg = y9OrganizationService.saveOrUpdate(org);
        return Y9Result.success(Y9ModelConvertUtil.convert(returnOrg, Organization.class), "新建或者更新组织机构成功！");
    }

    /**
     * 发送同步数据事件让订阅了相应事件的系统做组织架构的同步
     *
     * @param syncId 同步节点id
     * @param orgType 组织类型
     * @param needRecursion 是否递归
     * @param targetSystemName 目标系统名称
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "同步数据", operationType = OperationTypeEnum.ADD)
    @PostMapping(value = "/sync")
    public Y9Result<String> sync(@RequestParam @NotBlank String syncId, @RequestParam OrgTypeEnum orgType,
        @RequestParam boolean needRecursion, String targetSystemName) {
        compositeOrgBaseService.sync(syncId, orgType, needRecursion, targetSystemName);
        return Y9Result.success(null, "发送同步数据事件完成");
    }

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name 名称
     * @param treeType
     *            树类型：tree_type_org，tree_type_bureau，tree_type_dept，tree_type_group，tree_type_position，tree_type_person
     * @return
     */
    @RiseLog(operationName = "查询机构主体")
    @RequestMapping(value = "/treeSearch")
    @Deprecated
    public Y9Result<List<Y9OrgBase>> treeSearch(@RequestParam String name, @RequestParam TreeTypeEnum treeType) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        List<Y9OrgBase> treeList = new ArrayList<>();
        if (userInfo.isGlobalManager()) {
            treeList = compositeOrgBaseService.treeSearch(name, treeType);
        } else if (ManagerLevelEnum.SYSTEM_MANAGER.equals(userInfo.getManagerLevel())
            || ManagerLevelEnum.SECURITY_MANAGER.equals(userInfo.getManagerLevel())) {
            treeList = compositeOrgBaseService.treeSearch4DeptManager(name, treeType);
        }
        return Y9Result.success(treeList, "获取机构树成功！");
    }

    /**
     * 获取组织架构列表
     *
     * @param virtual 是否为虚拟组织
     * @return
     */
    @RiseLog(operationName = "获取组织架构列表")
    @GetMapping(value = "/list2")
    public Y9Result<List<OrgTreeNodeVO>> list2(@RequestParam(required = false) boolean virtual) {
        List<Y9Organization> organizationList;
        if (Y9LoginUserHolder.getUserInfo().isGlobalManager()) {
            organizationList = y9OrganizationService.list(virtual);
        } else {
            List<Y9Organization> orgList = y9OrganizationService.list(false);
            Y9Department managerDept = y9DepartmentService.getById(Y9LoginUserHolder.getUserInfo().getParentId());
            String mapping = managerDept.getGuidPath();
            organizationList =
                orgList.stream().filter(org -> mapping.contains(org.getGuidPath())).collect(Collectors.toList());
        }

        return Y9Result.success(OrgTreeNodeVO.convertY9OrgBaseList(organizationList), "获取组织架构列表成功！");
    }

    /**
     * 获取机构树子节点
     *
     * @param id       父节点id
     * @param treeType 树类型
     * @param disabled false为不显示禁用人员，true为显示禁用人员
     * @return
     */
    @RiseLog(operationName = "获取机构树子节点")
    @GetMapping(value = "/getTree2")
    public Y9Result<List<OrgTreeNodeVO>> getTree2(@RequestParam @NotBlank String id,
        @RequestParam TreeTypeEnum treeType, boolean disabled) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        List<Y9OrgBase> treeList = new ArrayList<>();
        if (userInfo.isGlobalManager() && !ManagerLevelEnum.GENERAL_USER.equals(userInfo.getManagerLevel())) {
            treeList = compositeOrgBaseService.getTree(id, treeType, disabled);
        } else if (!ManagerLevelEnum.GENERAL_USER.equals(userInfo.getManagerLevel())) {
            treeList = compositeOrgBaseService.getTree4DeptManager(id, treeType);
        }
        return Y9Result.success(OrgTreeNodeVO.convertY9OrgBaseList(treeList), "获取机构树成功！");
    }

    /**
     * 根据name，和结构树类型查询机构主体
     *
     * @param name     名称
     * @param treeType 树类型
     * @return
     */
    @RiseLog(operationName = "查询机构主体")
    @GetMapping(value = "/treeSearch2")
    public Y9Result<List<OrgTreeNodeVO>> treeSearch2(@RequestParam String name, @RequestParam TreeTypeEnum treeType) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        List<Y9OrgBase> treeList = new ArrayList<>();
        if (userInfo.isGlobalManager()) {
            treeList = compositeOrgBaseService.treeSearch(name, treeType);
        } else if (ManagerLevelEnum.SYSTEM_MANAGER.equals(userInfo.getManagerLevel())
            || ManagerLevelEnum.SECURITY_MANAGER.equals(userInfo.getManagerLevel())) {
            treeList = compositeOrgBaseService.treeSearch4DeptManager(name, treeType);
        }
        return Y9Result.success(OrgTreeNodeVO.convertY9OrgBaseList(treeList), "获取机构树成功！");
    }

}
