package net.risesoft.controller.role;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.enums.platform.TreeNodeType;
import net.risesoft.enums.platform.org.ManagerLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.vo.role.RoleTreeNodeVO;
import net.risesoft.vo.role.RoleVO;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 角色管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/role", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
    ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class RoleController {

    private final Y9RoleService y9RoleService;
    private final Y9AppService y9AppService;
    private final Y9TenantAppService y9TenantAppService;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9SystemService y9SystemService;

    /**
     * 删除角色节点
     *
     * @param id 角色id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "删除角色", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/deleteById")
    public Y9Result<String> deleteById(@RequestParam @NotBlank String id) {
        y9RoleService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据角色id，获取扩展属性
     *
     * @param roleId 角色id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam @NotBlank String roleId) {
        String properties = y9RoleService.getById(roleId).getProperties();
        return Y9Result.success(properties, "获取扩展属性成功");
    }

    /**
     * 根据id，获取角色对象
     *
     * @param id 唯一标识
     * @return {@code Y9Result<}{@link RoleVO}{@code >}
     */
    @RiseLog(operationName = "获取角色对象")
    @RequestMapping(value = "/getRoleById")
    public Y9Result<Y9Role> getRoleById(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9RoleService.getById(id), "获取角色对象成功");
    }

    /**
     * 根据父节点id，获取角色节点列表
     *
     * @param parentId 父节点id
     * @return {@code Y9Result<List<}{@link RoleTreeNodeVO}{@code >>}
     * @since 9.6.3
     */
    @RiseLog(operationName = "根据父节点id，获取角色节点列表 ")
    @GetMapping(value = "/listByParentId2")
    public Y9Result<List<RoleTreeNodeVO>> listByParentId2(@RequestParam @NotBlank String parentId) {
        List<Y9Role> roleList = y9RoleService.listByParentId(parentId);
        return Y9Result.success(RoleTreeNodeVO.convertY9RoleList(roleList), "获取角色列表成功");
    }

    /**
     * 保存扩展属性(直接覆盖) save the extend properties
     *
     * @param id 角色或节点id
     * @param properties 扩展属性
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存角色节点扩展属性(直接覆盖)", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveExtendProperties")
    public Y9Result<String> saveExtendProperties(@RequestParam @NotBlank String id, @RequestParam String properties) {
        Y9Role role = y9RoleService.saveProperties(id, properties);
        return Y9Result.success(role.getProperties(), "保存角色或节点扩展属性成功");
    }

    /**
     * 保存角色节点移动信息
     *
     * @param id 角色id
     * @param parentId 移动目标节点id
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存角色节点移动信息 ", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveMove")
    public Y9Result<String> saveMove(@RequestParam @NotBlank String id, @RequestParam @NotBlank String parentId) {
        y9RoleService.move(id, parentId);
        return Y9Result.successMsg("保存角色节点移动信息成功");
    }

    /**
     * 保存角色排序
     *
     * @param ids 角色id数组
     * @return {@code Y9Result<String>}
     */
    @RiseLog(operationName = "保存角色节点排序 ", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam @NotEmpty List<String> ids) {
        y9RoleService.saveOrder(ids);
        return Y9Result.successMsg("保存角色节点排序成功");
    }

    /**
     * 新建或者更新角色节点信息
     *
     * @param y9Role 角色实体
     * @return {@code Y9Result<}{@link RoleVO}{@code >}
     */
    @RiseLog(operationName = "新建或者更新角色节点信息", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Role> saveOrUpdate(Y9Role y9Role) {
        Y9Role role = y9RoleService.saveOrUpdate(y9Role);
        return Y9Result.success(role, "保存角色节点成功");
    }

    /**
     * 根据父节点id，父节点类型分层获取角色树（以系统为根节点）
     *
     * @param parentId 父节点id
     * @param parentNodeType 父节点类型
     * @return {@code Y9Result<List<}{@link RoleTreeNodeVO}{@code >>}
     * @since 9.6.3
     */
    @RiseLog(operationName = "根据父节点id，父节点类型分层获取角色树（以系统为根节点）")
    @GetMapping(value = "/tree")
    public Y9Result<List<RoleTreeNodeVO>> tree(@RequestParam(required = false) String parentId,
        @RequestParam(required = false) TreeNodeType parentNodeType) {
        List<RoleTreeNodeVO> roleTreeNodeVOList;
        if (ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.equals(Y9LoginUserHolder.getUserInfo().getManagerLevel())) {
            roleTreeNodeVOList = treeByOperationSystemManager(parentId, parentNodeType);
        } else {
            roleTreeNodeVOList = treeBySystemManager(parentId, parentNodeType);
        }
        return Y9Result.success(roleTreeNodeVOList, "获取角色列表成功");
    }

    private List<RoleTreeNodeVO> treeByOperationSystemManager(String parentId, TreeNodeType parentNodeType) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        if (StringUtils.isBlank(parentId)) {
            // 根节点为系统
            List<Y9System> y9SystemList = y9SystemService.listAll();
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9SystemList(y9SystemList));
        } else if (TreeNodeType.SYSTEM.equals(parentNodeType)) {
            // 系统下的角色
            List<Y9Role> y9RoleList = y9RoleService.listByParentId(parentId);
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9RoleList(y9RoleList));

            // 系统节点下为应用
            List<Y9App> appList = y9AppService.listBySystemId(parentId);
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9AppList(appList));

        } else {
            // 应用节点下为角色文件夹或角色节点
            List<Y9Role> roleList = y9RoleService.listByParentId(parentId);
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9RoleList(roleList));
        }
        return roleTreeNodeVOList;
    }

    private List<RoleTreeNodeVO> treeBySystemManager(String parentId, TreeNodeType parentNodeType) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        if (StringUtils.isBlank(parentId)) {
            // 根节点为系统
            List<Y9System> y9SystemList = y9TenantSystemService.listSystemByTenantId(Y9LoginUserHolder.getTenantId());
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9SystemList(y9SystemList));
        } else if (TreeNodeType.SYSTEM.equals(parentNodeType)) {
            // 系统下的角色
            List<Y9Role> y9RoleList = y9RoleService.listByParentId(parentId);
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9RoleList(y9RoleList));

            // 系统节点下为应用及系统下所有应用共用的角色
            List<String> appIdList = y9TenantAppService.listAppIdBySystemIdAndTenantId(parentId,
                Y9LoginUserHolder.getTenantId(), true, true);
            List<Y9App> appList = y9AppService.listByIds(appIdList);
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9AppList(appList));

        } else {
            // 应用节点下为角色文件夹或角色节点
            List<Y9Role> roleList = y9RoleService.listByParentId4Tenant(parentId, Y9LoginUserHolder.getTenantId());
            roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9RoleList(roleList));
        }
        return roleTreeNodeVOList;
    }

    /**
     * 根据角色名称，查询角色节点
     *
     * @param name 角色名称
     * @return {@code Y9Result<List<}{@link RoleTreeNodeVO}{@code >>}
     * @since 9.6.3
     */
    @RiseLog(operationName = "查询角色")
    @GetMapping(value = "/treeSearch2")
    public Y9Result<List<RoleTreeNodeVO>> treeSearch2(@RequestParam String name) {
        List<RoleTreeNodeVO> roleTreeNodeVOList;
        if (ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.equals(Y9LoginUserHolder.getUserInfo().getManagerLevel())) {
            roleTreeNodeVOList = treeSearchByOperationSystemManager(name);
        } else {
            roleTreeNodeVOList = treeSearchBySystemManager(name);
        }
        return Y9Result.success(roleTreeNodeVOList, "根据角色名称查询角色节点成功");
    }

    private List<RoleTreeNodeVO> treeSearchByOperationSystemManager(String name) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();

        List<Y9Role> y9RoleList = y9RoleService.treeSearch(name);
        Set<String> appIdList = y9RoleList.stream().map(Y9Role::getAppId).collect(Collectors.toSet());
        List<Y9App> appList = new ArrayList<>();
        for (String appId : appIdList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(appId)) {
                Y9App y9App = y9AppService.getById(appId);
                appList.add(y9App);
            }
        }
        Collections.sort(appList);
        roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9AppList(appList));

        List<String> systemIdList =
            appList.stream().map(Y9ResourceBase::getSystemId).distinct().collect(Collectors.toList());
        List<Y9System> y9SystemList = y9SystemService.listByIds(systemIdList);
        roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9SystemList(y9SystemList));

        for (Y9Role y9Role : y9RoleList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getAppId())) {
                roleTreeNodeVOList.add(RoleTreeNodeVO.convertY9Role(y9Role));
            }
        }
        return roleTreeNodeVOList;
    }

    private List<RoleTreeNodeVO> treeSearchBySystemManager(String name) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();

        List<Y9Role> y9RoleList = y9RoleService.treeSearch(name);

        List<String> tenantedAppIdList =
            y9TenantAppService.listAppIdByTenantId(Y9LoginUserHolder.getTenantId(), true, true);
        Set<String> roleRelatedAppIdSet = y9RoleList.stream().map(Y9Role::getAppId).collect(Collectors.toSet());
        Collection<String> resultAppIdCollection = CollectionUtils.intersection(tenantedAppIdList, roleRelatedAppIdSet);
        List<Y9App> appList = new ArrayList<>();
        for (String appId : resultAppIdCollection) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(appId)) {
                Y9App y9App = y9AppService.getById(appId);
                appList.add(y9App);
            }
        }
        Collections.sort(appList);
        roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9AppList(appList));

        List<String> systemIdList =
            appList.stream().map(Y9ResourceBase::getSystemId).distinct().collect(Collectors.toList());
        List<Y9System> y9SystemList = y9SystemService.listByIds(systemIdList);
        roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9SystemList(y9SystemList));

        for (Y9Role y9Role : y9RoleList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getAppId())) {
                roleTreeNodeVOList.add(RoleTreeNodeVO.convertY9Role(y9Role));
            }
        }
        return roleTreeNodeVOList;
    }

    /**
     * 获取应用角色树根节点（以应用为根节点）
     *
     * @return {@code Y9Result<List<}{@link RoleTreeNodeVO}{@code >>}
     * @since 9.6.8
     */
    @RiseLog(operationName = "获取应用角色树根节点（以应用为根节点）")
    @GetMapping(value = "/appRoleTree")
    public Y9Result<List<RoleTreeNodeVO>> appRoleTree(String appId) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();

        Y9App y9App = y9AppService.getById(appId);
        roleTreeNodeVOList.add(RoleTreeNodeVO.convertY9App(y9App));

        List<Y9Role> y9RoleList = y9RoleService.listByParentId(y9App.getSystemId());
        roleTreeNodeVOList.addAll(RoleTreeNodeVO.convertY9RoleList(y9RoleList));

        return Y9Result.success(roleTreeNodeVOList, "查询所有的根资源成功");
    }

}
