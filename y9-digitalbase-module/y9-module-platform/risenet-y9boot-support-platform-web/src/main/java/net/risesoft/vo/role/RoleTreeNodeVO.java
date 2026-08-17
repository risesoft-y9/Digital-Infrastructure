package net.risesoft.vo.role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.consts.InitDataConsts;
import net.risesoft.consts.RoleLevelConsts;
import net.risesoft.enums.TreeTypeEnum;
import net.risesoft.enums.platform.RoleLevelEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.enums.platform.TreeNodeType;
import net.risesoft.model.platform.Role;
import net.risesoft.model.platform.System;
import net.risesoft.model.platform.resource.App;
import net.risesoft.model.user.UserInfo;
import net.risesoft.vo.TreeNodeVO;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.service.resource.Y9SystemService;

/**
 * 角色树节点vo
 *
 * @author shidaobang
 * @date 2023/12/21
 */
@Getter
@Setter
public class RoleTreeNodeVO extends TreeNodeVO {

    private static final long serialVersionUID = -447737996123909425L;

    /**
     * 系统id
     */
    private String systemId;

    /**
     * 应用id
     */
    private String appId;

    /**
     * 租户id
     */
    private String tenantId;

    public static RoleTreeNodeVO convertRole(Role role, Y9SystemService y9SystemService) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(role.getId());
        roleTreeNodeVO.setSystemId(role.getSystemId());
        roleTreeNodeVO.setAppId(role.getAppId());
        roleTreeNodeVO.setTenantId(role.getTenantId());
        roleTreeNodeVO.setName(role.getName());
        roleTreeNodeVO.setParentId(role.getParentId());
        roleTreeNodeVO.setTabIndex(role.getTabIndex());
        roleTreeNodeVO.setHasChild(RoleTypeEnum.FOLDER.equals(role.getType()));
        roleTreeNodeVO.setNodeType(role.getType().getValue());
        
        boolean manageable = isRoleManageable(role, y9SystemService);
        roleTreeNodeVO.setManageable(manageable);
        roleTreeNodeVO.setDeletable(manageable);
        return roleTreeNodeVO;
    }
    
    private static boolean isRoleManageable(Role role, Y9SystemService y9SystemService) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        if (userInfo.isOperationSystemManager()) {
            return true;
        }
        if (userInfo.isTenantSystemManager()) {
            if (RoleLevelEnum.PUBLIC.equals(role.getLevel())) {
                return true;
            }
            System system = y9SystemService.getById(role.getSystemId());
            if (Objects.equals(system.getTenantId(), userInfo.getTenantId())) {
                return true;
            }
        }

        return false;
    }

    public static List<RoleTreeNodeVO> convertRoleList(List<Role> roleList, Y9SystemService y9SystemService) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Role role : roleList) {
            roleTreeNodeVOList.add(convertRole(role, y9SystemService));
        }
        return roleTreeNodeVOList;
    }

    public static RoleTreeNodeVO convertApp(App app) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(app.getId());
        roleTreeNodeVO.setSystemId(app.getSystemId());
        roleTreeNodeVO.setAppId(app.getId());
        roleTreeNodeVO.setName(app.getName());
        roleTreeNodeVO.setParentId(app.getSystemId());
        roleTreeNodeVO.setTabIndex(app.getTabIndex());
        roleTreeNodeVO.setHasChild(true);
        roleTreeNodeVO.setNodeType(app.getResourceType().toString());
        roleTreeNodeVO.setManageable(Y9LoginUserHolder.getUserInfo().isSystemManager());
        roleTreeNodeVO.setDeletable(false);
        return roleTreeNodeVO;
    }

    public static List<RoleTreeNodeVO> convertAppList(List<App> appList) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (App app : appList) {
            roleTreeNodeVOList.add(convertApp(app));
        }
        return roleTreeNodeVOList;
    }

    public static RoleTreeNodeVO convertSystem(System system) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(system.getId());
        roleTreeNodeVO.setAppId(null);
        roleTreeNodeVO.setName(system.getCnName());
        roleTreeNodeVO.setParentId(null);
        roleTreeNodeVO.setTabIndex(system.getTabIndex());
        roleTreeNodeVO.setHasChild(true);
        roleTreeNodeVO.setNodeType(TreeNodeType.SYSTEM.toString());
        roleTreeNodeVO.setDeletable(false);
        roleTreeNodeVO.setManageable(false);
        return roleTreeNodeVO;
    }

    public static List<RoleTreeNodeVO> convertSystemList(List<System> systemList) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (System system : systemList) {
            roleTreeNodeVOList.add(convertSystem(system));
        }
        return roleTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.ROLE;
    }
}
