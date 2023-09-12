package net.risesoft.controller.role;

import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.DefaultIdConsts;
import net.risesoft.controller.role.vo.RoleVO;
import net.risesoft.dataio.role.Y9RoleDataHandler;
import net.risesoft.enums.Y9RoleTypeEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.util.Y9FileUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9.util.mime.ContentDispositionUtil;
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
public class RoleController {

    private final Y9RoleService y9RoleService;
    private final Y9AppService y9AppService;
    private final Y9SystemService y9SystemService;
    private final Y9RoleDataHandler y9RoleDataHandler;

    /**
     * 删除角色节点
     *
     * @param id 角色id
     * @return
     */
    @RiseLog(operationName = "删除角色", operationType = OperationTypeEnum.DELETE)
    @PostMapping(value = "/deleteById")
    public Y9Result<String> deleteById(@RequestParam String id) {
        y9RoleService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 导出角色树
     *
     * @param resourceId 角色id
     * @param response 请求
     */
    @RiseLog(operationName = "导出角色树XML", operationType = OperationTypeEnum.ADD)
    @GetMapping(value = "/exportRoleXml")
    public void exportRoleXml(@RequestParam String resourceId, HttpServletResponse response) {
        try (OutputStream outStream = response.getOutputStream()) {

            Y9App y9App = y9AppService.findById(resourceId);
            String fileName =
                y9App.getName() + "-角色信息-" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xml";
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", ContentDispositionUtil.standardizeAttachment(fileName));

            y9RoleDataHandler.doExport(resourceId, outStream);

        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * 根据角色id，获取扩展属性
     *
     * @param roleId 角色id
     * @return
     */
    @RiseLog(operationName = "获取扩展属性")
    @RequestMapping(value = "/getExtendProperties")
    public Y9Result<String> getExtendProperties(@RequestParam String roleId) {
        String properties = y9RoleService.findById(roleId).getProperties();
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
    public Y9Result<Y9Role> getRoleById(@RequestParam String id) {
        return Y9Result.success(y9RoleService.findById(id), "获取角色对象成功");
    }

    /**
     * 获取主角色树
     *
     * @return
     */
    @RiseLog(operationName = "获取主角色树")
    @RequestMapping(value = "/getRootTree")
    public Y9Result<List<RoleVO>> getRootTree() {
        List<Y9Role> y9RoleList = y9RoleService.listByParentIdIsNull();
        List<RoleVO> roleVOList = new ArrayList<>();
        if (y9RoleList != null && !y9RoleList.isEmpty()) {
            for (Y9Role roleNode : y9RoleList) {
                RoleVO roleVO = Y9ModelConvertUtil.convert(roleNode, RoleVO.class);
                if (Y9RoleTypeEnum.FOLDER.getValue().equals(roleNode.getType())) {
                    roleVO.setHasChild(!y9RoleService.listByParentId(roleNode.getId()).isEmpty());
                }
                roleVOList.add(roleVO);
            }
        }
        return Y9Result.success(roleVOList, "获取主角色树成功");
    }

    /**
     * 导入角色
     *
     * @param file 上传文件
     * @param roleId 角色id
     * @return
     * @throws IOException
     */
    @RiseLog(operationName = "导入角色", operationType = OperationTypeEnum.ADD)
    @RequestMapping(value = "/impRoleXml")
    public Y9Result<String> impRoleXml(@RequestParam MultipartFile file, @RequestParam String roleId)
        throws IOException {
        String uploadDir = Y9Context.getRealPath("/file/temp/");
        File f = Y9FileUtil.writeFile(file.getInputStream(), ".xml", uploadDir);
        FileInputStream fileInputStream = new FileInputStream(f);
        y9RoleDataHandler.doImport(fileInputStream, roleId);
        return Y9Result.successMsg("导入角色成功");
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
    public Y9Result<List<Y9Role>> listByParentId(@RequestParam String parentId) {
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
    public Y9Result<String> saveExtendProperties(@RequestParam String id, @RequestParam String properties) {
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
    public Y9Result<String> saveMove(@RequestParam String id, @RequestParam String parentId) {
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
    public Y9Result<String> saveOrder(@RequestParam List<String> ids) {
        y9RoleService.saveOrder(ids);
        return Y9Result.successMsg("保存角色节点排序成功");
    }

    /**
     * 新建或者更新角色节点信息
     *
     * @param roleNode 角色实体
     * @return
     */
    @RiseLog(operationName = "新建或者更新角色节点信息", operationType = OperationTypeEnum.MODIFY)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Y9Role> saveOrUpdate(Y9Role roleNode) {
        if (StringUtils.isBlank(roleNode.getAppCnName())) {
            Y9App y9App = y9AppService.getById(roleNode.getAppId());
            roleNode.setAppCnName(y9App.getName());
            Y9System y9System = y9SystemService.getById(y9App.getSystemId());
            roleNode.setSystemName(y9System.getName());
            roleNode.setSystemCnName(y9System.getCnName());
        }
        Y9Role role = y9RoleService.saveOrUpdate(roleNode);
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
    public Y9Result<List<RoleVO>> treeSearch(@RequestParam String name) {
        List<Y9Role> y9RoleList = y9RoleService.treeSearchByName(name);
        List<RoleVO> roleVOList = new ArrayList<>();
        if (y9RoleList != null && !y9RoleList.isEmpty()) {
            Set<String> appIdList = y9RoleList.stream().map(Y9Role::getAppId).collect(Collectors.toSet());
            List<Y9App> appList = new ArrayList<>();
            for (String appId : appIdList) {
                if (!DefaultIdConsts.TOP_PUBLIC_ROLE_ID.equals(appId)) {
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
                appVO.setType("App");
                appVO.setHasChild(true);
                appVO.setParentId(y9App.getId());
                appVO.setGuidPath(y9App.getId());
                roleVOList.add(appVO);
            }
            for (Y9Role roleNode : y9RoleList) {
                if (!DefaultIdConsts.TOP_PUBLIC_ROLE_ID.equals(roleNode.getAppId())) {
                    RoleVO roleVO = Y9ModelConvertUtil.convert(roleNode, RoleVO.class);
                    if (Y9RoleTypeEnum.FOLDER.getValue().equals(roleNode.getType())) {
                        roleVO.setHasChild(!y9RoleService.listByParentId(roleNode.getId()).isEmpty());
                    }
                    roleVO.setGuidPath(roleNode.getAppId() + "," + roleNode.getGuidPath());
                    roleVOList.add(roleVO);
                }
            }
        }
        return Y9Result.success(roleVOList, "根据角色名称查询角色节点成功");
    }

}
