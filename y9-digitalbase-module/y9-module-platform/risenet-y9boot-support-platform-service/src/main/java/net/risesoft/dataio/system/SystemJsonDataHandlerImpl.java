package net.risesoft.dataio.system;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dataio.system.model.AppJsonModel;
import net.risesoft.dataio.system.model.MenuJsonModel;
import net.risesoft.dataio.system.model.OperationJsonModel;
import net.risesoft.dataio.system.model.RoleJsonModel;
import net.risesoft.dataio.system.model.SystemJsonModel;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.platform.resource.Menu;
import net.risesoft.model.platform.resource.Operation;
import net.risesoft.util.PlatformModelConvertUtil;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.service.resource.Y9AppService;
import net.risesoft.y9public.service.resource.Y9MenuService;
import net.risesoft.y9public.service.resource.Y9OperationService;
import net.risesoft.y9public.service.resource.Y9SystemService;
import net.risesoft.y9public.service.role.Y9RoleService;

/**
 * 系统 JSON 数据导入导出
 *
 * @author shidaobang
 * @date 2025/02/10
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SystemJsonDataHandlerImpl implements SystemDataHandler {

    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;
    private final Y9RoleService y9RoleService;

    private AppJsonModel buildApp(String appId) {
        App y9App = y9AppService.getById(appId);
        AppJsonModel appJsonModel = PlatformModelConvertUtil.convert(y9App, AppJsonModel.class);
        appJsonModel.setSubMenuList(buildMenuList(appJsonModel.getId()));
        appJsonModel.setSubOperationList(buildOperationList(appJsonModel.getId()));
        appJsonModel.setSubRoleList(buildRoleList(appJsonModel.getId()));
        return appJsonModel;
    }

    private List<AppJsonModel> buildAppList(String systemId) {
        List<App> y9AppList = y9AppService.listBySystemId(systemId);
        List<AppJsonModel> appJsonModelList = PlatformModelConvertUtil.convert(y9AppList, AppJsonModel.class);
        for (AppJsonModel appJsonModel : appJsonModelList) {
            appJsonModel.setSubMenuList(buildMenuList(appJsonModel.getId()));
            appJsonModel.setSubOperationList(buildOperationList(appJsonModel.getId()));
            appJsonModel.setSubRoleList(buildRoleList(appJsonModel.getId()));
        }
        return appJsonModelList;
    }

    private List<MenuJsonModel> buildMenuList(String parentId) {
        List<Menu> y9MenuList = y9MenuService.findByParentId(parentId);
        List<MenuJsonModel> menuJsonModelList = PlatformModelConvertUtil.convert(y9MenuList, MenuJsonModel.class);
        for (MenuJsonModel menuJsonModel : menuJsonModelList) {
            menuJsonModel.setSubMenuList(buildMenuList(menuJsonModel.getId()));
            menuJsonModel.setSubOperationList(buildOperationList(menuJsonModel.getId()));
        }
        return menuJsonModelList;
    }

    private List<OperationJsonModel> buildOperationList(String parentId) {
        List<Operation> y9OperationList = y9OperationService.findByParentId(parentId);
        List<OperationJsonModel> operationJsonModelList =
            PlatformModelConvertUtil.convert(y9OperationList, OperationJsonModel.class);
        for (OperationJsonModel operationJsonModel : operationJsonModelList) {
            operationJsonModel.setSubMenuList(buildMenuList(operationJsonModel.getId()));
            operationJsonModel.setSubOperationList(buildOperationList(operationJsonModel.getId()));
        }
        return operationJsonModelList;
    }

    private List<RoleJsonModel> buildRoleList(String parentId) {
        List<Role> y9RoleList = y9RoleService.listByParentId(parentId);
        List<RoleJsonModel> roleJsonModelList = PlatformModelConvertUtil.convert(y9RoleList, RoleJsonModel.class);
        for (RoleJsonModel roleJsonModel : roleJsonModelList) {
            roleJsonModel.setSubRoleList(buildRoleList(roleJsonModel.getId()));
        }
        return roleJsonModelList;
    }

    private SystemJsonModel buildSystem(String systemId) {
        System y9System = y9SystemService.getById(systemId);
        SystemJsonModel systemJsonModel = PlatformModelConvertUtil.convert(y9System, SystemJsonModel.class);
        systemJsonModel.setAppList(buildAppList(systemId));
        systemJsonModel.setSubRoleList(buildRoleList(systemId));
        return systemJsonModel;
    }

    @Override
    public void exportApp(String appId, OutputStream outStream) {
        AppJsonModel appJsonModel = this.buildApp(appId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(appJsonModel);

        try {
            IOUtils.write(jsonStr, outStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }

    @Override
    public void exportSystem(String systemId, OutputStream outStream) {
        SystemJsonModel systemJsonModel = this.buildSystem(systemId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(systemJsonModel);

        try {
            IOUtils.write(jsonStr, outStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void importApp(AppJsonModel appJsonModel, String systemId) {
        App y9App = PlatformModelConvertUtil.convert(appJsonModel, App.class);
        y9App.setSystemId(systemId);
        y9AppService.saveAndRegister4Tenant(y9App);
        importMenuList(appJsonModel.getSubMenuList(), systemId);
        importOperationList(appJsonModel.getSubOperationList(), systemId);
        importRoleList(appJsonModel.getSubRoleList(), systemId);
    }

    @Override
    public void importSystem(SystemJsonModel systemJsonModel) {
        System system = PlatformModelConvertUtil.convert(systemJsonModel, System.class);
        system = y9SystemService.saveAndRegister4Tenant(system);
        if (systemJsonModel.getSubRoleList() != null && !systemJsonModel.getSubRoleList().isEmpty()) {
            importRoleList(systemJsonModel.getSubRoleList(), system.getId());
        }
        if (systemJsonModel.getAppList() != null && !systemJsonModel.getAppList().isEmpty()) {
            importAppList(systemJsonModel.getAppList(), system.getId());
        }
    }

    private void importAppList(List<AppJsonModel> appJsonModelList, String systemId) {
        for (AppJsonModel appJsonModel : appJsonModelList) {
            importApp(appJsonModel, systemId);
        }
    }

    private void importMenu(MenuJsonModel menuJsonModel, String systemId) {
        Menu y9Menu = PlatformModelConvertUtil.convert(menuJsonModel, Menu.class);
        y9Menu.setSystemId(systemId);
        y9MenuService.saveOrUpdate(y9Menu);
        if (null != menuJsonModel.getSubOperationList() && !menuJsonModel.getSubOperationList().isEmpty()) {
            importOperationList(menuJsonModel.getSubOperationList(), systemId);
        }
        if (null != menuJsonModel.getSubMenuList() && !menuJsonModel.getSubMenuList().isEmpty()) {
            importMenuList(menuJsonModel.getSubMenuList(), systemId);
        }
    }

    private void importMenuList(List<MenuJsonModel> y9MenuList, String systemId) {
        for (MenuJsonModel menuJsonModel : y9MenuList) {
            importMenu(menuJsonModel, systemId);
        }
    }

    private void importOperation(OperationJsonModel operationJsonModel, String systemId) {
        Operation y9Operation = PlatformModelConvertUtil.convert(operationJsonModel, Operation.class);
        y9Operation.setSystemId(systemId);
        y9OperationService.saveOrUpdate(y9Operation);
        if (null != operationJsonModel.getSubOperationList() && !operationJsonModel.getSubOperationList().isEmpty()) {
            importOperationList(operationJsonModel.getSubOperationList(), systemId);
        }
        if (null != operationJsonModel.getSubMenuList() && !operationJsonModel.getSubMenuList().isEmpty()) {
            importMenuList(operationJsonModel.getSubMenuList(), systemId);
        }
    }

    private void importOperationList(List<OperationJsonModel> operationJsonModelList, String systemId) {
        for (OperationJsonModel operationJsonModel : operationJsonModelList) {
            importOperation(operationJsonModel, systemId);
        }
    }

    private void importRole(RoleJsonModel roleJsonModel, String systemId) {
        Role y9Role = PlatformModelConvertUtil.convert(roleJsonModel, Role.class);
        y9RoleService.saveOrUpdate(y9Role);
        if (null != roleJsonModel.getSubRoleList() && !roleJsonModel.getSubRoleList().isEmpty()) {
            importRoleList(roleJsonModel.getSubRoleList(), systemId);
        }
    }

    private void importRoleList(List<RoleJsonModel> roleJsonModelList, String systemId) {
        for (RoleJsonModel roleJsonModel : roleJsonModelList) {
            importRole(roleJsonModel, systemId);
        }
    }
}
