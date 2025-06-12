package net.risesoft.dataio.system;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.dataio.system.model.Y9AppJsonModel;
import net.risesoft.dataio.system.model.Y9MenuJsonModel;
import net.risesoft.dataio.system.model.Y9OperationJsonModel;
import net.risesoft.dataio.system.model.Y9RoleJsonModel;
import net.risesoft.dataio.system.model.Y9SystemJsonModel;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9ModelConvertUtil;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9Menu;
import net.risesoft.y9public.entity.resource.Y9Operation;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;
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
public class Y9SystemJsonDataHandlerImpl implements Y9SystemDataHandler {

    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;
    private final Y9RoleService y9RoleService;

    private Y9AppJsonModel buildApp(String appId) {
        Y9App y9App = y9AppService.getById(appId);
        Y9AppJsonModel y9AppJsonModel = Y9ModelConvertUtil.convert(y9App, Y9AppJsonModel.class);
        y9AppJsonModel.setSubMenuList(buildMenuList(y9AppJsonModel.getId()));
        y9AppJsonModel.setSubOperationList(buildOperationList(y9AppJsonModel.getId()));
        y9AppJsonModel.setSubRoleList(buildRoleList(y9AppJsonModel.getId()));
        return y9AppJsonModel;
    }

    private List<Y9AppJsonModel> buildAppList(String systemId) {
        List<Y9App> y9AppList = y9AppService.listBySystemId(systemId);
        List<Y9AppJsonModel> y9AppJsonModelList = Y9ModelConvertUtil.convert(y9AppList, Y9AppJsonModel.class);
        for (Y9AppJsonModel y9AppJsonModel : y9AppJsonModelList) {
            y9AppJsonModel.setSubMenuList(buildMenuList(y9AppJsonModel.getId()));
            y9AppJsonModel.setSubOperationList(buildOperationList(y9AppJsonModel.getId()));
            y9AppJsonModel.setSubRoleList(buildRoleList(y9AppJsonModel.getId()));
        }
        return y9AppJsonModelList;
    }

    private List<Y9MenuJsonModel> buildMenuList(String parentId) {
        List<Y9Menu> y9MenuList = y9MenuService.findByParentId(parentId);
        List<Y9MenuJsonModel> y9MenuJsonModelList = Y9ModelConvertUtil.convert(y9MenuList, Y9MenuJsonModel.class);
        for (Y9MenuJsonModel y9MenuJsonModel : y9MenuJsonModelList) {
            y9MenuJsonModel.setSubMenuList(buildMenuList(y9MenuJsonModel.getId()));
            y9MenuJsonModel.setSubOperationList(buildOperationList(y9MenuJsonModel.getId()));
        }
        return y9MenuJsonModelList;
    }

    private List<Y9OperationJsonModel> buildOperationList(String parentId) {
        List<Y9Operation> y9OperationList = y9OperationService.findByParentId(parentId);
        List<Y9OperationJsonModel> y9OperationJsonModelList =
            Y9ModelConvertUtil.convert(y9OperationList, Y9OperationJsonModel.class);
        for (Y9OperationJsonModel y9OperationJsonModel : y9OperationJsonModelList) {
            y9OperationJsonModel.setSubMenuList(buildMenuList(y9OperationJsonModel.getId()));
            y9OperationJsonModel.setSubOperationList(buildOperationList(y9OperationJsonModel.getId()));
        }
        return y9OperationJsonModelList;
    }

    private List<Y9RoleJsonModel> buildRoleList(String parentId) {
        List<Y9Role> y9RoleList = y9RoleService.listByParentId(parentId);
        List<Y9RoleJsonModel> y9RoleJsonModelList = Y9ModelConvertUtil.convert(y9RoleList, Y9RoleJsonModel.class);
        for (Y9RoleJsonModel y9RoleJsonModel : y9RoleJsonModelList) {
            y9RoleJsonModel.setSubRoleList(buildRoleList(y9RoleJsonModel.getId()));
        }
        return y9RoleJsonModelList;
    }

    private Y9SystemJsonModel buildSystem(String systemId) {
        Y9System y9System = y9SystemService.getById(systemId);
        Y9SystemJsonModel y9SystemJsonModel = Y9ModelConvertUtil.convert(y9System, Y9SystemJsonModel.class);
        y9SystemJsonModel.setAppList(buildAppList(systemId));
        y9SystemJsonModel.setSubRoleList(buildRoleList(systemId));
        return y9SystemJsonModel;
    }

    @Override
    public void exportApp(String appId, OutputStream outStream) {
        Y9AppJsonModel y9AppJsonModel = this.buildApp(appId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(y9AppJsonModel);

        try {
            IOUtils.write(jsonStr, outStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }

    }

    @Override
    public void exportSystem(String systemId, OutputStream outStream) {
        Y9SystemJsonModel y9SystemJsonModel = this.buildSystem(systemId);
        String jsonStr = Y9JsonUtil.writeValueAsStringWithDefaultPrettyPrinter(y9SystemJsonModel);

        try {
            IOUtils.write(jsonStr, outStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    @Override
    public void importApp(Y9AppJsonModel y9AppJsonModel, String systemId) {
        Y9App y9App = Y9ModelConvertUtil.convert(y9AppJsonModel, Y9App.class);
        y9App.setSystemId(systemId);
        y9AppService.saveAndRegister4Tenant(y9App);
        importMenuList(y9AppJsonModel.getSubMenuList(), systemId);
        importOperationList(y9AppJsonModel.getSubOperationList(), systemId);
        importRoleList(y9AppJsonModel.getSubRoleList(), systemId);
    }

    @Override
    public void importSystem(Y9SystemJsonModel y9SystemJsonModel) {
        Y9System y9System = Y9ModelConvertUtil.convert(y9SystemJsonModel, Y9System.class);
        y9System = y9SystemService.saveAndRegister4Tenant(y9System);
        if (y9SystemJsonModel.getSubRoleList() != null && !y9SystemJsonModel.getSubRoleList().isEmpty()) {
            importRoleList(y9SystemJsonModel.getSubRoleList(), y9System.getId());
        }
        if (y9SystemJsonModel.getAppList() != null && !y9SystemJsonModel.getAppList().isEmpty()) {
            importAppList(y9SystemJsonModel.getAppList(), y9System.getId());
        }
    }

    private void importAppList(List<Y9AppJsonModel> y9AppJsonModelList, String systemId) {
        for (Y9AppJsonModel y9AppJsonModel : y9AppJsonModelList) {
            importApp(y9AppJsonModel, systemId);
        }
    }

    private void importMenu(Y9MenuJsonModel y9MenuJsonModel, String systemId) {
        Y9Menu y9Menu = Y9ModelConvertUtil.convert(y9MenuJsonModel, Y9Menu.class);
        y9Menu.setSystemId(systemId);
        y9MenuService.saveOrUpdate(y9Menu);
        if (null != y9MenuJsonModel.getSubOperationList() && !y9MenuJsonModel.getSubOperationList().isEmpty()) {
            importOperationList(y9MenuJsonModel.getSubOperationList(), systemId);
        }
        if (null != y9MenuJsonModel.getSubMenuList() && !y9MenuJsonModel.getSubMenuList().isEmpty()) {
            importMenuList(y9MenuJsonModel.getSubMenuList(), systemId);
        }
    }

    private void importMenuList(List<Y9MenuJsonModel> y9MenuList, String systemId) {
        for (Y9MenuJsonModel y9MenuJsonModel : y9MenuList) {
            importMenu(y9MenuJsonModel, systemId);
        }
    }

    private void importOperation(Y9OperationJsonModel y9OperationJsonModel, String systemId) {
        Y9Operation y9Operation = Y9ModelConvertUtil.convert(y9OperationJsonModel, Y9Operation.class);
        y9Operation.setSystemId(systemId);
        y9OperationService.saveOrUpdate(y9Operation);
        if (null != y9OperationJsonModel.getSubOperationList()
            && !y9OperationJsonModel.getSubOperationList().isEmpty()) {
            importOperationList(y9OperationJsonModel.getSubOperationList(), systemId);
        }
        if (null != y9OperationJsonModel.getSubMenuList() && !y9OperationJsonModel.getSubMenuList().isEmpty()) {
            importMenuList(y9OperationJsonModel.getSubMenuList(), systemId);
        }
    }

    private void importOperationList(List<Y9OperationJsonModel> y9OperationJsonModelList, String systemId) {
        for (Y9OperationJsonModel y9OperationJsonModel : y9OperationJsonModelList) {
            importOperation(y9OperationJsonModel, systemId);
        }
    }

    private void importRole(Y9RoleJsonModel y9RoleJsonModel, String systemId) {
        Y9Role y9Role = Y9ModelConvertUtil.convert(y9RoleJsonModel, Y9Role.class);
        y9RoleService.saveOrUpdate(y9Role);
        if (null != y9RoleJsonModel.getSubRoleList() && !y9RoleJsonModel.getSubRoleList().isEmpty()) {
            importRoleList(y9RoleJsonModel.getSubRoleList(), systemId);
        }
    }

    private void importRoleList(List<Y9RoleJsonModel> y9RoleJsonModelList, String systemId) {
        for (Y9RoleJsonModel y9RoleJsonModel : y9RoleJsonModelList) {
            importRole(y9RoleJsonModel, systemId);
        }
    }
}
