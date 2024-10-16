package net.risesoft.controller.role.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.controller.TreeNodeVO;
import net.risesoft.controller.TreeTypeEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.enums.platform.TreeNodeType;
import net.risesoft.y9public.entity.resource.Y9App;
import net.risesoft.y9public.entity.resource.Y9System;
import net.risesoft.y9public.entity.role.Y9Role;

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

    /** 应用id */
    private String appId;
    /** 租户id */
    private String tenantId;

    public static RoleTreeNodeVO convertY9Role(Y9Role y9Role) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(y9Role.getId());
        roleTreeNodeVO.setAppId(y9Role.getAppId());
        roleTreeNodeVO.setTenantId(y9Role.getTenantId());
        roleTreeNodeVO.setName(y9Role.getName());
        roleTreeNodeVO.setParentId(y9Role.getParentId());
        roleTreeNodeVO.setTabIndex(y9Role.getTabIndex());
        roleTreeNodeVO.setHasChild(RoleTypeEnum.FOLDER.equals(y9Role.getType()));
        roleTreeNodeVO.setNodeType(y9Role.getType().getValue());
        return roleTreeNodeVO;
    }

    public static List<RoleTreeNodeVO> convertY9RoleList(List<Y9Role> y9RoleList) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9Role y9Role : y9RoleList) {
            roleTreeNodeVOList.add(convertY9Role(y9Role));
        }
        return roleTreeNodeVOList;
    }

    public static RoleTreeNodeVO convertY9App(Y9App y9App) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(y9App.getId());
        roleTreeNodeVO.setAppId(y9App.getId());
        roleTreeNodeVO.setName(y9App.getName());
        roleTreeNodeVO.setParentId(y9App.getSystemId());
        roleTreeNodeVO.setTabIndex(y9App.getTabIndex());
        roleTreeNodeVO.setHasChild(true);
        roleTreeNodeVO.setNodeType(y9App.getResourceType().toString());
        return roleTreeNodeVO;
    }

    public static List<RoleTreeNodeVO> convertY9AppList(List<Y9App> y9AppList) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9App y9App : y9AppList) {
            roleTreeNodeVOList.add(convertY9App(y9App));
        }
        return roleTreeNodeVOList;
    }

    public static RoleTreeNodeVO convertY9System(Y9System y9System) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(y9System.getId());
        roleTreeNodeVO.setAppId(null);
        roleTreeNodeVO.setName(y9System.getCnName());
        roleTreeNodeVO.setParentId(null);
        roleTreeNodeVO.setTabIndex(y9System.getTabIndex());
        roleTreeNodeVO.setHasChild(true);
        roleTreeNodeVO.setNodeType(TreeNodeType.SYSTEM.toString());
        return roleTreeNodeVO;
    }

    public static List<RoleTreeNodeVO> convertY9SystemList(List<Y9System> y9SystemList) {
        List<RoleTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9System y9System : y9SystemList) {
            roleTreeNodeVOList.add(convertY9System(y9System));
        }
        return roleTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.ROLE;
    }
}
