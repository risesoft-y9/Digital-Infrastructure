package net.risesoft.controller.role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import net.risesoft.controller.role.vo.RoleTreeNodeVO;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.controller.role.vo.RoleVO;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 角色管理
 *
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/14
 */
@RestController
@RequestMapping(value = "/api/rest/role", produces = "application/json")
@Slf4j
@RequiredArgsConstructor
@Validated
public class RoleController {

    private final Y9RoleService y9RoleService;
    private final Y9AppService y9AppService;
    private final Y9SystemService y9SystemService;
    private final CompositeResourceService compositeResourceService;
    private final Y9TenantAppService y9TenantAppService;

    /**
     * 删除角色节点
     *
     * @param id 角色id
     * @return
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
     * @return
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
     * @return
     */
    @RiseLog(operationName = "获取角色对象")
    @RequestMapping(value = "/getRoleById")
    public Y9Result<Y9Role> getRoleById(@RequestParam @NotBlank String id) {
        return Y9Result.success(y9RoleService.getById(id), "获取角色对象成功");
    }

    /**
     * 获取主角色树
     *
     * @return
     */
    @RiseLog(operationName = "获取主角色树")
    @RequestMapping(value = "/getRootTree")
    @Deprecated
    public Y9Result<List<RoleVO>> getRootTree() {
        List<Y9Role> y9RoleList = y9RoleService.listByParentIdIsNull();
        List<RoleVO> roleVOList = new ArrayList<>();
        if (y9RoleList != null && !y9RoleList.isEmpty()) {
            for (Y9Role y9Role : y9RoleList) {
                RoleVO roleVO = Y9ModelConvertUtil.convert(y9Role, RoleVO.class);
                if (RoleTypeEnum.FOLDER.equals(y9Role.getType())) {
                    roleVO.setHasChild(!y9RoleService.listByParentId(y9Role.getId()).isEmpty());
                }
                roleVOList.add(roleVO);
            }
        }
        return Y9Result.success(roleVOList, "获取主角色树成功");
    }

    /**
     * 根据父节点id，获取角色节点列表
     *
     * @param parentId 父节点id
     * @return
     * @since 9.6.1
     */
    @RiseLog(operationName = "根据父节点id，获取角色节点列表 ")
    @RequestMapping(value = "/listByParentId")
    @Deprecated
    public Y9Result<List<Y9Role>> listByParentId(@RequestParam @NotBlank String parentId) {
        List<Y9Role> roleList = y9RoleService.listByParentId(parentId);
        return Y9Result.success(roleList, "获取角色列表成功");
    }

    /**
     * 保存扩展属性(直接覆盖) save the extend properties
     *
     * @param id 角色或节点id
     * @param properties 扩展属性
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
     * @return
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
     * @return
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
     * @return
     */
    @RiseLog(operationName = "新建或者更新角色节点信息", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Role> saveOrUpdate(Y9Role y9Role) {
        Y9Role role = y9RoleService.saveOrUpdate(y9Role);
        return Y9Result.success(role, "保存角色节点成功");
    }

    /**
     * 根据角色名称，查询角色节点
     *
     * @param name 角色名称
     * @return
     */
    @RiseLog(operationName = "查询角色")
    @RequestMapping(value = "/treeSearch")
    @Deprecated
    public Y9Result<List<RoleVO>> treeSearch(@RequestParam String name) {
        List<Y9Role> y9RoleList = y9RoleService.treeSearchByName(name);
        List<RoleVO> roleVOList = new ArrayList<>();
        Set<String> appIdList = y9RoleList.stream().map(Y9Role::getAppId).collect(Collectors.toSet());
        List<Y9App> appList = new ArrayList<>();
        for (String appId : appIdList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(appId)) {
                Y9App y9App = y9AppService.getById(appId);
                appList.add(y9App);
            }
        }
        Collections.sort(appList);
        for (Y9App y9App : appList) {
            Y9System y9System = y9SystemService.getById(y9App.getSystemId());
            RoleVO appVO = new RoleVO();
            appVO.setId(y9App.getId());
            appVO.setName(y9App.getName());
            appVO.setSystemName(y9System.getName());
            appVO.setSystemCnName(y9System.getCnName());
            appVO.setType(RoleTypeEnum.FOLDER);
            appVO.setHasChild(true);
            appVO.setParentId(y9App.getId());
            appVO.setGuidPath(y9App.getId());
            roleVOList.add(appVO);
        }
        for (Y9Role y9Role : y9RoleList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getAppId())) {
                RoleVO roleVO = Y9ModelConvertUtil.convert(y9Role, RoleVO.class);
                if (RoleTypeEnum.FOLDER.equals(y9Role.getType())) {
                    roleVO.setHasChild(!y9RoleService.listByParentId(y9Role.getId()).isEmpty());
                }
                roleVO.setGuidPath(y9Role.getAppId() + "," + y9Role.getGuidPath());
                roleVOList.add(roleVO);
            }
        }
        return Y9Result.success(roleVOList, "根据角色名称查询角色节点成功");
    }

    /**
     * 获取角色树根节点
     *
     * @return
     * @since 9.6.3
     */
    @RiseLog(operationName = "获取主角色树")
    @GetMapping(value = "/getRootTree2")
    public Y9Result<List<RoleTreeNodeVO>> getRootTree2() {
        List<Y9App> appResourceList = compositeResourceService.listRootResourceList();
        List<Y9App> accessibleAppResourceList;

        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        if (ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.equals(userInfo.getManagerLevel())) {
            accessibleAppResourceList = appResourceList;
        } else {
            List<String> appIds =
                y9TenantAppService.listAppIdByTenantId(Y9LoginUserHolder.getTenantId(), Boolean.TRUE, Boolean.TRUE);
            accessibleAppResourceList = appResourceList.stream().filter(resource -> appIds.contains(resource.getId()))
                .collect(Collectors.toList());
        }
        return Y9Result.success(RoleTreeNodeVO.convertY9AppList(accessibleAppResourceList), "查询所有的根资源成功");
    }

    /**
     * 根据父节点id，获取角色节点列表
     *
     * @param parentId 父节点id
     * @return
     * @since 9.6.3
     */
    @RiseLog(operationName = "根据父节点id，获取角色节点列表 ")
    @GetMapping(value = "/listByParentId2")
    public Y9Result<List<RoleTreeNodeVO>> listByParentId2(@RequestParam @NotBlank String parentId) {
        List<Y9Role> roleList = y9RoleService.listByParentId(parentId);
        return Y9Result.success(RoleTreeNodeVO.convertY9RoleList(roleList), "获取角色列表成功");
    }

    /**
     * 根据角色名称，查询角色节点
     *
     * @param name 角色名称
     * @return
     * @since 9.6.3
     */
    @RiseLog(operationName = "查询角色")
    @GetMapping(value = "/treeSearch2")
    public Y9Result<List<RoleTreeNodeVO>> treeSearch2(@RequestParam String name) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();

        List<Y9Role> y9RoleList = y9RoleService.treeSearchByName(name);
        Set<String> appIdList = y9RoleList.stream().map(Y9Role::getAppId).collect(Collectors.toSet());
        List<Y9App> appList = new ArrayList<>();
        for (String appId : appIdList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(appId)) {
                Y9App y9App = y9AppService.getById(appId);
                appList.add(y9App);
            }
        }
        Collections.sort(appList);
        for (Y9App y9App : appList) {
            roleTreeNodeVOList.add(RoleTreeNodeVO.convertY9App(y9App));
        }
        for (Y9Role y9Role : y9RoleList) {
            if (!InitDataConsts.TOP_PUBLIC_ROLE_ID.equals(y9Role.getAppId())) {
                roleTreeNodeVOList.add(RoleTreeNodeVO.convertY9Role(y9Role));
            }
        }
        return Y9Result.success(roleTreeNodeVOList, "根据角色名称查询角色节点成功");
    }

}
