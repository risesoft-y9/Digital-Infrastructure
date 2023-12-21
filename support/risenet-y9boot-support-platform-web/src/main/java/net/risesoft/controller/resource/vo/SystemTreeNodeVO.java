package net.risesoft.controller.resource.vo;

import java.util.ArrayList;
import java.util.List;

import net.risesoft.controller.TreeNodeVO;
import net.risesoft.controller.TreeTypeEnum;
import net.risesoft.y9public.entity.resource.Y9System;

/**
 * 系统树节点vo
 *
 * @author shidaobang
 * @date 2023/12/21
 */
public class SystemTreeNodeVO extends TreeNodeVO {
    
    private static final long serialVersionUID = -7329269439077778947L;

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.SYSTEM;
    }

    public static SystemTreeNodeVO convertY9System(Y9System y9ResourceBase) {
        SystemTreeNodeVO systemTreeNodeVO = new SystemTreeNodeVO();
        systemTreeNodeVO.setId(y9ResourceBase.getId());
        systemTreeNodeVO.setName(y9ResourceBase.getName());
        systemTreeNodeVO.setParentId(null);
        systemTreeNodeVO.setTabIndex(y9ResourceBase.getTabIndex());
        systemTreeNodeVO.setHasChild(true);
        systemTreeNodeVO.setNodeType("SYSTEM");
        return systemTreeNodeVO;
    }

    public static List<SystemTreeNodeVO> convertY9SystemList(List<Y9System> y9SystemList) {
        List<SystemTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9System y9System : y9SystemList) {
            roleTreeNodeVOList.add(convertY9System(y9System));
        }
        return roleTreeNodeVOList;
    }
}
