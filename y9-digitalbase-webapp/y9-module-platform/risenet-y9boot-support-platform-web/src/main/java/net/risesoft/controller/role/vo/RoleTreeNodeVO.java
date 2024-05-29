package net.risesoft.controller.role.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.controller.TreeNodeVO;
import net.risesoft.controller.TreeTypeEnum;
import net.risesoft.enums.platform.RoleTypeEnum;
import net.risesoft.y9public.entity.resource.Y9App;
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

    private String appId;

    public static RoleTreeNodeVO convertY9Role(Y9Role y9Role) {
        RoleTreeNodeVO roleTreeNodeVO = new RoleTreeNodeVO();
        roleTreeNodeVO.setId(y9Role.getId());
        roleTreeNodeVO.setAppId(y9Role.getAppId());
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
        roleTreeNodeVO.setParentId(null);
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

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.ROLE;
    }
}
