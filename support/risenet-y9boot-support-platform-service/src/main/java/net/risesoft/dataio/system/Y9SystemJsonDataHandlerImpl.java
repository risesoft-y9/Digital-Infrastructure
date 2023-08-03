package net.risesoft.dataio.system;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.dataio.system.model.Y9AppExportModel;
import net.risesoft.dataio.system.model.Y9MenuExportModel;
import net.risesoft.dataio.system.model.Y9OperationExportModel;
import net.risesoft.dataio.system.model.Y9RoleExportModel;
import net.risesoft.dataio.system.model.Y9SystemExportModel;
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

@Service
@RequiredArgsConstructor
public class Y9SystemJsonDataHandlerImpl implements Y9SystemDataHandler {

    private final Y9SystemService y9SystemService;
    private final Y9AppService y9AppService;
    private final Y9MenuService y9MenuService;
    private final Y9OperationService y9OperationService;
    private final Y9RoleService y9RoleService;

    @Override
    public Y9AppExportModel buildApp(String appId) {
        Y9App y9App = y9AppService.findById(appId);
        Y9AppExportModel y9AppExportModel = Y9ModelConvertUtil.convert(y9App, Y9AppExportModel.class);
        y9AppExportModel.setSubMenuList(buildMenuList(y9AppExportModel.getId()));
        y9AppExportModel.setSubOperationList(buildOperationList(y9AppExportModel.getId()));
        y9AppExportModel.setSubRoleList(buildRoleList(y9AppExportModel.getId()));
        return y9AppExportModel;
    }

    @Override
    public Y9SystemExportModel buildSystem(String systemId) {
        Y9System y9System = y9SystemService.getById(systemId);
        Y9SystemExportModel y9SystemExportModel = Y9ModelConvertUtil.convert(y9System, Y9SystemExportModel.class);
        y9SystemExportModel.setAppList(buildAppList(systemId));
        return y9SystemExportModel;
    }

    @Override
    public void importApp(Y9AppExportModel y9AppExportModel) {
        Y9App y9App = Y9ModelConvertUtil.convert(y9AppExportModel, Y9App.class);
        y9AppService.saveOrUpdate(y9App);
        importMenuList(y9AppExportModel.getSubMenuList());
        importOperationList(y9AppExportModel.getSubOperationList());
        importRoleList(y9AppExportModel.getSubRoleList());
    }

    @Override
    public void importSystem(Y9SystemExportModel y9SystemExportModel) {
        Y9System y9System = Y9ModelConvertUtil.convert(y9SystemExportModel, Y9System.class);
        y9SystemService.saveOrUpdate(y9System);
        importAppList(y9SystemExportModel.getAppList());
    }

    private List<Y9AppExportModel> buildAppList(String systemId) {
        List<Y9App> y9AppList = y9AppService.listBySystemId(systemId);
        List<Y9AppExportModel> y9AppExportModelList = Y9ModelConvertUtil.convert(y9AppList, Y9AppExportModel.class);
        for (Y9AppExportModel y9AppExportModel : y9AppExportModelList) {
            y9AppExportModel.setSubMenuList(buildMenuList(y9AppExportModel.getId()));
            y9AppExportModel.setSubOperationList(buildOperationList(y9AppExportModel.getId()));
            y9AppExportModel.setSubRoleList(buildRoleList(y9AppExportModel.getId()));
        }
        return y9AppExportModelList;
    }

    private List<Y9MenuExportModel> buildMenuList(String parentId) {
        List<Y9Menu> y9MenuList = y9MenuService.findByParentId(parentId);
        List<Y9MenuExportModel> y9MenuExportModelList = Y9ModelConvertUtil.convert(y9MenuList, Y9MenuExportModel.class);
        for (Y9MenuExportModel y9MenuExportModel : y9MenuExportModelList) {
            y9MenuExportModel.setSubMenuList(buildMenuList(y9MenuExportModel.getId()));
            y9MenuExportModel.setSubOperationList(buildOperationList(y9MenuExportModel.getId()));
        }
        return y9MenuExportModelList;
    }

    private List<Y9OperationExportModel> buildOperationList(String parentId) {
        List<Y9Operation> y9OperationList = y9OperationService.findByParentId(parentId);
        List<Y9OperationExportModel> y9OperationExportModelList =
            Y9ModelConvertUtil.convert(y9OperationList, Y9OperationExportModel.class);
        for (Y9OperationExportModel y9OperationExportModel : y9OperationExportModelList) {
            y9OperationExportModel.setSubMenuList(buildMenuList(y9OperationExportModel.getId()));
            y9OperationExportModel.setSubOperationList(buildOperationList(y9OperationExportModel.getId()));
        }
        return y9OperationExportModelList;
    }

    private List<Y9RoleExportModel> buildRoleList(String parentId) {
        List<Y9Role> y9RoleList = y9RoleService.listByParentId(parentId);
        List<Y9RoleExportModel> y9RoleExportModelList = Y9ModelConvertUtil.convert(y9RoleList, Y9RoleExportModel.class);
        for (Y9RoleExportModel y9RoleExportModel : y9RoleExportModelList) {
            y9RoleExportModel.setSubRoleList(buildRoleList(y9RoleExportModel.getId()));
        }
        return y9RoleExportModelList;
    }

    private void importAppList(List<Y9AppExportModel> y9AppExportModelList) {
        for (Y9AppExportModel y9AppExportModel : y9AppExportModelList) {
            importApp(y9AppExportModel);
        }
    }

    private void importMenu(Y9MenuExportModel y9MenuExportModel) {
        Y9Menu y9Menu = Y9ModelConvertUtil.convert(y9MenuExportModel, Y9Menu.class);
        y9MenuService.saveOrUpdate(y9Menu);
        importOperationList(y9MenuExportModel.getSubOperationList());
        importMenuList(y9MenuExportModel.getSubMenuList());
    }

    private void importMenuList(List<Y9MenuExportModel> y9MenuList) {
        for (Y9MenuExportModel y9MenuExportModel : y9MenuList) {
            importMenu(y9MenuExportModel);
        }
    }

    private void importOperation(Y9OperationExportModel y9OperationExportModel) {
        Y9Operation y9Operation = Y9ModelConvertUtil.convert(y9OperationExportModel, Y9Operation.class);
        y9OperationService.saveOrUpdate(y9Operation);
        importOperationList(y9OperationExportModel.getSubOperationList());
        importMenuList(y9OperationExportModel.getSubMenuList());
    }

    private void importOperationList(List<Y9OperationExportModel> y9OperationExportModelList) {
        for (Y9OperationExportModel y9OperationExportModel : y9OperationExportModelList) {
            importOperation(y9OperationExportModel);
        }
    }

    private void importRole(Y9RoleExportModel y9RoleExportModel) {
        Y9Role y9Role = Y9ModelConvertUtil.convert(y9RoleExportModel, Y9Role.class);
        y9RoleService.saveOrUpdate(y9Role);
        importRoleList(y9RoleExportModel.getSubRoleList());
    }

    private void importRoleList(List<Y9RoleExportModel> y9RoleExportModelList) {
        for (Y9RoleExportModel y9RoleExportModel : y9RoleExportModelList) {
            importRole(y9RoleExportModel);
        }
    }
}
