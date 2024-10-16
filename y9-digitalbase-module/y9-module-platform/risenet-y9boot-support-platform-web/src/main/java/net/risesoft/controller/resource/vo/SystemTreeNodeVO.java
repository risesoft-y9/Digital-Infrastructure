package net.risesoft.controller.resource.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import net.risesoft.controller.TreeNodeVO;
import net.risesoft.controller.TreeTypeEnum;
import net.risesoft.y9public.entity.resource.Y9System;

/**
 * 系统树节点vo
 *
 * @author shidaobang
 * @date 2023/12/21
 */
@Getter
@Setter
public class SystemTreeNodeVO extends TreeNodeVO {

    private static final long serialVersionUID = -7329269439077778947L;

    /** 系统中文名 */
    private String cnName;
    /** 系统id */
    private String systemId;
    /** 租户id */
    private String tenantId;

    public static SystemTreeNodeVO convertY9System(Y9System y9System) {
        SystemTreeNodeVO systemTreeNodeVO = new SystemTreeNodeVO();
        systemTreeNodeVO.setId(y9System.getId());
        systemTreeNodeVO.setName(y9System.getName());
        systemTreeNodeVO.setCnName(y9System.getCnName());
        systemTreeNodeVO.setParentId(null);
        systemTreeNodeVO.setTabIndex(y9System.getTabIndex());
        systemTreeNodeVO.setHasChild(true);
        systemTreeNodeVO.setNodeType("SYSTEM");
        systemTreeNodeVO.setSystemId(y9System.getId());
        systemTreeNodeVO.setTenantId(y9System.getTenantId());
        return systemTreeNodeVO;
    }

    public static List<SystemTreeNodeVO> convertY9SystemList(List<Y9System> y9SystemList) {
        List<SystemTreeNodeVO> roleTreeNodeVOList = new ArrayList<>();
        for (Y9System y9System : y9SystemList) {
            roleTreeNodeVOList.add(convertY9System(y9System));
        }
        return roleTreeNodeVOList;
    }

    @Override
    public TreeTypeEnum getTreeType() {
        return TreeTypeEnum.SYSTEM;
    }
}
