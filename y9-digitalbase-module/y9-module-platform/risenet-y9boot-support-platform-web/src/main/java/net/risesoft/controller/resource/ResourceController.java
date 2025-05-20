package net.risesoft.controller.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.controller.resource.vo.ResourceBaseVO;
import net.risesoft.controller.resource.vo.ResourceTreeNodeVO;
import net.risesoft.enums.platform.ManagerLevelEnum;
import net.risesoft.enums.platform.TreeNodeType;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.user.UserInfo;
import net.risesoft.permission.annotation.IsAnyManager;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9ResourceBase;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.service.resource.CompositeResourceService;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.tenant.Y9TenantAppService;
import net.risesoft.y9public.service.tenant.Y9TenantSystemService;

/**
 * 资源管理
 *
 * @author shidaobang
 * @date 2022/3/1
 */
@RestController
@RequestMapping(value = "/api/rest/resource", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
@IsAnyManager({ManagerLevelEnum.SYSTEM_MANAGER, ManagerLevelEnum.SECURITY_MANAGER,
    ManagerLevelEnum.OPERATION_SYSTEM_MANAGER})
public class ResourceController {

    private final CompositeResourceService compositeResourceService;
    private final Y9AppService y9AppService;
    private final Y9TenantAppService y9TenantAppService;
    private final Y9TenantSystemService y9TenantSystemService;
    private final Y9SystemService y9SystemService;

    /**
     * 查询所有的根资源（App资源）
     *
     * @return {@code Y9Result<List<}{@link ResourceBaseVO}{@code >>}
     */
    @RiseLog(operationName = "查询所有的根资源（App资源）")
    @GetMapping(value = "/allTreeRoot")
    @Deprecated
    public Y9Result<List<ResourceBaseVO>> allTreeRoot() {
        List<Y9App> appResourceList = compositeResourceService.listRootResourceList();
        return Y9Result.success(Y9ModelConvertUtil.convert(appResourceList, ResourceBaseVO.class), "查询所有的根资源成功");
    }

    /**
     * 根据父资源id获取子资源列表
     *
     * @param parentId 父节点id
     * @return {@code Y9Result<List<}{@link ResourceTreeNodeVO}{@code >>}
     */
    @RiseLog(operationName = "根据父资源id获取子资源列表")
    @GetMapping(value = "/listByParentId2")
    public Y9Result<List<ResourceTreeNodeVO>> listByParentId2(@RequestParam @NotBlank String parentId) {
        List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.listByParentId(parentId);
        return Y9Result.success(ResourceTreeNodeVO.convertY9ResourceBaseList(y9ResourceBaseList), "根据父资源id获取子资源列表成功");
    }

    /**
     * 查询所有的根资源（有权限的App资源）
     *
     * @return {@code Y9Result<List<}{@link ResourceTreeNodeVO}{@code >>}
     */
    @RiseLog(operationName = "查询所有的根资源（有权限的App资源）")
    @GetMapping(value = "/treeRoot2")
    public Y9Result<List<ResourceTreeNodeVO>> treeRoot2() {
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
        return Y9Result.success(ResourceTreeNodeVO.convertY9ResourceBaseList(accessibleAppResourceList), "查询所有的根资源成功");
    }

    /**
     * 对同一级的资源进行排序
     *
     * @param ids 资源id数组
     * @return {@code Y9Result<Object>}
     */
    @RiseLog(operationName = "对同一级的资源进行排序", operationType = OperationTypeEnum.MODIFY)
    @GetMapping(value = "/sort")
    public Y9Result<Object> sort(@RequestParam @NotEmpty String[] ids) {
        compositeResourceService.sort(ids);
        return Y9Result.successMsg("对同一级的资源进行排序成功");
    }

    /**
     * 根据父节点id，父节点类型分层获取资源树
     *
     * @param parentId 父节点id
     * @param parentNodeType 父节点类型
     * @return {@code Y9Result<List<}{@link ResourceTreeNodeVO}{@code >>}
     */
    @RiseLog(operationName = "根据父节点id，父节点类型分层获取资源树")
    @GetMapping(value = "/tree")
    public Y9Result<List<ResourceTreeNodeVO>> tree(@RequestParam(required = false) String parentId,
        @RequestParam(required = false) TreeNodeType parentNodeType) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList;
        if (ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.equals(Y9LoginUserHolder.getUserInfo().getManagerLevel())) {
            resourceTreeNodeVOList = treeByOperationSystemManager(parentId, parentNodeType);
        } else {
            resourceTreeNodeVOList = treeBySystemManager(parentId, parentNodeType);
        }
        return Y9Result.success(resourceTreeNodeVOList, "查询所有的根资源成功");
    }

    private List<ResourceTreeNodeVO> treeByOperationSystemManager(String parentId, TreeNodeType parentNodeType) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList = new ArrayList<>();
        if (StringUtils.isBlank(parentId)) {
            // 根节点为系统
            List<Y9System> y9SystemList = y9SystemService.listAll();
            resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9SystemList(y9SystemList));
        } else if (TreeNodeType.SYSTEM.equals(parentNodeType)) {
            // 系统节点下为应用
            List<Y9App> appList = y9AppService.listBySystemId(parentId);
            resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9ResourceBaseList(appList));
        } else {
            List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.listByParentId(parentId);
            resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9ResourceBaseList(y9ResourceBaseList));
        }
        return resourceTreeNodeVOList;
    }

    private List<ResourceTreeNodeVO> treeBySystemManager(String parentId, TreeNodeType parentNodeType) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList = new ArrayList<>();
        if (StringUtils.isBlank(parentId)) {
            // 根节点为系统
            List<Y9System> y9SystemList = y9TenantSystemService.listSystemByTenantId(Y9LoginUserHolder.getTenantId());
            resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9SystemList(y9SystemList));
        } else if (TreeNodeType.SYSTEM.equals(parentNodeType)) {
            // 系统节点下为应用
            List<String> appIdList = y9TenantAppService.listAppIdBySystemIdAndTenantId(parentId,
                Y9LoginUserHolder.getTenantId(), true, true);
            List<Y9App> appList = y9AppService.listByIds(appIdList);
            resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9ResourceBaseList(appList));
        } else {
            List<Y9ResourceBase> y9ResourceBaseList = compositeResourceService.listByParentId(parentId);
            resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9ResourceBaseList(y9ResourceBaseList));
        }
        return resourceTreeNodeVOList;
    }

    /**
     * 根据应用id查询资源（App资源）
     *
     * @param appId 应用id
     * @return {@code Y9Result<List<}{@link ResourceBaseVO}{@code >>}
     */
    @RiseLog(operationName = "根据应用id查询资源（App资源）")
    @GetMapping(value = "/appTreeRoot/{appId}")
    public Y9Result<List<ResourceTreeNodeVO>> treeRootByAppId(@PathVariable @NotBlank String appId) {
        Y9App y9App = y9AppService.getById(appId);
        return Y9Result.success(ResourceTreeNodeVO.convertY9ResourceBaseList(Collections.singletonList(y9App)),
            "根据应用id查询资源成功");
    }

    /**
     * 根据系统id查询所有的根资源（有权限的App资源）
     *
     * @param systemId 系统id
     * @return {@code Y9Result<List<}{@link ResourceBaseVO}{@code >>}
     */
    @RiseLog(operationName = "根据系统id查询所有的根资源（有权限的App资源）")
    @GetMapping(value = "/treeRoot/{systemId}")
    public Y9Result<List<ResourceBaseVO>> treeRootBySystemId(@PathVariable @NotBlank String systemId) {
        List<Y9ResourceBase> appResourceList = compositeResourceService.listRootResourceBySystemId(systemId);
        List<String> appIds =
            y9TenantAppService.listAppIdByTenantId(Y9LoginUserHolder.getTenantId(), Boolean.TRUE, Boolean.TRUE);
        List<Y9ResourceBase> asscAppResourceList =
            appResourceList.stream().filter(resource -> appIds.contains(resource.getId())).collect(Collectors.toList());
        return Y9Result.success(Y9ModelConvertUtil.convert(asscAppResourceList, ResourceBaseVO.class),
            "根据系统id查询所有的根资源成功");
    }

    /**
     * 根据名称查询资源树
     *
     * @param name 资源名称
     * @param appId 应用id
     * @return {@code Y9Result<List<}{@link ResourceTreeNodeVO}{@code >>}
     */
    @RiseLog(operationName = "根据名称查询资源树")
    @GetMapping(value = "/treeSearch2")
    public Y9Result<List<ResourceTreeNodeVO>> treeSearch2(@RequestParam String name, String appId) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList;
        if (ManagerLevelEnum.OPERATION_SYSTEM_MANAGER.equals(Y9LoginUserHolder.getUserInfo().getManagerLevel())) {
            resourceTreeNodeVOList = treeSearchByOperationSystemManager(name, appId);
        } else {
            resourceTreeNodeVOList = treeSearchBySystemManager(name, appId);
        }

        return Y9Result.success(resourceTreeNodeVOList, "根据名称查询资源树成功");
    }

    private List<ResourceTreeNodeVO> treeSearchByOperationSystemManager(String name, String appId) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList = new ArrayList<>();

        List<Y9ResourceBase> appResourceList = compositeResourceService.treeSearch(name);
        List<Y9ResourceBase> accessResourceList = appResourceList;
        if (StringUtils.isNotBlank(appId)) {
            accessResourceList = appResourceList.stream().filter(resource -> appId.equals(resource.getAppId()))
                .collect(Collectors.toList());
        }
        resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9ResourceBaseList(accessResourceList));

        List<String> systemIdList =
            accessResourceList.stream().map(Y9ResourceBase::getSystemId).collect(Collectors.toList());
        List<Y9System> y9SystemList = y9SystemService.listByIds(systemIdList);
        resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9SystemList(y9SystemList));
        return resourceTreeNodeVOList;
    }

    private List<ResourceTreeNodeVO> treeSearchBySystemManager(String name, String appId) {
        List<ResourceTreeNodeVO> resourceTreeNodeVOList = new ArrayList<>();

        List<Y9ResourceBase> appResourceList = compositeResourceService.treeSearch(name);
        List<String> appIds =
            y9TenantAppService.listAppIdByTenantId(Y9LoginUserHolder.getTenantId(), Boolean.TRUE, Boolean.TRUE);
        List<Y9ResourceBase> accessAppResourceList = appResourceList.stream()
            .filter(resource -> appIds.contains(resource.getAppId())).collect(Collectors.toList());
        if (StringUtils.isNotBlank(appId)) {
            accessAppResourceList = accessAppResourceList.stream().filter(resource -> appId.equals(resource.getAppId()))
                .collect(Collectors.toList());
        }
        resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9ResourceBaseList(accessAppResourceList));

        List<String> systemIdList =
            accessAppResourceList.stream().map(Y9ResourceBase::getSystemId).collect(Collectors.toList());
        List<Y9System> y9SystemList = y9SystemService.listByIds(systemIdList);
        resourceTreeNodeVOList.addAll(ResourceTreeNodeVO.convertY9SystemList(y9SystemList));
        return resourceTreeNodeVOList;
    }
}